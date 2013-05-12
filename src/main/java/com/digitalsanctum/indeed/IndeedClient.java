package com.digitalsanctum.indeed;

import com.digitalsanctum.indeed.plugin.ChainedPlugin;
import com.digitalsanctum.indeed.plugin.Plugin;
import com.digitalsanctum.indeed.util.FileUtils;
import io.airlift.command.Arguments;
import io.airlift.command.Cli;
import io.airlift.command.Command;
import io.airlift.command.Help;
import io.airlift.command.Option;
import io.airlift.command.OptionType;
import retrofit.http.RestAdapter;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class IndeedClient {

   private static final Logger LOG = Logger.getLogger(IndeedClient.class.getSimpleName());

   private static final ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class);

   private String publisherId;
   private Indeed indeed;

   public IndeedClient(String publisherId) {
      this.publisherId = publisherId;
      RestAdapter restAdapter = new RestAdapter.Builder()
         .setServer("http://api.indeed.com/ads")
         .build();
      this.indeed = restAdapter.create(Indeed.class);
   }

   private SearchResponse search(SearchRequest request) {
      SearchResponse response = indeed.search(request.publisherId, request.query, request.location,
         request.sort, request.start, request.limit, request.radius, request.from, request.st, request.jt);
      response.sort = request.sort;


      // todo move plugin dependency login here

      Set<String> executedPlugins = newHashSet();

      // process plugins
      for (Plugin plugin : plugins) {
         if (request.isTypeCompatible(plugin.appliesTo())) {

            long start = System.currentTimeMillis();

            // execute dependency plugins
            if (plugin instanceof ChainedPlugin) {
               List<Plugin> dependsOn = ((ChainedPlugin) plugin).dependsOn();
               if (!dependsOn.isEmpty()) {
                  for (Plugin p : dependsOn) {
                     String pluginName = p.getClass().getSimpleName();
                     if (!executedPlugins.contains(pluginName)) {
                        p.execute(indeed, request, response);
                        executedPlugins.add(pluginName);
                     }
                  }
               }
            }

            plugin.execute(this.indeed, request, response);
            LOG.info(format("executed %s plugin in %d ms", plugin.getClass().getSimpleName(), System.currentTimeMillis() - start));
         }
      }

      return response;
   }

   private GetJobsResponse getJobs(GetJobsRequest request) {
      GetJobsResponse response = indeed.getJobs(this.publisherId, request.jobKeys);

      // process plugins
      for (Plugin plugin : plugins) {
         if (request.isTypeCompatible(plugin.appliesTo())) {
            plugin.execute(this.indeed, request, response);
         }
      }
      return response;
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
            String propsPath = System.getProperty("user.home") + File.separatorChar + ".indeed.properties";
            Properties props = FileUtils.loadProperties(propsPath);
            if (props != null && props.containsKey("publisher.id")) {
               id = props.getProperty("publisher.id");
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
         indeedClient.getJobs(new GetJobsRequest(this.jobKeys));
      }
   }

   @Command(name = "open", description = "Open job details in browser")
   public static class Open extends IndeedCommand {
      @Arguments(description = "Job key. This parameter is required.", required = true)
      public String jobKey;

      @Override
      protected void doRun(IndeedClient indeedClient) {
         String url = indeedClient.getJobs(new GetJobsRequest(jobKey)).results.get(0).url;
         System.out.println(format("opening job %s (url=%s) in browser...", jobKey, url));
         BrowserControl.openUrl(url);
      }
   }
}
