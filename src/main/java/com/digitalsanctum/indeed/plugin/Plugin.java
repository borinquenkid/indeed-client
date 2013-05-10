package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.BaseRequest;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.RequestType;

/** @author Shane Witbeck */
public interface Plugin<R extends BaseRequest,T> {

   public RequestType[] appliesTo();

   public void execute(Indeed indeed, R request, T response);
}
