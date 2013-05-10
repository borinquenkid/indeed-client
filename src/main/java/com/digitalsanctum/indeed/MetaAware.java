package com.digitalsanctum.indeed;

import java.util.Map;

/** @author Shane Witbeck */
public interface MetaAware {

   void addMeta(Meta meta);

   Meta getMeta(String key);

   Map<String, Meta> getMetaMap();
}
