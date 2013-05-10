package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class Meta {

   private Column column;
   private String value;

   public Meta(Column column, String value) {
      this.column = column;
      this.value = value;
   }

   public Column getColumn() {
      return column;
   }

   public String getValue() {
      return value;
   }
}
