package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Column;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class ExtractTextPlugin implements Plugin<SearchRequest, SearchResponse> {

   private static final Logger LOG = Logger.getLogger(ExtractTextPlugin.class.getSimpleName());

   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(RequestType.SEARCH);
   }

   @Override
   public void execute(Indeed indeed, SearchRequest request, SearchResponse response) {
      for (Result r : response.results) {

         try {
            // be nice to indeed.com
            Thread.sleep(request.getRequestSleepInterval());
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         try {
            Document doc = Jsoup.connect(r.url)
               .method(Connection.Method.GET)
               .get();
            String text = doc.text();
            r.addMeta(new Meta(new Column("job_text", -1), text, false));

         } catch (IOException e) {
            LOG.log(Level.SEVERE, format("Error processing job %s", r.jobkey), e);
         }
      }
   }
}
