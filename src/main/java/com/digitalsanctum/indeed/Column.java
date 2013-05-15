package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class Column {

   private String title;
   private int width;
   private Align align = Align.LEFT;

   public Column(String title) {
      this.title = title;
      this.width = title.length();
   }

   public Column(String title, int width) {
      this.title = title;
      this.width = (title.length() > width) ? title.length() + 1 : width;
   }

   public Column(String title, int width, Align align) {
      this(title, width);
      this.align = align;
   }

   public String getTitle() {
      return title;
   }

   public int getWidth() {
      return width;
   }

   public Align justify() {
      return this.align;
   }

   public String getFormat() {
      return "%" + this.align.getFmtString() + width + "s";
   }
}
