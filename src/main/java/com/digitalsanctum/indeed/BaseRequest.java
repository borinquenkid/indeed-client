package com.digitalsanctum.indeed;

import com.digitalsanctum.indeed.util.FileUtils;

import java.io.File;
import java.util.Properties;
import java.util.Set;

/** @author Shane Witbeck */
public abstract class BaseRequest implements Request {

   public abstract RequestType type();

   public boolean isTypeCompatible(Set<RequestType> types) {
      for (RequestType type : types) {
         if (type == type()) return true;
      }
      return false;
   }

   public Properties getProperties() {
      String propsPath = System.getProperty("user.home") + File.separatorChar + ".indeed.properties";
      return FileUtils.loadProperties(propsPath);
   }
}
