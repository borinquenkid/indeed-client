package com.digitalsanctum.indeed;

import java.util.Set;

/** @author Shane Witbeck */
public abstract class BaseRequest {

   public abstract RequestType type();

   public boolean isTypeCompatible(Set<RequestType> types) {
      for (RequestType type : types) {
         if (type == type()) return true;
      }
      return false;
   }
}
