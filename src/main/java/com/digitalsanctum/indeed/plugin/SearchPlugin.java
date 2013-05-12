package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;

/** @author Shane Witbeck */
public abstract class SearchPlugin implements Plugin<SearchRequest, SearchResponse> {
   @Override
   public Class[] appliesTo() {
      return new Class[]{SearchRequest.class};
   }
}
