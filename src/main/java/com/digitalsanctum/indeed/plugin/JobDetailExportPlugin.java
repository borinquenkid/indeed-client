package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Result;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.io.Files;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/** @author Shane Witbeck */
public class JobDetailExportPlugin extends SearchPlugin {

    private static final Logger LOG = Logger.getLogger(JobDetailExportPlugin.class.getSimpleName());

    private String dataDir;

    @Override
    public Class[] appliesTo() {
        return new Class[]{SearchRequest.class};
    }

    @Override
    public void execute(Indeed indeed, SearchRequest req, SearchResponse res) {

        this.dataDir = req.getProperty("plugin.export.data.dir");
        boolean exportHtml = Boolean.valueOf(req.getProperty("plugin.export.html"));
        boolean exportText = Boolean.valueOf(req.getProperty("plugin.export.text"));

        boolean createDataDirSuccess = createDataDir();
        if (!createDataDirSuccess) {
            return;
        }

        for (Result r : res.results) {
            try {

                // play nice with indeed.com
                Thread.sleep(req.getRequestSleepInterval());

                Document doc = Jsoup.connect(r.url)
                    .method(Connection.Method.GET)
                    .get();

                if (exportHtml) {
                    write(r.jobkey + ".html", doc.html());
                }
                if (exportText) {
                    write(r.jobkey + ".txt", doc.text());
                }


            } catch (IOException e) {
                e.printStackTrace();
                LOG.log(Level.SEVERE, format("Error exporting job %s", r.jobkey), e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
