package com.digitalsanctum.indeed;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/** @author Shane Witbeck */
public class Table {

   private List<Row> rows;

   public Table addRow(Row row) {
      if (rows == null) rows = newArrayList();
      this.rows.add(row);
      return this;
   }

   public String printHeader() {
      if (rows == null) return null;
      Row headerRow = null;
      int lastLen = 0;
      for (Row row : rows) {
         if (lastLen == 0 || row.numberOfColumns() > lastLen) {
            headerRow = row;
            lastLen = row.numberOfColumns();
         }
      }
      return headerRow != null ? headerRow.printColumnTitles() : null;
   }

   public String printRows() {
      StringBuilder out = new StringBuilder();
      for (Row row : rows) {
         out.append(row.print()).append("\n");
      }
      return out.toString();
   }

   public String print() {
      StringBuilder out = new StringBuilder();
      return out
         .append(printHeader()).append("\n")
         .append(printRows())
         .toString();
   }
}
