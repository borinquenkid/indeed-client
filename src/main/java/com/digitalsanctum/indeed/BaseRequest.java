package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public abstract class BaseRequest {

   public abstract RequestType type();

   public boolean isTypeCompatible(RequestType[] types) {
      for (RequestType type : types) {
         if (type == type()) return true;
      }
      return false;
   }
}
