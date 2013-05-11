package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class Meta {

   private Column column;
   private String value;
   private boolean display;

   public Meta(Column column, String value, boolean display) {
      this.column = column;
      this.value = value;
      this.display = display;
   }

   public Column getColumn() {
      return column;
   }

   public String getValue() {
      return value;
   }

   public boolean isDisplay() {
      return display;
   }
}
