package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.AndroidConstant;
import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.DoubleUtil;
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

  private long lNativeHeap_kB = 0L;
  private long lDalvikHeap_kB = 0L;
  private long lPssTOTAL_kB = 0L;

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
                String.valueOf(DoubleUtil.div(this.getlNativeHeap_kB(), 1024, 4)),
                String.valueOf(DoubleUtil.div(this.getlDalvikHeap_kB(), 1024, 4)),
                String.valueOf(DoubleUtil.div(this.getlPssTOTAL_kB(), 1024, 4))
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
          lNativeHeap_kB = Long.parseLong(array[2]);
        }
        if (str.contains("Dalvik Heap")) {
          array = str.trim().split(AndroidConstant.BLANK_SPLIT);
          lDalvikHeap_kB = Long.parseLong(array[2]);
        }
        if (str.contains("TOTAL")) {
          array = str.trim().split(AndroidConstant.BLANK_SPLIT);
          lPssTOTAL_kB = Long.parseLong(array[1]);
        }
      }
    }

    LogUtil.debug("Native Heap: " + lNativeHeap_kB + " kB");
    LogUtil.debug("Dalvik Heap: " + lDalvikHeap_kB + " kB");
    LogUtil.debug("PSS TOTAL: " + lPssTOTAL_kB + " kB");
  }

  public String getAppPackageName() {
    return appPackageName;
  }

  public void setAppPackageName(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  public long getlNativeHeap_kB() {
    return lNativeHeap_kB;
  }

  public void setlNativeHeap_kB(long lNativeHeap_kB) {
    this.lNativeHeap_kB = lNativeHeap_kB;
  }

  public long getlDalvikHeap_kB() {
    return lDalvikHeap_kB;
  }

  public void setlDalvikHeap_kB(long lDalvikHeap_kB) {
    this.lDalvikHeap_kB = lDalvikHeap_kB;
  }

  public long getlPssTOTAL_kB() {
    return lPssTOTAL_kB;
  }

  public void setlPssTOTAL_kB(long lPssTOTAL_kB) {
    this.lPssTOTAL_kB = lPssTOTAL_kB;
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
