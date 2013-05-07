package com.digitalsanctum.indeed;

import com.google.common.base.Strings;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class SearchResponse extends GetJobsResponse {

   public String sort;

   public String query;
   public String location;
   public String dupefilter;
   public boolean highlight;
   public String totalResults;
   public String start;
   public String end;
   public String radius;
   public String pageNumber;


   @Override
   public String print() {
      StringBuilder sb = new StringBuilder();
      sb.append(format("%nShowing %s-%s of %s results sorted by %s. [query='%s', location='%s']%n",
         start, end, totalResults, sort, query, location));
      sb.append(Strings.repeat("-", 130)).append("\n");
      sb.append(Result.printHeader());
      sb.append(Strings.repeat("-", 130)).append("\n");
      for (Result result : results) {
         sb.append(result.print(false));
      }
      return sb.toString();
   }
}
