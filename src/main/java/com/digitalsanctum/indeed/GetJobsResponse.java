package com.digitalsanctum.indeed;

import com.google.common.base.Strings;

import java.util.List;

/** @author Shane Witbeck */
public class GetJobsResponse implements PrintAware {

   public List<Result> results;

   @Override
   public String print() {
      StringBuilder sb = new StringBuilder();
      sb.append(Result.printHeader());
      sb.append(Strings.repeat("-", 130)).append("\n");
      for (Result result : results) {
         sb.append(result.print(true));
      }
      return sb.toString();
   }
}
