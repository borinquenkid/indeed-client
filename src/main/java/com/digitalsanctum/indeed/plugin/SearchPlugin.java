package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;

/** @author Shane Witbeck */
public interface SearchPlugin {

   void execute(Indeed indeed, SearchRequest searchRequest, SearchResponse searchResponse);
}
