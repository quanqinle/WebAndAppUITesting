package com.quanql.test.core.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 读取配置文件
 *
 * @author quanqinle
 */
public class ConfigUtil {
  private static ConfigUtil pu;
  private Properties properties = new Properties();
  private static final String CONFIG_FILE = "config.properties";

  /*
   * for testng listener
   */
  private int retryCount = 0;
  private String sourceCodeDir = "src";
  private String sourceCodeEncoding = "UTF-8";
  private static final String RETRYCOUNT = "retrycount";
  private static final String SOURCEDIR = "sourcecodedir";
  private static final String SOURCEENCODING = "sourcecodeencoding";

  private ConfigUtil() {
    loadConfig(CONFIG_FILE);
    parseTestngLinstenerVals(properties);
  }

  public static ConfigUtil getInstance() {
    if (pu == null) {
      pu = new ConfigUtil();
    }
    return pu;
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public String getPropertyValueAsUTF8(String propertyName) {
    try {
      return new String(properties.getProperty(propertyName).getBytes("ISO-8859-1"), "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /** @param file */
  public void loadConfig(String file) {
    try {
      // properties.load(PropertyUtil.class.getResourceAsStream(file));
      properties.load(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      properties = null;
      LogUtil.warn("FileNotFoundException:" + file);
    } catch (IOException e) {
      properties = null;
      LogUtil.warn("IOException:" + file);
    }
  }

  /**
   * 解析testng listener参数
   *
   * @param properties
   */
  private void parseTestngLinstenerVals(Properties properties) {
    if (properties != null) {
      String sRetryCount = null;

      Enumeration<?> en = properties.propertyNames();
      while (en.hasMoreElements()) {
        String key = (String) en.nextElement();
        if (key.toLowerCase().equals(RETRYCOUNT)) {
          sRetryCount = properties.getProperty(key);
        }
        if (key.toLowerCase().equals(SOURCEDIR)) {
          sourceCodeDir = properties.getProperty(key);
        }
        if (key.toLowerCase().equals(SOURCEENCODING)) {
          sourceCodeEncoding = properties.getProperty(key);
        }
      }
      if (sRetryCount != null) {
        sRetryCount = sRetryCount.trim();
        try {
          retryCount = Integer.parseInt(sRetryCount);
        } catch (final NumberFormatException e) {
          throw new NumberFormatException(
              "Parse " + RETRYCOUNT + " [" + sRetryCount + "] from String to Int Exception");
        }
      }
    }
  }

  public int getRetryCount() {
    return retryCount;
  }

  public String getSourceCodeDir() {
    return this.sourceCodeDir;
  }

  public String getSrouceCodeEncoding() {
    return this.sourceCodeEncoding;
  }
}
