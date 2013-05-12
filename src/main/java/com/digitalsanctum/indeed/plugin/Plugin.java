package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.BaseRequest;
import com.digitalsanctum.indeed.GetJobsResponse;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.Request;
import com.digitalsanctum.indeed.RequestType;
import com.digitalsanctum.indeed.Response;

import java.util.List;
import java.util.Set;

/** @author Shane Witbeck */
public interface Plugin<R extends Request, T extends Response> {

   public Set<RequestType> appliesTo();

   public void execute(Indeed indeed, R request, T response);
}
