package com.digitalsanctum.indeed;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import io.airlift.command.Arguments;
import io.airlift.command.Cli;
import io.airlift.command.Command;
import io.airlift.command.Help;
import io.airlift.command.Option;
import io.airlift.command.OptionType;
import retrofit.http.GET;
import retrofit.http.Name;
import retrofit.http.RestAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class IndeedClient {

   private static final Logger LOG = Logger.getLogger(IndeedClient.class.getSimpleName());

   private static final String API_URL = "http://api.indeed.com/ads";
   private static final String FORMAT = "json";
   private static final String VERSION = "2";

   protected final String RESULT_FMT = "%-30s%-30s%-30s%-20s%-20s%n";

   private String publisherId;
   private Indeed indeed;

   public IndeedClient(String publisherId) {
      this.publisherId = publisherId;
      RestAdapter restAdapter = new RestAdapter.Builder()
         .setServer(API_URL)
         .build();
      this.indeed = restAdapter.create(Indeed.class);
   }

   class Result {
      String jobtitle;
      String company;
      String city;
      String state;
      String country;
      String formattedLocation;
      String source;
      String date;
      String snippet;
      String url;
      String latitude;
      String longitude;
      String jobkey;
      boolean sponsored;
      boolean expired;
      String formattedLocationFull;
      String formattedRelativeTime;

      public String print(boolean includeSnippet) {
         StringBuilder sb = new StringBuilder();
         String jobTitleTrunc = truncate(jobtitle, 30);
         String companyTrunc = truncate(company, 30);
         sb.append(format(RESULT_FMT, jobTitleTrunc, companyTrunc, formattedLocationFull, formattedRelativeTime, jobkey));
         if (includeSnippet) {
            sb.append(format("%nDescription:%n%s", snippet));
         }
         return sb.toString();
      }
   }

   class GetJobsResponse {
      List<Result> results;

      public String print() {
         StringBuilder sb = new StringBuilder();
         sb.append(format(RESULT_FMT, "job_title", "company", "location", "posted", "job_key"));
         sb.append(Strings.repeat("-", 130)).append("\n");
         for (Result result : results) {
            sb.append(result.print(true));
         }
         return sb.toString();
      }
   }

   class SearchResponse extends GetJobsResponse {

      String sort;

      String query;
      String location;
      String dupefilter;
      boolean highlight;
      String totalResults;
      String start;
      String end;
      String radius;
      String pageNumber;

      @Override
      public String print() {
         StringBuilder sb = new StringBuilder();
         sb.append(format("%nShowing %s-%s of %s results sorted by %s. [query='%s', location='%s']%n",
            start, end, totalResults, sort, query, location));
         sb.append(Strings.repeat("-", 130)).append("\n");
         sb.append(format(RESULT_FMT, "job_title", "company", "location", "posted", "job_key"));
         sb.append(Strings.repeat("-", 130)).append("\n");
         for (Result result : results) {
            sb.append(result.print(false));
         }
         return sb.toString();
      }
   }

   private static String formatLn(ImmutableList<String> widths, ImmutableMap<String, Collection<String>> headerValues) {
      StringBuilder sb = new StringBuilder();

      // build up format pattern
      StringBuilder widthFmts = new StringBuilder();
      for (String width : widths) {
         widthFmts.append(width);
      }
      widthFmts.append("%n");
      String widthFmtStr = widthFmts.toString();

      // headerLabels
      sb.append(format(widthFmtStr, headerValues.keySet().toArray()));

      // values
      int idx = 0;
      for (Map.Entry<String, Collection<String>> entry : headerValues.entrySet()) {
         String widthPattern = widths.get(idx);
         for (String value : entry.getValue()) {
            sb.append(format(widthPattern, value));
         }
         idx++;
      }
      sb.append("\n");

      return sb.toString();
   }


   interface Indeed {
      @GET("/apisearch")
      SearchResponse search(
         @Name("publisher") String publisher,
         @Name("v") String v,
         @Name("format") String format,
         @Name("q") String q,
         @Name("l") String l,
         @Name("sort") String sort,
         @Name("start") int start,
         @Name("limit") int limit,
         @Name("radius") int radius,
         @Name("fromAge") int fromAge,
         @Name("st") String st,
         @Name("jt") String jt
      );

      @GET("/apigetjobs")
      GetJobsResponse getJobs(
         @Name("publisher") String publisher,
         @Name("v") String v,
         @Name("format") String format,
         @Name("jobkeys") String jobKeys
      );
   }

   private SearchResponse search(String query, String location, String sort, int start, int limit, int radius, int from, String st, String jt) {
      SearchResponse response = indeed.search(publisherId, VERSION, FORMAT, query, location, sort, start, limit, radius, from, st, jt);
      response.sort = sort;
      return response;
   }

   private GetJobsResponse getJobs(String jobKeys) {
      return indeed.getJobs(publisherId, VERSION, FORMAT, jobKeys);
   }

   private static String truncate(String str, int maxLength) {
      if (str == null) return "";
      if (str.length() > maxLength) {
         return str.substring(0, maxLength - 3) + "...";
      }
      return str;
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
            start = (page-1) * limit;
         }
         System.out.println(indeedClient.search(query, location, sort, start, limit, radius, from, st, jt).print());
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
