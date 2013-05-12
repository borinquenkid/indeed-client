package com.digitalsanctum.indeed.plugin;

import java.util.List;

/** @author Shane Witbeck */
public interface ChainedPlugin {
   List<Plugin> dependsOn();
}
