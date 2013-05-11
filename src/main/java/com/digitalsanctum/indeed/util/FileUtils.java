package com.digitalsanctum.indeed.util;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

/** @author Shane Witbeck */
public final class FileUtils {

   public static final String CLASSPATH_PREFIX = "classpath:";

   private FileUtils() {
      throw new AssertionError("Instantiating a utility class");
   }

   public static boolean delete(File f) {
      if (f.isDirectory()) {
         String[] children = f.list();
         for (String aChildren : children) {
            boolean success = delete(new File(f, aChildren));
            if (!success) {
               return false;
            }
         }
      }
      return f.delete();
   }

   public static String getFileContents(String path) throws IOException {
      if (Strings.isNullOrEmpty(path)) {
         return null;
      }
      return path.startsWith(CLASSPATH_PREFIX)
         ? getFileContentsFromClasspath(path.substring(CLASSPATH_PREFIX.length()))
         : getFileContentsFromPath(path);
   }

   public static String getFileContentsFromPath(String path) throws IOException {
      return Files.toString(new File(path), Charsets.UTF_8);
   }

   public static String getFileContentsFromClasspath(String path) throws IOException {
      URL url = Resources.getResource(path);
      return Resources.toString(url, Charsets.UTF_8);
   }

   public static Properties loadProperties(String path) {
      String contents = null;
      try {
         contents = getFileContents(path);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return loadPropertiesFromString(contents);
   }

   public static Properties loadPropertiesFromString(String s) {
      Properties p = new Properties();
      try {
         p.load(new StringReader(s));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return p;
   }
}
