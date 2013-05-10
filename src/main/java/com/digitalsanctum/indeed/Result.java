package com.digitalsanctum.indeed;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/** @author Shane Witbeck */
public class Result implements MetaAware {

   private Map<String, Meta> metaMap = newHashMap();

   public String jobtitle;
   public String company;
   public String city;
   public String state;
   public String country;
   public String formattedLocation;
   public String source;
   public String date;
   public String snippet;
   public String url;
   public String latitude;
   public String longitude;
   public String jobkey;
   public boolean sponsored;
   public boolean expired;
   public String formattedLocationFull;
   public String formattedRelativeTime;

   @Override
   public void addMeta(Meta meta) {
      this.metaMap.put(meta.getColumn().getTitle(), meta);
   }

   @Override
   public Meta getMeta(String key) {
      return this.metaMap.get(key);
   }

   public Map<String, Meta> getMetaMap() {
      return metaMap;
   }

   public boolean hasMeta() {
      return metaMap != null && !metaMap.isEmpty();
   }

}
