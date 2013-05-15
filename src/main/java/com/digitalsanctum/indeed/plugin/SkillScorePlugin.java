package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Column;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Align;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/** @author Shane Witbeck */
public class SkillScorePlugin extends SearchPlugin implements ChainedPlugin {

   @Override
   public void execute(Indeed indeed, SearchRequest request, SearchResponse response) {

      Properties props = request.getProperties();

      Iterable<String> includes = getSkills(props, "plugin.skillscore.include");
      Iterable<String> excludes = getSkills(props, "plugin.skillscore.exclude");

      for (Result r : response.results) {

         String text = r.getMeta("job_text").getValue();

         int score = 0;
         if (includes != null) {
            for (String positivePattern : includes) {
               if (positivePattern.length() == 0) continue;
               boolean positivePresent = Pattern
                  .compile(Pattern.quote(positivePattern), Pattern.CASE_INSENSITIVE)
                  .matcher(text)
                  .find();

               if (positivePresent) {
                  score += 1;
               }
            }
         }

         if (excludes != null) {
            for (String negativePattern : excludes) {
               if (negativePattern.length() == 0) continue;
               boolean negativePresent = Pattern
                  .compile(Pattern.quote(negativePattern), Pattern.CASE_INSENSITIVE)
                  .matcher(text)
                  .find();
               if (negativePresent) {
                  score -= 1;
               }
            }
         }

         Meta meta = new Meta(new Column("skill_score", 15, Align.RIGHT), String.valueOf(score), true);
         r.addMeta(meta);
      }
   }

   private Iterable<String> getSkills(Properties props, String key) {
      String excludeProp = props.getProperty(key);
      Iterable<String> excludes = null;
      if (excludeProp != null) {
         excludes = Splitter.on(',').trimResults().split(excludeProp);
      }
      return excludes;
   }

   @Override
   public List<Plugin> dependsOn() {
      return ImmutableList.of((Plugin) new ExtractTextPlugin());
   }
}
