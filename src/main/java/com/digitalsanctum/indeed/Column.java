package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class Column {

   private String title = "";
   private int width = 25;
   private Align align = Align.LEFT;


   public Column title(String title) {
      this.title = title;
      return this;
   }

   public Column width(int width) {
      this.width = width;
      return this;
   }

   public Column align(Align align) {
      this.align = align;
      return this;
   }

   public String getTitle() {
      return title;
   }

   public int getWidth() {
      return width;
   }

   public Align getAlign() {
      return this.align;
   }

   public String getFormat() {
      return "%" + this.align.getFmtString() + width + "s";
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder("Column{");
      sb.append("title='").append(title).append('\'');
      sb.append(", width=").append(width);
      sb.append(", align=").append(align);
      sb.append('}');
      return sb.toString();
   }
}
