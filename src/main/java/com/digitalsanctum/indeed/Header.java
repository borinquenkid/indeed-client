package com.digitalsanctum.indeed;

import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/** @author Shane Witbeck */
public class Header {

   private List<Column> columns = newArrayList();
   private Map<String, Integer> widthMap = newHashMap();

   public Header() {
      addColumn("job_title", 30);
      addColumn("company", 30);
      addColumn("location", 30);
      addColumn("posted", 20);
      addColumn("job_key", 20);
   }

   public int getColumnWidth(String title) {
      return widthMap.get(title);
   }

   public int getTotalWidth() {
      int width = 0;
      for (Column column : columns) {
         width += column.getWidth();
      }
      return width;
   }

   public List<Column> getColumns() {
      return columns;
   }

   public void addColumn(String title, int width) {
      columns.add(new Column(title, width));
      widthMap.put(title, width);
   }

   public String horizontalDiv() {
     return Strings.repeat("-", getTotalWidth()) + "\n";
   }

   public String print(List<Result> results) {

      if (!results.isEmpty()) {
         Map<String, Meta> metaMap = results.get(0).getMetaMap();
         if (metaMap != null && !metaMap.isEmpty()) {
            for (String metaKey : metaMap.keySet()) {
               if (metaMap.get(metaKey).isDisplay()) {
                  addColumn(metaKey, metaMap.get(metaKey).getColumn().getWidth());
               }
            }
         }
      }
      return new Row(columns).header();
   }

}
