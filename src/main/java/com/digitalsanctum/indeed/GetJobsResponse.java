package com.digitalsanctum.indeed;

import com.google.common.base.Strings;
import com.google.common.collect.Tables;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class GetJobsResponse implements PrintAware {

   public List<Result> results;

   protected Header header = new Header();

   @Override
   public String print() {
      StringBuilder sb = new StringBuilder();
      sb.append(header.print(results));
      sb.append(header.horizontalDiv());
      for (Result result : results) {
         sb.append(printResult(result, true));
      }
      return sb.toString();
   }

   public String printResult(Result result, boolean includeSnippet) {
      StringBuilder sb = new StringBuilder();

      String jobTitleTrunc = truncate(result.jobtitle, header.getColumnWidth("job_title"));
      String companyTrunc = truncate(result.company, header.getColumnWidth("company"));

      List<String> columnValues = newArrayList();
      columnValues.add(jobTitleTrunc);
      columnValues.add(companyTrunc);
      columnValues.add(result.formattedLocationFull);
      columnValues.add(result.formattedRelativeTime);
      columnValues.add(result.jobkey);

      if (result.hasMeta()) {
         Map<String, Meta> metaMap = result.getMetaMap();
         for (String metaKey : metaMap.keySet()) {
            columnValues.add(metaMap.get(metaKey).getValue());
         }
      }

      sb.append(new Row(header.getColumns()).print(columnValues));

      if (includeSnippet) {
         sb.append(format("%nDescription:%n%s", result.snippet));
      }
      return sb.toString();
   }

   private String truncate(String str, int maxLength) {
      if (str == null) return "";
      if (str.length() > maxLength) {
         return str.substring(0, maxLength - 3) + "...";
      }
      return str;
   }
}
