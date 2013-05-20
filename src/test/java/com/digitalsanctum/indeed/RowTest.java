package com.digitalsanctum.indeed;

import org.testng.annotations.Test;

/** @author Shane Witbeck */
public class RowTest {

   @Test
   public void createRow() {
      Row r = new Row()
         .addCell(new Column().title("foo"), "col11")
         .addCell(new Column().title("bar"), "col12");

      System.out.println(r.print());
   }
}
