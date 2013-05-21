package com.digitalsanctum.indeed;

import retrofit.http.GET;
import retrofit.http.Query;

/** @author Shane Witbeck */
public interface Indeed {

   @GET("/apisearch?format=json&v=2")
   SearchResponse search(
      @Query("publisher") String publisher,
      @Query("q") String q,
      @Query("l") String l,
      @Query("sort") String sort,
      @Query("start") int start,
      @Query("limit") int limit,
      @Query("radius") int radius,
      @Query("fromAge") int fromAge,
      @Query("st") String st,
      @Query("jt") String jt
   );

   @GET("/apigetjobs?format=json&v=2")
   GetJobsResponse getJobs(
      @Query("publisher") String publisher,
      @Query("jobkeys") String jobKeys
   );
}
