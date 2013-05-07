package com.digitalsanctum.indeed;

import retrofit.http.GET;
import retrofit.http.Name;
import retrofit.http.QueryParam;
import retrofit.http.QueryParams;

/** @author Shane Witbeck */
public interface Indeed {

   static final String FORMAT = "json";
   static final String VERSION = "2";


   @GET("/apisearch")
   @QueryParams({
      @QueryParam(name = "format", value = FORMAT),
      @QueryParam(name = "v", value = VERSION)
   })
   SearchResponse search(
      @Name("publisher") String publisher,
      @Name("q") String q,
      @Name("l") String l,
      @Name("sort") String sort,
      @Name("start") int start,
      @Name("limit") int limit,
      @Name("radius") int radius,
      @Name("fromAge") int fromAge,
      @Name("st") String st,
      @Name("jt") String jt
   );

   @GET("/apigetjobs")
   @QueryParams({
      @QueryParam(name = "format", value = FORMAT),
      @QueryParam(name = "v", value = VERSION)
   })
   GetJobsResponse getJobs(
      @Name("publisher") String publisher,
      @Name("jobkeys") String jobKeys
   );
}
