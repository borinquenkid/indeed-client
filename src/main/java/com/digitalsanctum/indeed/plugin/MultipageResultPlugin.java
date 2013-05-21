package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;

//import static com.digitalsanctum.indeed.IndeedClient.FORMAT;
//import static com.digitalsanctum.indeed.IndeedClient.VERSION;

/** @author Shane Witbeck */
public class MultipageResultPlugin extends SearchPlugin {

   @Override
   public void execute(Indeed indeed, SearchRequest req, SearchResponse res) {

      if (!res.hasResults()) return;

      // handle more than 25 results at one shot
      try {
         if (res.totalResults > 25 && req.limit > 25) {
            int origStart = req.start;
            int total = 25;

            while (total < req.limit) {

               // be nice to indeed.com
               Thread.sleep(req.getRequestSleepInterval());

               req.start = total + 1;

               SearchResponse tmpResponse = indeed.search(/*FORMAT, VERSION, */req.publisherId, req.query, req.location, req.sort,
                  req.start, req.limit, req.radius, req.from, req.st, req.jt);
               res.results.addAll(tmpResponse.results);

               total += tmpResponse.results.size();
            }

            // reset 'start' and 'end'
            res.start = origStart;
            res.end = total;
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}