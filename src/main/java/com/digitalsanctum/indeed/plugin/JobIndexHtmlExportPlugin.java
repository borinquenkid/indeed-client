package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Align;
import com.digitalsanctum.indeed.Column;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Meta;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.Row;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.digitalsanctum.indeed.Table;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Shane Witbeck */
public class JobIndexHtmlExportPlugin extends SearchPlugin {

   private static final Logger LOG = Logger.getLogger(JobDetailExportPlugin.class.getSimpleName());

   private String dataDir;

   @Override
   public Class[] appliesTo() {
      return new Class[]{SearchRequest.class};
   }

   @Override
   public void execute(Indeed indeed, SearchRequest req, SearchResponse res) {

      this.dataDir = req.getProperty("plugin.export.data.dir");
      boolean createDataDirSuccess = createDataDir();
      if (!createDataDirSuccess) {
         return;
      }
      try {

         for (Result r : res.results) {
            String link = "<a href=\"" + r.url + "\">view</a>";
            r.addMeta(new Meta(new Column().title("view").width(5).align(Align.RIGHT), link, true));
         }
         write("index.html", printHtml(res.getTable()));

      } catch (IOException e) {
         e.printStackTrace();
         LOG.log(Level.SEVERE, "Error exporting job index file", e);
      }
   }

   private String printHtml(Table table) {
      StringBuilder sb = new StringBuilder();
      sb.append("<html><head/><body>");

      Row headerRow = table.getHeaderRow();
      sb.append("<table><tr>");
      for (String headerTitle : headerRow.getColumnTitles()) {
         sb.append("<th>").append(headerTitle).append("</th>");
      }
      sb.append("</tr>");

      List<Row> rows = table.getRows();
      for (Row row : rows) {
         sb.append("<tr>");
         for (String val : row.getValues()) {
            sb.append("<td>").append(val).append("</td>");
         }
         sb.append("<tr>");
      }

      sb.append("</table></body></html>");

      return sb.toString();
   }

   private boolean createDataDir() {
      File dataDir = new File(this.dataDir);
      if (dataDir.exists()) {
         return true;
      }
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
      String fp = this.dataDir + File.separatorChar + filename;
      System.out.println("writing to " + fp);
      Files.write(jobText, new File(fp), Charset.defaultCharset());
   }
}
