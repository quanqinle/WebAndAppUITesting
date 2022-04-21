package com.quanql.test.androidperfutils.data;

import com.google.common.base.Strings;
import com.quanql.test.androidperfutils.AndroidConstant;
import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 流量信息
 *
 * @author 权芹乐
 */
public class FlowInfo {
  private static String printTitle = "ReceiveWlan0(mb),TransmitWlan0(mb),Wlan0Total(mb)";
  private String appPackageName;
  private String printLine = "";

  private long lReceiveWlan0Bytes = 0L;
  private long lTransmitWlan0Bytes = 0L;

  public FlowInfo() {
    this(AndroidConstant.APP_PACKAGE_NAME);
  }

  public FlowInfo(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  public static String getPrintTitle() {
    return printTitle;
  }

  public static void setPrintTitle(String printTitle) {
    FlowInfo.printTitle = printTitle;
  }

  public void getCurrentFlowData() {
    if (Strings.isNullOrEmpty(appPackageName)) {
      return;
    }

    getCurrentFlowData(this.appPackageName);
    this.printLine =
        StringUtils.join(
            new String[] {
              String.format("%.4f", (this.getLReceiveWlan0Bytes() / 1024D / 1024D)),
              String.format("%.4f", (this.getLTransmitWlan0Bytes() / 1024D / 1024D)),
              String.format(
                  "%.4f",
                  ((this.getLReceiveWlan0Bytes() + this.getLTransmitWlan0Bytes()) / 1024D / 1024D))
            },
            ",");
  }

  public void getCurrentFlowData(String appPkgName) {
    String[] array;

    AppInfo appInfo = new AppInfo(AndroidConstant.APP_PACKAGE_NAME);
    String pid = appInfo.getPid();
    String cmdline = "adb shell cat /proc/" + pid + "/net/dev";
    CommandResult cr = ShellUtils.execCommand(cmdline);
    for (String str : cr.getSuccessMsgArray()) {
      if (str.contains("wlan0")) {
        LogUtil.debug(str);
        array = str.trim().split(AndroidConstant.BLANK_SPLIT);
        lReceiveWlan0Bytes = Long.parseLong(array[1]);
        lTransmitWlan0Bytes = Long.parseLong(array[9]);

        String join =
            StringUtils.join("接收/发送(bytes)：", lReceiveWlan0Bytes, " / ", lTransmitWlan0Bytes);
        LogUtil.debug(join);
      }
    }
  }

  /**
   * 获取 app 当前 wlan0 通道的累计流量
   *
   * @param appPkgName
   * @author 权芹乐
   */
  public void getFlowWlan0(String appPkgName) {}

  public String getAppPackageName() {
    return appPackageName;
  }

  public void setAppPackageName(String appPackageName) {
    this.appPackageName = appPackageName;
  }

  public String getPrintLine() {
    return printLine;
  }

  public void setPrintLine(String printLine) {
    this.printLine = printLine;
  }

  public long getLReceiveWlan0Bytes() {
    return lReceiveWlan0Bytes;
  }

  public void setLReceiveWlan0Bytes(long lReceiveWlan0Bytes) {
    this.lReceiveWlan0Bytes = lReceiveWlan0Bytes;
  }

  public long getLTransmitWlan0Bytes() {
    return lTransmitWlan0Bytes;
  }

  public void setLTransmitWlan0Bytes(long lTransmitWlan0Bytes) {
    this.lTransmitWlan0Bytes = lTransmitWlan0Bytes;
  }

  /**
   * 结算两次操作间的流量
   *
   * @param firstFlow -
   * @param secondFlow -
   */
  public void countFlow(String firstFlow, String secondFlow) {

    String[] firstTemp = firstFlow.split(",");
    String[] secondTemp = secondFlow.split(",");

    // 计算两次操作间隔的流量
    float receiveFlow = Float.parseFloat(secondTemp[0]) - Float.parseFloat(firstTemp[0]);
    float transmitFlow = Float.parseFloat(secondTemp[1]) - Float.parseFloat(firstTemp[1]);
    float totalFlow = Float.parseFloat(secondTemp[2]) - Float.parseFloat(firstTemp[2]);

    this.printLine = String.format("%.4f, %.4f, %.4f", receiveFlow, transmitFlow, totalFlow);
  }
}
