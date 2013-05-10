package com.digitalsanctum.indeed;

/** @author Shane Witbeck */
public class GetJobsRequest extends BaseRequest {

   public String jobKeys;

   public GetJobsRequest(String jobKeys) {
      this.jobKeys = jobKeys;
   }

   @Override
   public RequestType type() {
      return RequestType.DETAIL;
   }
}
