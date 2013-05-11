package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.GetJobsRequest;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class JobFileExporter implements Plugin<SearchRequest, SearchResponse> {

   private static final Logger LOG = Logger.getLogger(JobFileExporter.class.getSimpleName());

   private static final String DATA_DIR = System.getProperty("user.home") + File.separatorChar + ".indeed-data";


   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(RequestType.SEARCH);
   }

   @Override
   public void execute(Indeed indeed, SearchRequest req, SearchResponse res) {

      boolean createDataDirSuccess = createDataDir();
      if (!createDataDirSuccess) {
         return;
      }

      for (Result r : res.results) {
         try {

            // play nice with indeed.com
            Thread.sleep(1000);

            Document doc = Jsoup.connect(r.url)
               .method(Connection.Method.GET)
               .get();

            write(r.jobkey + ".html", doc.html());
            write(r.jobkey + ".txt", doc.text());

         } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, format("Error exporting job %s", r.jobkey), e);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }


   private boolean createDataDir() {
      File dataDir = new File(DATA_DIR);
      if (dataDir.exists()) {
         return true;
      }
      System.out.println("creating data directory: " + dataDir.getAbsolutePath());
      boolean createdDir = false;
      try {
         Files.createParentDirs(dataDir);
         createdDir = dataDir.mkdir();
      } catch (IOException ioe) {
         System.err.println("Error creating data directory: " + dataDir.getAbsolutePath());
      }
      return createdDir;
   }

   private void write(String filename, String jobText) throws IOException {
      String fp = DATA_DIR + File.separatorChar + filename;
      System.out.println("writing to " + fp);
      Files.write(jobText, new File(fp), Charset.defaultCharset());
   }
}
