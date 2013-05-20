package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Column;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Align;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.digitalsanctum.indeed.util.FileUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/** @author Shane Witbeck */
public class RemoteJobsPlugin extends SearchPlugin implements ChainedPlugin {

   @Override
   public void execute(Indeed indeed, SearchRequest searchRequest, SearchResponse searchResponse) {

      Iterable<String> positive = getPatterns(searchRequest, "plugin.remotejobs.positive.file");
      Iterable<String> negative = getPatterns(searchRequest, "plugin.remotejobs.negative.file");

      for (Result r : searchResponse.results) {

         String text = extractTextFromUrl(r.url);
         int score = 0;
         score = testPatterns(positive, text, score, true, 1);
         score = testPatterns(positive, r.jobtitle, score, true, 2);
         score = testPatterns(negative, text, score, false, 1);

         r.addMeta(new Meta(new Column().title("remote_score").width(15).align(Align.RIGHT), String.valueOf(score), true));
      }
   }

   private Iterable<String> getPatterns(SearchRequest searchRequest, String patternFileProperty) {
      String content = null;
      try {
         content = FileUtils.getFileContentsFromPath(searchRequest.getDataDir() + File.separatorChar + searchRequest.getProperty(patternFileProperty));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return Splitter.on("\n").omitEmptyStrings().trimResults().split(content);
   }

   private int testPatterns(Iterable<String> patterns, String text, int score, boolean add, int additiveScore) {
      for (String pattern : patterns) {
         boolean present = Pattern
            .compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE)
            .matcher(text)
            .find();
         if (present) {
            score = (add ? (score + additiveScore) : (score - additiveScore));
         }
      }
      return score;
   }

   @Override
   public List<Plugin> dependsOn() {
      return ImmutableList.of();
   }
}
