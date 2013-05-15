package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public enum Align {
   LEFT("-"),
   RIGHT("");

   private String fmtString;

   Align(String s) {
      this.fmtString = s;
   }

   public String getFmtString() {
      return fmtString;
   }
}
