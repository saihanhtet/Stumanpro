package com.hanhtet.stumanpro.utils;

import java.util.logging.*;

public class LOG {

  private LOG() {
    throw new IllegalStateException("logger class");
  }

  private static final Logger logger = Logger.getLogger(DATA.APPLICATION_NAME);

  public static void logInfo(String msg) {
    logger.info(msg);
  }

  public static void logInfo(Object msg) {
    logger.info((String) msg);
  }

  public static void logWarn(String msg) {
    logger.warning(msg);
  }

  public static void logMe(Level lvl, String msg, Exception e) {
    logger.log(lvl, msg, e);
  }
}
