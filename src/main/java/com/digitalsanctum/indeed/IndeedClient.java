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
import retrofit.http.RetrofitError;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;

/** @author Shane Witbeck */
public class IndeedClient {

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

   static Set<String> PLUGIN_CACHE = newHashSet();

   private SearchResponse search(SearchRequest request) {
      SearchResponse response = null;
      try {
         response = indeed.search(request.publisherId, request.query, request.location,
            request.sort, request.start, request.limit, request.radius, request.from, request.st, request.jt);
         response.sort = request.sort;
      } catch (RetrofitError re) {
         System.err.println("Search Error: " + re.getLocalizedMessage());
         re.printStackTrace();
      }

      if (response == null) return null;

      // process plugins
      for (Plugin plugin : plugins) {
         if (request.isTypeCompatible(plugin.appliesTo())
            && !PLUGIN_CACHE.contains(plugin.getName())) {
            executePluginAndDependencies(request, response, plugin);
         }
      }

      return response;
   }

   private void executePluginAndDependencies(Request request,
                                             Response response,
                                             Plugin plugin) {
      if (plugin instanceof ChainedPlugin) {
         List<Plugin> dependsOn = ((ChainedPlugin) plugin).dependsOn();
         if (!dependsOn.isEmpty()) {
            for (Plugin p : dependsOn) {
               if (!PLUGIN_CACHE.contains(p.getName())) {
                  executePluginAndDependencies(request, response, p);
               }
            }
         }
      }

      String pluginName = plugin.getName();
      if (!PLUGIN_CACHE.contains(pluginName)) {
         plugin.execute(indeed, request, response);
         PLUGIN_CACHE.add(pluginName);
      }
   }

   private GetJobsResponse getJobs(GetJobsRequest request) {
      GetJobsResponse response = indeed.getJobs(this.publisherId, request.jobKeys);

      Set<String> executedPlugins = newHashSet();

      // process plugins
      for (Plugin plugin : plugins) {
         if (request.isTypeCompatible(plugin.appliesTo())) {
            executePluginAndDependencies(request, response, plugin);
         }
      }
      return response;
   }

   public static void main(String... args) {

      Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("indeed")
         .withDescription("a command line client for indeed.com")
         .withDefaultCommand(Help.class)
         .withCommands(Help.class, Detail.class, Open.class, Search.class, PrintVersion.class);

      Cli<Runnable> indeedParser = builder.build();

      try {
         indeedParser.parse(args).run();
      } catch (RuntimeException e) {
         e.printStackTrace();
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
            String propsPath = System.getProperty("user.home") + File.separatorChar + ".indeed" + File.separatorChar + "indeed.properties";
            Properties props = FileUtils.loadProperties(propsPath);
            if (props != null && props.containsKey("publisher.id")) {
               id = props.getProperty("publisher.id");
            }
         }

         if (id == null || id.length() == 0) {
            System.err.println("publisher id not found");
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

   public static enum Version {
      INSTANCE;

      private final String version;

      private Version() {
         this.version = Version.class.getPackage().getSpecificationVersion();
      }

      @Override
      public String toString() {
         return version;
      }
   }

   @Command(name = "version", description = "output the version of indeed and java runtime in use")
   public static class PrintVersion implements Runnable {
      public void run() {
         System.out.println("Indeed " + Version.INSTANCE);
         System.out.println("Java version: " + System.getProperty("java.version"));
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
