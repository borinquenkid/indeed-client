package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Column;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;

/** @author Shane Witbeck */
public class RemoteJobsPlugin extends SearchChainedPlugin {

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
   public void doExecute(Indeed indeed, SearchRequest searchRequest, SearchResponse searchResponse) {

      List<Result> remoteResults = Lists.newArrayList();

      for (Result r : searchResponse.results) {

         String text = r.getMeta("job_text").getValue();

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
            Meta meta = new Meta(new Column("remote_score", 10), String.valueOf(score), true);
            r.addMeta(meta);
            remoteResults.add(r);
         }

         searchResponse.results = remoteResults;
      }
   }

   @Override
   public List<Plugin> dependsOn() {
      return ImmutableList.of((Plugin) new ExtractTextPlugin());
   }
}
