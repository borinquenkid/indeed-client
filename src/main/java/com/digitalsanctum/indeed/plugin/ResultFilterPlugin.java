package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/** @author Shane Witbeck */
public class ResultFilterPlugin extends SearchPlugin implements ChainedPlugin {

   @Override
   public List<Plugin> dependsOn() {
      return ImmutableList.of(new RemoteJobsPlugin(), (Plugin) new SkillScorePlugin());
   }

   @Override
   public void execute(Indeed indeed, SearchRequest request, SearchResponse response) {

      Map<String, String> ruleMap = request.getPropertiesStartingWith("plugin.resultfilter.rules.");
      boolean remoteConf = ruleMap.containsKey("plugin.resultfilter.rules.remote_score");
      int remoteScore = 0;
      if (remoteConf) {
         remoteScore = Integer.valueOf(ruleMap.get("plugin.resultfilter.rules.remote_score"));
      }
      boolean skillConf = ruleMap.containsKey("plugin.resultfilter.rules.skill_score");
      int skillScore = 0;
      if (skillConf) {
         skillScore = Integer.valueOf(ruleMap.get("plugin.resultfilter.rules.skill_score"));
      }

      List<Result> filteredResults = newArrayList();

      for (Result r : response.results) {

         Meta remoteScoreMeta = r.getMeta("remote_score");
         int resultRemoteScore = 0;
         if (remoteScoreMeta != null) {
            resultRemoteScore = Integer.valueOf(r.getMeta("remote_score").getValue());
         }
         Meta skillScoreMeta = r.getMeta("skill_score");
         int resultSkillScore = 0;
         if (skillScoreMeta != null) {
            resultSkillScore = Integer.valueOf(r.getMeta("skill_score").getValue());
         }
         boolean filter = false;

         if (remoteConf && skillConf) {
            if (resultRemoteScore >= remoteScore && resultSkillScore >= skillScore) {
               filter = true;
            }
         } else if (remoteConf) {
            if (resultRemoteScore >= remoteScore) {
               filter = true;
            }
         } else if (skillConf) {
            if (resultSkillScore >= skillScore) {
               filter = true;
            }
         }

         if (filter) {
            filteredResults.add(r);
         }
      }

      if (!filteredResults.isEmpty()) {
         response
            .printMeta(false)
            .results = filteredResults;
         System.out.println("\nShowing filtered results!\n");
      }
   }
}
