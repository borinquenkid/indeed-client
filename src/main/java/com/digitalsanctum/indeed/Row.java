package com.digitalsanctum.indeed;

import com.google.common.base.Joiner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class Row {

   private List<Column> columns = newArrayList();
   private List<String> values = newArrayList();

   public Row addCell(Column column, String value) {
      this.columns.add(column);
      this.values.add(truncate(value, column.getWidth()));
      return this;
   }

   private StringBuilder formats() {
      StringBuilder formats = new StringBuilder();
      for (Column column : columns) {
         formats.append(column.getFormat());
      }
      return formats;
   }

   public int numberOfColumns() {
      return columns.size();
   }

   public String printColumnTitles() {
      List<String> titles = newArrayList();
      for (Column column : columns) {
         titles.add(column.getTitle());
      }
      return format(formats().toString(), titles.toArray());
   }

   public String print() {
      return format(formats().toString(), values.toArray());
   }

   private String truncate(String str, int maxLength) {
      if (str == null) return "";
      if (str.length() > maxLength) {
         return str.substring(0, maxLength - 3) + "...";
      }
      return str;
   }
}
