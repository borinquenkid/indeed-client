package com.digitalsanctum.indeed;

import org.testng.annotations.Test;

/** @author Shane Witbeck */
public class TableTest {

   @Test
   public void createTable() {
      Table t = new Table()
         .addRow(new Row()
            .addCell(new Column().title("title11"), "val11")
            .addCell(new Column().title("title12"), "val12")
         ).addRow(new Row()
            .addCell(new Column().title("title11"), "val21")
         );

      System.out.println(t.print());
   }
}
