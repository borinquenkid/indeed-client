package com.digitalsanctum.indeed;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

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
      Row headerRow = getHeaderRow();
      if (headerRow != null) {
         String div = Strings.repeat("-", headerRow.widthInChars());
         StringBuilder out = new StringBuilder();
         out.append(div).append("\n")
            .append(headerRow.printColumnTitles()).append("\n")
            .append(div);

         return out.toString();
      } else {
         return null;
      }
   }

   public List<Row> getRows() {
      return rows;
   }

   public boolean hasRows() {
      return rows != null && !rows.isEmpty();
   }

   public Row getHeaderRow() {
      if (rows == null) return null;
      Row headerRow = null;
      int lastLen = 0;
      for (Row row : rows) {
         if (lastLen == 0 || row.numberOfColumns() > lastLen) {
            headerRow = row;
            lastLen = row.numberOfColumns();
         }
      }
      return headerRow;
   }

   public String printRows() {
      if (rows == null) return null;
      StringBuilder out = new StringBuilder();
      for (Row row : rows) {
         out.append(row.print()).append("\n");
      }
      return out.toString();
   }

   public String print() {
      if (rows == null) return null;
      StringBuilder out = new StringBuilder();
      return out
         .append(printHeader()).append("\n")
         .append(printRows())
         .toString();
   }
}
