package com.muling.mall.bms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muling.common.base.BaseEntity;
import com.muling.common.protocol.SearchRequest;
import com.muling.common.result.Result;
import com.muling.common.util.DateUtils;
import com.muling.common.util.ValidateUtil;
import com.muling.common.web.service.AbstractSearchService;
import com.muling.mall.bms.converter.MarketConverter;
import com.muling.mall.bms.pojo.entity.OmsMarket;
import com.muling.mall.bms.protocol.SearchResponse;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

import static com.muling.common.constant.RedisConstants.HOT_SEARCH_KEYS;


@Service
public class SearchServiceImpl extends AbstractSearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Value("${search.path:/opt/bms/search/index/}")
    private String indexPath;

    @Autowired
    private MarketServiceImpl marketService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MarketConverter marketConverter;

    LocalDateTime lastUpdateTime;

    /**
     * 每分钟跑一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void scheduleCreateIndex() throws IOException {
        createIndex();
    }

    public void recreateIndex() throws IOException {
        logger.info("[SEARCH] Start recreate index when app start");

        cleanOldIndexFile();

        createIndex();

        logger.info("[SEARCH] End recreate index when app start");
    }

    public void createIndex() throws IOException {

        List<OmsMarket> markets = marketService.list(Wrappers.<OmsMarket>lambdaQuery()
                .gt(lastUpdateTime != null, OmsMarket::getUpdated, lastUpdateTime));

        if (ValidateUtil.isNotEmpty(markets)) {
            lastUpdateTime = markets.stream().map(BaseEntity::getUpdated).max(Comparator.comparing(DateUtils::localDateTimeToDate)).get();
        }

        doBuildIndex(markets);
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

        Set<Long> dataIds = new HashSet<>();

        redisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEYS, request.getKey(), 1);
        try {

            searchKey(request, dataIds);
        } catch (Exception e) {
            logger.error("[SEARCH] Search failed", e);
            return Result.success();
        }

        List<OmsMarket> markets = null;

        if (ValidateUtil.isNotEmpty(dataIds)) {
            markets = marketService.list(Wrappers.<OmsMarket>lambdaQuery().in(ValidateUtil.isNotEmpty(dataIds), OmsMarket::getId, dataIds));
        }

        return Result.success(new SearchResponse()
                .setMarkets(marketConverter.po2voList(markets)));
    }

    public String getIndexPath() {
        return indexPath;
    }

    private void doBuildIndex(List<OmsMarket> markets) throws IOException {
        long now = System.currentTimeMillis();
        File file = buildIndexFile();

        Directory dir = FSDirectory.open(file.toPath());

        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));

        if (ValidateUtil.isNotEmpty(markets)) {
            logger.info("[SEARCH] Index data not empty");
            for (OmsMarket item : markets) {
                Document doc = new Document();

                doc.add(new TextField(ID, item.getId().toString(), Field.Store.YES));
                doc.add(new TextField(NAME, item.getName(), Field.Store.YES));
                ;
                writer.addDocument(doc);
            }
        }

        writer.close();
        dir.close();

        logger.info("[SEARCH] End write index, elapsed {} ms", System.currentTimeMillis() - now);
    }


    private void searchKey(SearchRequest request, Set<Long> ids) throws Exception {
        String path = getIndexPath();

        if (!new File(path).exists()) {
            logger.warn("[SEARCH] Index dir not exist,pls check");
            return;
        }

        IndexReader reader = getIndexReader(path);
        try {
            Analyzer analyzer = new StandardAnalyzer();

            Query nameQuery = buildNameQuery(analyzer, request.getKey());

            excQuery(nameQuery, reader, ids, NAME);
        } catch (Exception e) {
            logger.error("[SEARCH] Search key error ", e);
        } finally {
            closeReader(reader);
        }
    }


    public void excQuery(Query query, IndexReader reader, Set<Long> dataIds, String key) {
        try {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, 100);
            if (ValidateUtil.isEmpty(topDocs.scoreDocs)) {
                logger.info("[SEARCH] No result match key {} ", key);
            }
            addResultDocs(reader, dataIds, key, topDocs);
        } catch (Exception e) {
            logger.error("[SEARCH] Exec query error ", e);
        }
    }

    private void addResultDocs(IndexReader reader, Set<Long> dataIds, String key, TopDocs topDocs) throws IOException {
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = reader.document(scoreDoc.doc);

            logger.info(doc.get(ID) + ":" + doc.get(key));

            Set<Long> ids = new HashSet<>();
            dataIds.add(Long.valueOf(doc.get(ID)));
        }
    }
}
