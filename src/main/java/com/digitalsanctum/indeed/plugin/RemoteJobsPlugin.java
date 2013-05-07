package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.Lists;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class RemoteJobsPlugin implements SearchPlugin {

   private static final Logger LOG = Logger.getLogger(JobFileExporter.class.getSimpleName());

   private static final String[] POSITIVE = {
      "remote",
      "telecommute",
      "allows telecommute",
      "this is a remote assignment",
      "you may be located anywhere",
      "candidates can be located anywhere",
      "remote opportunity",
      "location: remote",
      "allows remote",
      "remote position",
      "work from home"
   };

   private static final String[] NEGATIVE = {
      "remote work authorized : no",
      "is this a remote or multiple location position? no",
      "remote sensing",
      "remote deposit",
      "remote access",
      "geographically remote",
      "remote pathways",
      "remote procedure",
      "remote monitoring",
      "experience leading remote",
      "working remotely will be limited"
   };


   @Override
   public void execute(Indeed indeed, SearchRequest searchRequest, SearchResponse searchResponse) {

      List<Result> remoteResults = Lists.newArrayList();

      for (Result r : searchResponse.results) {
         try {
            Document doc = Jsoup.connect(r.url)
               .method(Connection.Method.GET)
               .get();
            String text = doc.text();

            int score = 0;
            for (String positivePattern : POSITIVE) {

               boolean positivePresent = Pattern
                  .compile(Pattern.quote(positivePattern), Pattern.CASE_INSENSITIVE)
                  .matcher(text)
                  .find();

               if (positivePresent) {
                  score += 1;
               }
            }

            for (String negativePattern : NEGATIVE) {
               boolean negativePresent = Pattern
                  .compile(Pattern.quote(negativePattern), Pattern.CASE_INSENSITIVE)
                  .matcher(text)
                  .find();
               if (negativePresent) {
                  score -= 1;
               }
            }

            if (score > 0) {
               remoteResults.add(r);
               System.out.println("score=" + score + " >> " + r.jobtitle + " (" + r.jobkey +")");
            }

         } catch (IOException e) {
            LOG.log(Level.SEVERE, format("Error processing job %s", r.jobkey), e);
         }
      }

      searchResponse.results = remoteResults;
   }

}
