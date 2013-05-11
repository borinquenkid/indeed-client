package com.digitalsanctum.indeed.plugin;

import com.digitalsanctum.indeed.BaseRequest;
import com.digitalsanctum.indeed.Indeed;
import com.digitalsanctum.indeed.PrintAware;
import com.digitalsanctum.indeed.RequestType;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import static com.digitalsanctum.indeed.RequestType.*;

/** @author Shane Witbeck */
public class ConsolePrintPlugin implements Plugin<BaseRequest, Object> {

   @Override
   public Set<RequestType> appliesTo() {
      return ImmutableSet.of(DETAIL, SEARCH);
   }

   @Override
   public void execute(Indeed indeed, BaseRequest request, Object response) {
      if (response instanceof PrintAware) {
         System.out.println(((PrintAware) response).print());
      }
   }
}
