package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class SearchRequest extends BaseRequest {

   public String publisherId;

   public String query;
   public String location;
   public String sort;
   public int start;
   public int limit;
   public int radius;
   public int from;
   public String st;
   public String jt;


   @Override
   public RequestType type() {
      return RequestType.SEARCH;
   }
}
