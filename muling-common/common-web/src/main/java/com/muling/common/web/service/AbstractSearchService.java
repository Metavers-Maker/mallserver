package com.muling.common.web.service;

import com.muling.common.protocol.SearchRequest;
import com.muling.common.result.Result;
import jodd.io.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Service
public abstract class AbstractSearchService implements ISearchService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSearchService.class);
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TYPE = "type";

    public abstract void recreateIndex() throws IOException;

    public abstract void createIndex() throws IOException;

    /**
     * 关键字搜索
     *
     * @param request
     * @return
     */
    public abstract Result search(SearchRequest request);

    public Set<String> getHotSearchKeys(){
        return null;
    }

    public void cleanOldIndexFile() {

        try {
            FileUtil.deleteDir(getIndexPath());
        } catch (Exception e) {
            logger.error("[SEARCH] Clean index dir failed", e);
        }
    }


    public abstract String getIndexPath();

    public File buildIndexFile() {
        String pathIndex = getIndexPath();

        File file = new File(pathIndex);

        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }



    public Query buildNameQuery(Analyzer analyzer, String value) throws IOException {
        return buildQuery(analyzer, NAME, value);
    }

    private Query buildQuery(Analyzer analyzer, String field, String value) throws IOException {
        // 创建分词器
        TokenStream tokenStream = analyzer.tokenStream(field, new StringReader(value));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        // 构建布尔查询
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            Query query = new WildcardQuery(new Term(field, "*" + term + "*"));
            booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
        }
        tokenStream.end();
        tokenStream.close();

        return  booleanQueryBuilder.build();
    }


    private void addResultDocs(IndexReader reader, Set<Long> dataIds, String key, TopDocs topDocs) throws IOException {
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = reader.document(scoreDoc.doc);

            logger.info("type:" + doc.get(TYPE) + doc.get(ID) + ":" + doc.get(key));

            Set<Long> ids = new HashSet<>();
            dataIds.add(Long.valueOf(doc.get(ID)));
    }
    }

    /***
     * 针对不存在的path路径，FSDirectory.open会默认创建该目录，该场景是异常场景
     */
    public IndexReader getIndexReader(String path) throws Exception {

        FSDirectory fs = FSDirectory.open(new File(path).toPath());

        return DirectoryReader.open(fs);
    }

    public void closeReader(IndexReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            logger.error("[SEARCH] Close reader error ", e);
        }
    }
}
