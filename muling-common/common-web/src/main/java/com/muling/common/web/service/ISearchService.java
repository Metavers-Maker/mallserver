package com.muling.common.web.service;

import com.muling.common.protocol.SearchRequest;
import com.muling.common.result.Result;

import java.io.IOException;
import java.util.Set;

public interface ISearchService {

    void recreateIndex() throws IOException;

    void createIndex()  throws IOException;

    Result search(SearchRequest request);

    Set<String> getHotSearchKeys();
}
