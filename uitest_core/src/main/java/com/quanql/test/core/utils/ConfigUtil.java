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

  private static ConfigUtil configUtil;
  private Properties properties = new Properties();
  private static final String CONFIG_FILE = "config.properties";

  /** for testng listener */
  private int retryCount = 0;

  private String sourceCodeDir = "src";
  private String sourceCodeEncoding = "UTF-8";
  private static final String RETRY_COUNT = "retrycount";
  private static final String SOURCE_DIR = "sourcecodedir";
  private static final String SOURCE_ENCODING = "sourcecodeencoding";

  private ConfigUtil() {
    loadConfig(CONFIG_FILE);
    parseTestngListenerVals(properties);
  }

  public static ConfigUtil getInstance() {
    if (configUtil == null) {
      configUtil = new ConfigUtil();
    }
    return configUtil;
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

  /**
   * @param file
   */
  public void loadConfig(String file) {
    try {
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
   * 解析 testng listener 参数
   *
   * @param properties
   */
  private void parseTestngListenerVals(Properties properties) {
    if (properties != null) {
      String sRetryCount = null;

      Enumeration<?> en = properties.propertyNames();
      while (en.hasMoreElements()) {
        String key = (String) en.nextElement();
        if (key.equalsIgnoreCase(RETRY_COUNT)) {
          sRetryCount = properties.getProperty(key);
        }
        if (key.equalsIgnoreCase(SOURCE_DIR)) {
          sourceCodeDir = properties.getProperty(key);
        }
        if (key.equalsIgnoreCase(SOURCE_ENCODING)) {
          sourceCodeEncoding = properties.getProperty(key);
        }
      }
      if (sRetryCount != null) {
        sRetryCount = sRetryCount.trim();
        try {
          retryCount = Integer.parseInt(sRetryCount);
        } catch (final NumberFormatException e) {
          throw new NumberFormatException(
              "Parse " + RETRY_COUNT + " [" + sRetryCount + "] from String to Int Exception");
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

  public String getSourceCodeEncoding() {
    return this.sourceCodeEncoding;
  }
}
