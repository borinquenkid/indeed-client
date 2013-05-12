package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.BaseRequest;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.PrintAware;
import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.Response;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import static com.digitalsanctum.indeed.RequestType.DETAIL;
import static com.digitalsanctum.indeed.RequestType.SEARCH;

/** @author Shane Witbeck */
public class ConsolePrintPlugin implements Plugin<BaseRequest, Response> {

   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(DETAIL, SEARCH);
   }

   @Override
   public void execute(Indeed indeed, BaseRequest request, Response response) {
      if (response instanceof PrintAware) {
         System.out.println(((PrintAware) response).print());
      }
   }
}
