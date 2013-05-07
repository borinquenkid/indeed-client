package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.PrintAware;

/** @author Shane Witbeck */
public class ConsolePrintPlugin implements PrintPlugin {
   @Override
   public void print(PrintAware printAware) {
      System.out.println(printAware.print());
   }
}
