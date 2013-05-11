package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;

import java.util.List;

/** @author Shane Witbeck */
public abstract class SearchChainedPlugin extends SearchPlugin {

   private boolean executed;

   @Override
   public final void execute(Indeed indeed, SearchRequest request, SearchResponse response) {
      if (!dependsOn().isEmpty()) {
         for (Plugin p : dependsOn()) {
            if (p.isExecuted()) {
               continue;
            }
            p.execute(indeed, request, response);
         }
      }
      doExecute(indeed, request, response);
      executed = true;
   }

   @Override
   public boolean isExecuted() {
      return this.executed;
   }

   public abstract List<Plugin> dependsOn();

   public abstract void doExecute(Indeed indeed, SearchRequest request, SearchResponse response);
}
