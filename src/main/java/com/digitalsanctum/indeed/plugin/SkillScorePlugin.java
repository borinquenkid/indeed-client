package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/** @author Shane Witbeck */
public class SkillScorePlugin implements Plugin<SearchRequest, SearchResponse> {
   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(RequestType.SEARCH);
   }

   @Override
   public void execute(Indeed indeed, SearchRequest request, SearchResponse response) {
      // todo
   }
}
