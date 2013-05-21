package com.digitalsanctum.indeed;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class SearchResponse extends GetJobsResponse {

   public String sort;

   public String query;
   public String location;
   public String dupefilter;
   public boolean highlight;
   public int totalResults;
   public int start;
   public int end;
   public String radius;
   public String pageNumber;

   boolean printMeta = true;

   public SearchResponse printMeta(boolean printMeta) {
      this.printMeta = printMeta;
      return this;
   }

   @Override
   public String print() {
      StringBuilder sb = new StringBuilder();
      if (printMeta) {
         sb.append(format("%nShowing %s-%s of %d results sorted by %s. [query='%s', location='%s']%n",
            start, end, totalResults, sort, query, location));
      }
      if (hasResults()) {
         sb.append(getTable().print());
      }
      return sb.toString();
   }
}
