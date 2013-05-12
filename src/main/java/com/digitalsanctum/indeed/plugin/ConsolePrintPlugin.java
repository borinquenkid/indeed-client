package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.BaseRequest;
import com.digitalsanctum.indeed.GetJobsRequest;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.PrintAware;
import com.digitalsanctum.indeed.Response;
import com.digitalsanctum.indeed.SearchRequest;

/** @author Shane Witbeck */
public class ConsolePrintPlugin implements Plugin<BaseRequest,Response> {

   @Override
   public Class[] appliesTo() {
      return new Class[]{GetJobsRequest.class, SearchRequest.class};
   }

   @Override
   public void execute(Indeed indeed, BaseRequest request, Response response) {
      if (response instanceof PrintAware) {
         System.out.println(((PrintAware) response).print());
      }
   }
}
