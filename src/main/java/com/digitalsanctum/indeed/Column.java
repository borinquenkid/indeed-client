package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class Column {

   private String title;
   private int width;

   public Column(String title) {
      this.title = title;
      this.width = title.length();
   }

   public Column(String title, int width) {
      this.title = title;
      this.width = (title.length() > width) ? title.length() + 1 : width;
   }

   public String getTitle() {
      return title;
   }

   public int getWidth() {
      return width;
   }

   public String getFormat() {
      return "%-" + width + "s";
   }
}
