package com.digitalsanctum.indeed;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class Row {

   private List<Column> columns = newArrayList();

   public Row(List<Column> columns) {
      this.columns = columns;
   }

   public StringBuilder formats() {
      StringBuilder formats = new StringBuilder();
      for (Column column : columns) {
         formats.append(column.getFormat());
      }
      return formats;
   }

   public String header() {
      List<String> titles = newArrayList();
      for (Column column : columns) {
         titles.add(column.getTitle());
      }
      return print(titles);
   }

   public String print(List<String> values) {
      return format(formats().append("%n").toString(), values.toArray());
   }
}
