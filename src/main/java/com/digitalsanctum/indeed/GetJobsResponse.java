package com.digitalsanctum.indeed;

import com.google.common.base.Strings;
import com.google.common.collect.Tables;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class GetJobsResponse implements PrintAware, Response {

   public List<Result> results;

   @Override
   public String print() {
      return getTable().print();
   }

   protected Table getTable() {
      Table table = new Table();
      for (Result result : results) {
         Row row = getRowFromResult(result);
         table.addRow(row);
      }
      return table;
   }

   private Row getRowFromResult(Result result) {
      Row row = new Row();
      row
         .addCell(new Column().title("job_title").width(50), result.jobtitle)
         .addCell(new Column().title("company"), result.company)
         .addCell(new Column().title("location"), result.formattedLocationFull)
         .addCell(new Column().title("posted"), result.formattedRelativeTime)
         .addCell(new Column().title("job_key").width(20), result.jobkey);

      if (result.hasMeta()) {
         for(Map.Entry<String, Meta> entry : result.getMetaMap().entrySet()) {
            if (entry.getValue().isDisplay()) {
               row.addCell(entry.getValue().getColumn(), entry.getValue().getValue());
            }
         }
      }
      return row;
   }
}