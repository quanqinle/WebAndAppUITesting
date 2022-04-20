package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.AndroidConstant;
import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.LogUtil;

/**
 * CPU信息
 *
 * @author 权芹乐
 */
public class CpuInfo {
  private String appPackageName = "";
  private static String printTitle = "CPU(%)";
  private String printLine = "";

  private String cpu = "";

  // for debug
  public static void main(String[] args) {}

  public CpuInfo() {
    this(AndroidConstant.APP_PACKAGE_NAME);
  }

  public CpuInfo(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  /** 获取最新CPU信息 */
  public void getCurrentCpuData() {
    getCurrentCpuData(this.appPackageName);
  }

  /** 获取最新CPU信息 */
  public void getCurrentCpuData(String appPackageName) {
    getCpuDataByTop(appPackageName);
    setPrintLine(this.cpu);
  }

  /** 获取最新CPU信息，使用dumpsys cpuinfo方式 */
  public void getCpuDataByDumpsys(String appPackageName) {
    // Load: 4.54 / 3.83 / 3.39
    // CPU usage from 24139ms to 19131ms ago:
    // 20% 8049/com.quanql.demo: 16% user + 4.1% kernel / faults: 845 minor
    // 8.3% 299/surfaceflinger: 4.9% user + 3.3% kernel
    // 3.5% 8155/com.quanql.demo:xg_service_v2: 3.3% user + 0.1% kernel /
    // faults: 3599 minor
    // 2.3% 919/system_server: 1.3% user + 0.9% kernel / faults: 71 minor
    String appname = appPackageName;
    String cpucmd = "adb shell dumpsys cpuinfo " + appname;

    String[] array;

    CommandResult cr = ShellUtils.execCommand(cpucmd);
    for (String str : cr.getSuccessMsgArray()) {
      if (str.contains(appname)) {
        array = str.trim().split(AndroidConstant.BLANK_SPLIT);
        // TODO 解析其他字段
        this.cpu = array[0];

        break;
      }
    }
    LogUtil.debug("dumpsys cpuinfo->\n" + cr.getSuccessMsg());
    LogUtil.debug("CPU%= " + this.cpu);
  }

  /**
   * 获取最新CPU信息，使用top方式<br>
   * 注：耗时约3秒，不好用啊！
   *
   * @author 权芹乐
   */
  public void getCpuDataByTop(String appPackageName) {
    // PID PR CPU% S #THR VSS RSS PCY UID Name
    // 29971 4 12% S 90 1759340K 176096K fg u0_a288 com.quanql.demo
    // 30019 1 0% S 26 1554956K 41532K fg u0_a288 com.quanql.demo:cmf
    // 30210 2 0% S 24 1545548K 38088K bg u0_a288
    // com.quanql.demo:loadwebcache
    // 30043 6 0% S 29 1554000K 49568K bg u0_a288
    // com.quanql.demo:pushservice
    // 30079 7 0% S 31 1553904K 48780K bg u0_a288
    // com.quanql.demo:xg_service_v2
    // 30253 4 0% S 1 1084K 324K bg u0_a288
    // /data/data/com.quanql.demo/lib/libtpnsWatchdog.so

    String appname = appPackageName;
    String cpucmd = "adb shell \"top -n 1\"|grep " + appname;

    String[] array;

    CommandResult cr = ShellUtils.execCommand(cpucmd);
    for (String str : cr.getSuccessMsgArray()) {
      // 结尾
      if (str.endsWith(appname)) {
        array = str.trim().split(AndroidConstant.BLANK_SPLIT);

        // 为了方便后续数据分析，记录结果时去除百分号
        this.cpu = array[2].substring(0, array[2].length() - 1);
      }
    }
    LogUtil.debug("top->\n" + cr.getSuccessMsg());
    LogUtil.debug("CPU%= " + this.cpu);
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public static String getPrintTitle() {
    return printTitle;
  }

  public static void setPrintTitle(String printTitle) {
    CpuInfo.printTitle = printTitle;
  }

  public String getPrintLine() {
    return printLine;
  }

  public void setPrintLine(String printLine) {
    this.printLine = printLine;
  }
}
