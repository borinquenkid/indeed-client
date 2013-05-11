package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/** @author Shane Witbeck */
public abstract class SearchPlugin implements Plugin<SearchRequest, SearchResponse> {
   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(RequestType.SEARCH);
   }
}
