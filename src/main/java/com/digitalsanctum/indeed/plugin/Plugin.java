package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Request;
import com.digitalsanctum.indeed.Response;

/** @author Shane Witbeck */
public interface Plugin<R extends Request, T extends Response> {

   public Class[] appliesTo();

   public void execute(Indeed indeed, R request, T response);

   public String getName();
}
