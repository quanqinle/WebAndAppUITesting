package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.AndroidConstant;
import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 内存信息
 *
 * @author 权芹乐
 */
public class MemoryInfo {

  private String appPackageName = "";
  private static String printTitle = "Native Heap(mb),Dalvik Heap(mb),PSS TOTAL(mb)";
  private String printLine = "";

  private long lNativeHeapKB = 0L;
  private long lDalvikHeapKB = 0L;
  private long lPssTotalKB = 0L;

  public MemoryInfo() {
    this(AndroidConstant.APP_PACKAGE_NAME);
  }

  public MemoryInfo(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  public void getCurrentMemData() {
    if (!appPackageName.isEmpty()) {
      getCurrentMemData(this.appPackageName);
      this.printLine =
          StringUtils.join(
              new String[] {
                String.format("%.4f", (this.getLNativeHeapKB() / 1024D)),
                String.format("%.4f", (this.getLDalvikHeapKB() / 1024D)),
                String.format("%.4f", (this.getLPssTotalKB() / 1024D))
              },
              ",");
    }
  }

  public void getCurrentMemData(String appPackageName) {
    String[] array;
    // String[][] table;
    // int iTableRow = 0; // talbe行
    // int iTableColumn = 0; // table列

    boolean isInTable = false;

    String memcmd = "adb shell dumpsys meminfo " + this.appPackageName;

    CommandResult cr = ShellUtils.execCommand(memcmd);
    LogUtil.debug(cr.getSuccessMsg());
    for (String str : cr.getSuccessMsgArray()) {
      if (str.contains("** MEMINFO in pid")) {
        isInTable = true;
      }
      if (isInTable && str.startsWith("Objects")) {
        isInTable = false;
        break;
      }
      if (isInTable) {
        if (str.contains("Native Heap")) {
          array = str.trim().split(AndroidConstant.BLANK_SPLIT);
          lNativeHeapKB = Long.parseLong(array[2]);
        }
        if (str.contains("Dalvik Heap")) {
          array = str.trim().split(AndroidConstant.BLANK_SPLIT);
          lDalvikHeapKB = Long.parseLong(array[2]);
        }
        if (str.contains("TOTAL")) {
          array = str.trim().split(AndroidConstant.BLANK_SPLIT);
          lPssTotalKB = Long.parseLong(array[1]);
        }
      }
    }

    LogUtil.debug("Native Heap: " + lNativeHeapKB + " kB");
    LogUtil.debug("Dalvik Heap: " + lDalvikHeapKB + " kB");
    LogUtil.debug("PSS TOTAL: " + lPssTotalKB + " kB");
  }

  public String getAppPackageName() {
    return appPackageName;
  }

  public void setAppPackageName(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  public long getLNativeHeapKB() {
    return lNativeHeapKB;
  }

  public void setLNativeHeapKB(long lNativeHeapKB) {
    this.lNativeHeapKB = lNativeHeapKB;
  }

  public long getLDalvikHeapKB() {
    return lDalvikHeapKB;
  }

  public void setLDalvikHeapKB(long lDalvikHeapKB) {
    this.lDalvikHeapKB = lDalvikHeapKB;
  }

  public long getLPssTotalKB() {
    return lPssTotalKB;
  }

  public void setLPssTotalKB(long lPssTotalKB) {
    this.lPssTotalKB = lPssTotalKB;
  }

  public static String getPrintTitle() {
    return printTitle;
  }

  public void setPrintTitle(String printTitle) {
    MemoryInfo.printTitle = printTitle;
  }

  public String getPrintLine() {
    return printLine;
  }

  public void setPrintLine(String printLine) {
    this.printLine = printLine;
  }
}
