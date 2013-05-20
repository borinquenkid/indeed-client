package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.SearchRequest;
import com.digitalsanctum.indeed.SearchResponse;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Shane Witbeck */
public class JobIndexExportPlugin extends SearchPlugin {

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
            write("index.txt", res.print());
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Error exporting job index file", e);
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