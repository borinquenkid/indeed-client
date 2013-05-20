package com.digitalsanctum.indeed;

import com.digitalsanctum.indeed.util.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** @author Shane Witbeck */
public abstract class BaseRequest implements Request {

   private static Properties properties;

   static {
      String propsPath = System.getProperty("user.home") + File.separatorChar + ".indeed" + File.separatorChar + "indeed.properties";
      properties = FileUtils.loadProperties(propsPath);
   }

   public boolean isTypeCompatible(Class[] clazzes) {
      for (Class clazz : clazzes) {
         if (clazz == getClass()) return true;
      }
      return false;
   }

   public Properties getProperties() {
      return properties;
   }

   public Map<String, String> getPropertiesStartingWith(String startingWith) {
      Map<String, String> map = new HashMap<String, String>();
      for (String propName : properties.stringPropertyNames()) {
         if (propName.startsWith(startingWith)) {
            map.put(propName, getProperty(propName));
         }
      }
      return map;
   }

   public String getProperty(String key) {
      return properties.getProperty(key);
   }

   public String getDataDir() {
      return getProperty("data.dir");
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
