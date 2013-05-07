package com.digitalsanctum.indeed;

import com.digitalsanctum.indeed.plugin.PrintPlugin;
import com.digitalsanctum.indeed.plugin.SearchPlugin;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.airlift.command.Arguments;
import io.airlift.command.Cli;
import io.airlift.command.Command;
import io.airlift.command.Help;
import io.airlift.command.Option;
import io.airlift.command.OptionType;
import retrofit.http.RestAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class IndeedClient {

   private static final Logger LOG = Logger.getLogger(IndeedClient.class.getSimpleName());

   private static final String API_URL = "http://api.indeed.com/ads";


   private static ServiceLoader<PrintPlugin> printPlugins = ServiceLoader.load(PrintPlugin.class);
   private static ServiceLoader<SearchPlugin> searchPlugins = ServiceLoader.load(SearchPlugin.class);

   private String publisherId;
   private Indeed indeed;

   public IndeedClient(String publisherId) {
      this.publisherId = publisherId;
      RestAdapter restAdapter = new RestAdapter.Builder()
         .setServer(API_URL)
         .build();
      this.indeed = restAdapter.create(Indeed.class);
   }

   private SearchResponse search(SearchRequest request) {
      SearchResponse response = indeed.search(request.publisherId, request.query, request.location,
         request.sort, request.start, request.limit, request.radius, request.from, request.st, request.jt);
      response.sort = request.sort;

      // process search plugins
      for (SearchPlugin searchPlugin : searchPlugins) {
         searchPlugin.execute(this.indeed, request, response);
      }

      // process print plugins
      for (PrintPlugin printPlugin : printPlugins) {
         printPlugin.print(response);
      }

      return response;
   }

   private GetJobsResponse getJobs(String jobKeys) {
      return indeed.getJobs(publisherId, jobKeys);
   }

   public static void main(String... args) {

      Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("indeed")
         .withDescription("a command line client for indeed.com")
         .withDefaultCommand(Help.class)
         .withCommands(Help.class, Detail.class, Open.class, Search.class);

      Cli<Runnable> indeedParser = builder.build();

      try {
         indeedParser.parse(args).run();
      } catch (RuntimeException e) {
         LOG.log(Level.SEVERE, "Error", e);
         System.exit(1);
      }
      System.exit(0);
   }

   public static abstract class IndeedCommand implements Runnable {

      @Option(type = OptionType.GLOBAL, name = "-id", description = "Your indeed.com publisher id.")
      public String id;

      @Override
      public void run() {

         if (id == null) {
            try {
               File indeedConf = new File(System.getProperty("user.home") + File.separatorChar + ".indeed");
               String fileContents = Files.toString(indeedConf, Charsets.UTF_8);
               if (fileContents != null) {
                  id = fileContents.trim();
               }
            } catch (IOException e) {
               LOG.log(Level.SEVERE, "Error reading ~/.indeed file", e);
            }
         }

         if (id == null || id.length() == 0) {
            LOG.log(Level.SEVERE, "publisher id not found");
            System.exit(1);
         }

         IndeedClient indeedClient = new IndeedClient(id);
         doRun(indeedClient);
      }

      protected abstract void doRun(IndeedClient indeedClient);
   }

   @Command(name = "search", description = "Search for jobs")
   public static class Search extends IndeedCommand {
      @Arguments(description = "Query. By default terms are ANDed.")
      public String query;

      @Option(name = "-loc", description = "Location. Use a postal code or a \"city, state/province/region\" combination.")
      public String location;

      @Option(name = "-sort", description = "Sort by relevance or date. Default is relevance.", allowedValues = {"date", "relevance"})
      public String sort = "relevance";

      @Option(name = "-jt",
         description = "Job type. Allowed values: \"fulltime\", \"parttime\", \"contract\", \"internship\", \"temporary\".",
         allowedValues = {"contract", "fulltime", "parttime", "internship", "temporary"})
      public String jt;

      @Option(name = "-st", description = "Site type. To show only jobs from job boards use 'jobsite'. For jobs from direct employer websites use 'employer'.",
         allowedValues = {"employer", "jobsite"})
      public String st;

      @Option(name = "-from", description = "Number of days back to search.")
      public int from = 0;

      @Option(name = "-start", description = "Start results at this result number, beginning with 0. Default is 0.")
      public int start = 0;

      @Option(name = "-p", description = "Results page number; overrides start. Default is 1.")
      public int page = 1;

      @Option(name = "-limit", description = "Maximum number of results returned per query. Default is 10, maximum allowed is 25.")
      public int limit = 10;

      @Option(name = "-radius", description = "Distance from search location (\"as the crow flies\"). Default is 25.")
      public int radius = 25;


      @Override
      protected void doRun(IndeedClient indeedClient) {
         if (page <= 1) {
            start = 0;
         } else if (page > 1) {
            start = (page - 1) * limit;
         }

         SearchRequest request = new SearchRequest();
         request.publisherId = this.id;
         request.query = query;
         request.location = location;
         request.sort = sort;
         request.start = start;
         request.limit = limit;
         request.radius = radius;
         request.from = from;
         request.st = st;
         request.jt = jt;

         indeedClient.search(request);
      }
   }

   @Command(name = "detail", description = "Details for job(s)")
   public static class Detail extends IndeedCommand {
      @Arguments(description = "Job keys. A comma separated list of job keys specifying the jobs to look up. This parameter is required.", required = true)
      public String jobKeys;

      @Override
      protected void doRun(IndeedClient indeedClient) {
         System.out.println(indeedClient.getJobs(jobKeys).print());
      }
   }

   @Command(name = "open", description = "Open job details in browser")
   public static class Open extends IndeedCommand {
      @Arguments(description = "Job key. This parameter is required.", required = true)
      public String jobKey;

      @Override
      protected void doRun(IndeedClient indeedClient) {
         String url = indeedClient.getJobs(jobKey).results.get(0).url;
         System.out.println(format("opening job %s (url=%s) in browser...", jobKey, url));
         BrowserControl.openUrl(url);
      }
   }
}
