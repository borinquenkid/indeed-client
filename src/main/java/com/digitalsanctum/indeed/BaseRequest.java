package com.digitalsanctum.indeed;

import com.digitalsanctum.indeed.util.FileUtils;

import java.io.File;
import java.util.Properties;
import java.util.Set;

/** @author Shane Witbeck */
public abstract class BaseRequest implements Request {

   public abstract RequestType type();

   private static Properties properties;

   static {
      String propsPath = System.getProperty("user.home") + File.separatorChar + ".indeed.properties";
      properties = FileUtils.loadProperties(propsPath);
   }

   public boolean isTypeCompatible(Set<RequestType> types) {
      for (RequestType type : types) {
         if (type == type()) return true;
      }
      return false;
   }

   public Properties getProperties() {
      return properties;
   }

   public String getProperty(String key) {
      return properties.getProperty(key);
   }

   public int getRequestSleepInterval() {
      String p = getProperty("request.sleep.interval");
      return (p == null) ? 0 : Integer.valueOf(p);
   }

   public boolean debug() {
      String p = getProperty("debug");
      return (p != null) && Boolean.parseBoolean(p);
   }
}
