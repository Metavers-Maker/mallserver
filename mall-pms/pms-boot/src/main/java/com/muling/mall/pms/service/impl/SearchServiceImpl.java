package com.muling.mall.pms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.base.BaseEntity;
import com.muling.common.enums.VisibleEnum;
import com.muling.common.result.Result;
import com.muling.common.util.DateUtils;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.service.AbstractSearchService;
import com.muling.common.web.service.ISearchService;
import com.muling.mall.pms.common.constant.SearchType;
import com.muling.mall.pms.converter.BrandConverter;
import com.muling.mall.pms.converter.SpuConverter;
import com.muling.mall.pms.converter.SubjectConverter;
import com.muling.mall.pms.pojo.dto.SearchResponse;
import com.muling.mall.pms.pojo.entity.PmsBrand;
import com.muling.mall.pms.pojo.entity.PmsSpu;
import com.muling.mall.pms.pojo.entity.PmsSubject;
import com.muling.common.protocol.SearchRequest;
import jodd.io.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.muling.common.constant.RedisConstants.HOT_SEARCH_KEYS;


@Service
public class SearchServiceImpl extends AbstractSearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";

    @Value("${search.path:/search/index/}")
    private String indexPath;

    @Autowired
    private PmsSpuServiceImpl pmsSpuService;

    @Autowired
    private PmsBrandServiceImpl pmsBrandService;

    @Autowired
    private PmsSubjectServiceImpl pmsSubjectService;

    @Autowired
    private SpuConverter spuConverter;

    @Autowired
    private BrandConverter brandConverter;

    @Autowired
    private SubjectConverter subjectConverter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${search.hot.keys.limit:5}")
    private Integer searchHotKeyLimit;

    Map<Integer, LocalDateTime> type2TimeMap = new ConcurrentHashMap<>();

    /**
     * 每分钟跑一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void scheduleCreateIndex() throws IOException {
        createIndex();
    }

    /**
     * 每天1点清理7天前的热门搜索数据
     * */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleCleanHotSearch(){
        // 清理一周前的搜索记录
        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - 7 * 24 * 3600 * 1000;
        redisTemplate.opsForZSet().removeRangeByScore(HOT_SEARCH_KEYS, 0, oneWeekAgo);
    }

    public Set<String> getHotSearchKeys(){
        return redisTemplate.opsForZSet().reverseRange(HOT_SEARCH_KEYS, 0, searchHotKeyLimit - 1);
    }


    public void recreateIndex() throws IOException {
        logger.info("[SEARCH] Start recreate index when app start");

        cleanOldIndexFile();

        createIndex();

        logger.info("[SEARCH] End recreate index when app start");
    }

    public void createIndex() throws IOException {

        LocalDateTime maxSpuUpdateTime = type2TimeMap.get(SearchType.SPU);
        LocalDateTime maxBrandUpdateTime = type2TimeMap.get(SearchType.BRAND);
        LocalDateTime maxSubjectUpdateTime = type2TimeMap.get(SearchType.SUBJECT);

        List<PmsSpu> pmsSpus = pmsSpuService.list(Wrappers.<PmsSpu>lambdaQuery()
                .eq(PmsSpu::getVisible, VisibleEnum.DISPLAY.getValue())
                .gt(maxSpuUpdateTime != null, PmsSpu::getUpdated, maxSpuUpdateTime));

        if(ValidateUtil.isNotEmpty(pmsSpus)){
            maxSpuUpdateTime = pmsSpus.stream().map(BaseEntity::getUpdated).max(Comparator.comparing(DateUtils::localDateTimeToDate)).get();
            type2TimeMap.put(SearchType.SPU, maxSpuUpdateTime);
        }

        List<PmsBrand> pmsBrands = pmsBrandService.list(Wrappers.<PmsBrand>lambdaQuery()
                .eq(PmsBrand::getVisible, VisibleEnum.DISPLAY.getValue())
                .gt(maxBrandUpdateTime != null, PmsBrand::getUpdated, maxBrandUpdateTime));
        if(ValidateUtil.isNotEmpty(pmsSpus)){
            maxBrandUpdateTime = pmsBrands.stream().map(BaseEntity::getUpdated).max(Comparator.comparing(DateUtils::localDateTimeToDate)).get();
            type2TimeMap.put(SearchType.BRAND, maxBrandUpdateTime);
        }

        List<PmsSubject> pmsSubjects = pmsSubjectService.list(Wrappers.<PmsSubject>lambdaQuery()
                .eq(PmsSubject::getVisible, VisibleEnum.DISPLAY.getValue())
                .gt(maxSubjectUpdateTime != null, PmsSubject::getUpdated, maxSubjectUpdateTime));
        if(ValidateUtil.isNotEmpty(pmsSpus)){
            maxSubjectUpdateTime = pmsSubjects.stream().map(BaseEntity::getUpdated).max(Comparator.comparing(DateUtils::localDateTimeToDate)).get();
            type2TimeMap.put(SearchType.SUBJECT, maxSubjectUpdateTime);
        }

        doBuildIndex(pmsSpus, pmsBrands, pmsSubjects);
    }

    /**
     * 关键字搜索
     *
     * @param request
     * @return
     */
    public Result search(SearchRequest request) {

        // 如果只有一个单次，直接返回，不搜索
        if (request.getKey().trim().length() < 2) return Result.success();

        Map<Integer, Set<Long>> type2DataIds = new HashMap<>();

        redisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEYS, request.getKey(), 1);
        try {

            searchKey(request, type2DataIds);
        } catch (Exception e) {
            logger.error("[SEARCH] Search failed", e);
            return Result.success();
        }

        List<PmsSpu> pmsSpus = null;
        List<PmsBrand> pmsBrands = null;
        List<PmsSubject> pmsSubjects = null;

        if (ValidateUtil.isNotEmpty(type2DataIds.get(SearchType.SPU))) {
            pmsSpus = pmsSpuService.list(Wrappers.<PmsSpu>lambdaQuery().in(PmsSpu::getId, type2DataIds.get(SearchType.SPU)));
        }

        if (ValidateUtil.isNotEmpty(type2DataIds.get(SearchType.BRAND))) {
            pmsBrands = pmsBrandService.list(Wrappers.<PmsBrand>lambdaQuery().in(PmsBrand::getId, type2DataIds.get(SearchType.BRAND)));
        }

        if (ValidateUtil.isNotEmpty(type2DataIds.get(SearchType.SUBJECT))) {
            pmsSubjects = pmsSubjectService.list(Wrappers.<PmsSubject>lambdaQuery().in(PmsSubject::getId, type2DataIds.get(SearchType.SUBJECT)));
        }

        return Result.success(new SearchResponse()
                .setGoods(spuConverter.po2voList(pmsSpus))
                .setBrands(brandConverter.brands2vo(pmsBrands))
                .setSubjects(subjectConverter.po2voList(pmsSubjects)));
    }



    public String getIndexPath() {
        return indexPath;
    }

    private void doBuildIndex(List<PmsSpu> pmsSpus, List<PmsBrand> pmsBrands,
                              List<PmsSubject> pmsSubjects) throws IOException {
        long now = System.currentTimeMillis();
        File file = buildIndexFile();

        Directory dir = FSDirectory.open(file.toPath());

        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));

        if (ValidateUtil.isNotEmpty(pmsSpus)) {
            logger.info("[SEARCH] Index spu not empty");
            for (PmsSpu spu : pmsSpus) {
                Document doc = new Document();

                doc.add(new TextField(ID, spu.getId().toString(), Field.Store.YES));
                doc.add(new TextField(NAME, spu.getName(), Field.Store.YES));
                doc.add(new TextField(TYPE, SearchType.SPU.toString(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }
        if (ValidateUtil.isNotEmpty(pmsBrands)) {
            logger.info("[SEARCH] Index brand not empty");
            for (PmsBrand brand : pmsBrands) {
                Document doc = new Document();

                doc.add(new TextField(ID, brand.getId().toString(), Field.Store.YES));
                doc.add(new TextField(NAME, brand.getName(), Field.Store.YES));
                doc.add(new TextField(TYPE, SearchType.BRAND.toString(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }

        if (ValidateUtil.isNotEmpty(pmsSubjects)) {
            logger.info("[SEARCH] Index subject not empty");
            for (PmsSubject subject : pmsSubjects) {
                Document doc = new Document();

                doc.add(new TextField(ID, subject.getId().toString(), Field.Store.YES));
                doc.add(new TextField(NAME, subject.getName(), Field.Store.YES));
                doc.add(new TextField(TYPE, SearchType.SUBJECT.toString(), Field.Store.YES));
                writer.addDocument(doc);
            }
        }

        writer.close();
        dir.close();

        logger.info("[SEARCH] End write index, elapsed {} ms", System.currentTimeMillis() - now);
    }

   private void searchKey(SearchRequest request, Map<Integer, Set<Long>> type2DataIds) throws Exception {
        String path = getIndexPath();

        if (!new File(path).exists()) {
            logger.warn("[SEARCH] Index dir not exist,pls check");
            return;
        }

        IndexReader reader = getIndexReader(path);
        try {
            Analyzer analyzer = new StandardAnalyzer();

            Query nameQuery = buildNameQuery(analyzer, request.getKey());

            excQuery(nameQuery, reader, type2DataIds, NAME);
        } catch (Exception e) {
            logger.error("[SEARCH] Search key error ", e);
        } finally {
            closeReader(reader);
        }
    }

    public void excQuery(Query query, IndexReader reader, Map<Integer, Set<Long>> type2DataIds, String key) {
        try {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, 100);
            if (ValidateUtil.isEmpty(topDocs.scoreDocs)) {
                logger.info("[SEARCH] No result match key {} ", key);
            }
            addResultDocs(reader, type2DataIds, key, topDocs);
        } catch (Exception e) {
            logger.error("[SEARCH] Exec query error ", e);
        }
    }

    private void addResultDocs(IndexReader reader, Map<Integer, Set<Long>> type2DataIds, String key, TopDocs topDocs) throws IOException {
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = reader.document(scoreDoc.doc);

            logger.info("type:" + doc.get(TYPE) + doc.get(ID) + ":" + doc.get(key));

            if (type2DataIds.containsKey(Integer.valueOf(doc.get(TYPE)))) {
                type2DataIds.get(Integer.valueOf(doc.get(TYPE))).add(Long.valueOf(doc.get(ID)));
            } else {
                Set<Long> ids = new HashSet<>();
                ids.add(Long.valueOf(doc.get(ID)));
                type2DataIds.putIfAbsent(Integer.valueOf(doc.get(TYPE)), ids);
            }
        }
    }

}
