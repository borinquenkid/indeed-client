package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

/** @author Shane Witbeck */
public abstract class SearchPlugin implements Plugin<SearchRequest, SearchResponse> {

   final static Map<String, String> cache = newHashMap();


   @Override
   public Class[] appliesTo() {
      return new Class[]{SearchRequest.class};
   }

   public String getName() {
      return getClass().getName();
   }

   protected String extractTextFromUrl(String url) {
      if (cache.containsKey(url)) {
         return cache.get(url);
      }
      String text = null;
      try {

         // play nice
         Thread.sleep(1000);

         Document doc = Jsoup.connect(url)
            .method(Connection.Method.GET)
            .get();
         text = doc.text();
         cache.put(url, text);

      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      return text;
   }
}
