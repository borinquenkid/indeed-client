package com.digitalsanctum.indeed;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/** @author Shane Witbeck */
public class ColumnTest {

   @Test
   public void createColumnTitleOnly() {
      Column c = new Column().title("foo");
      assertEquals(c.getTitle(), "foo");
      assertEquals(c.getAlign(), Align.LEFT);
      assertEquals(c.getWidth(), 25);
      assertEquals(c.getFormat(), "%-25s");

   }

   @Test
   public void createColumnWidthOnly() {
      Column c = new Column().width(15);
      assertEquals(c.getTitle(), "");
      assertEquals(c.getAlign(), Align.LEFT);
      assertEquals(c.getWidth(), 15);
      assertEquals(c.getFormat(), "%-15s");
   }

   @Test
   public void createColumnAlignOnly() {
      Column c = new Column().align(Align.RIGHT);
      assertEquals(c.getTitle(), "");
      assertEquals(c.getAlign(), Align.RIGHT);
      assertEquals(c.getWidth(), 25);
      assertEquals(c.getFormat(), "%25s");
   }
}
