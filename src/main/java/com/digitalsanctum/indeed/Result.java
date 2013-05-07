package com.digitalsanctum.indeed;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class Result {


   public String jobtitle;
   public String company;
   public String city;
   public String state;
   public String country;
   public String formattedLocation;
   public String source;
   public String date;
   public String snippet;
   public String url;
   public String latitude;
   public String longitude;
   public String jobkey;
   public boolean sponsored;
   public boolean expired;
   public String formattedLocationFull;
   public String formattedRelativeTime;


   public static String printFormat() {
      return "%-30s%-30s%-30s%-20s%-20s%n";
   }

   public static String printHeader() {
      return String.format(printFormat(), "job_title", "company", "location", "posted", "job_key");
   }

   public String print(boolean includeSnippet) {
      StringBuilder sb = new StringBuilder();
      String jobTitleTrunc = truncate(jobtitle, 30);
      String companyTrunc = truncate(company, 30);
      sb.append(String.format(printFormat(), jobTitleTrunc, companyTrunc, formattedLocationFull, formattedRelativeTime, jobkey));
      if (includeSnippet) {
         sb.append(format("%nDescription:%n%s", snippet));
      }
      return sb.toString();
   }

   protected String truncate(String str, int maxLength) {
      if (str == null) return "";
      if (str.length() > maxLength) {
         return str.substring(0, maxLength - 3) + "...";
      }
      return str;
   }
}
