package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author 权芹乐 */
public class PhoneInfo {
  // getprop
  private String abi;
  private String sdk;
  private String heapgrowthlimit;
  private String heapsize;

  @SuppressWarnings("unused")
  private String manufacturer;

  private static final Pattern p = Pattern.compile("\\:\\s\\[(.*)]$");

  public static void main(String[] args) {
    PhoneInfo phoneInfo = new PhoneInfo();
    LogUtil.info(phoneInfo.getAbi());
    LogUtil.info(phoneInfo.getSdk());
    LogUtil.info(phoneInfo.getHeapgrowthlimit());
    // LogUtil.info(phoneInfo.getHeapsize());
  }

  public PhoneInfo() {
    this.getProp();
  }

  /**
   * 开启飞行模式
   *
   * @author 权芹乐
   */
  public static void setAirplanModeOn() {
    String[] cmdlines = {
      "adb shell settings put global airplane_mode_on 1",
      "adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"
    };
    CommandResult cr = ShellUtils.execCommand(cmdlines[0]);
    LogUtil.debug(cr.getSuccessMsg());
    cr = ShellUtils.execCommand(cmdlines[1]);
    LogUtil.debug(cr.getSuccessMsg());
  }

  /**
   * 开启飞行模式
   *
   * @author 权芹乐
   */
  public static void setAirplanModeOff() {
    String[] cmdlines = {
      "adb shell settings put global airplane_mode_on 0",
      "adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"
    };
    CommandResult cr = ShellUtils.execCommand(cmdlines[0]);
    LogUtil.debug(cr.getSuccessMsg());
    cr = ShellUtils.execCommand(cmdlines[1]);
    LogUtil.debug(cr.getSuccessMsg());
  }

  protected void getProp() {
    CommandResult cr = ShellUtils.execCommand("adb shell getprop");
    for (String str : cr.getSuccessMsgArray()) {
      LogUtil.debug(str);
      if (str.contains("[ro.build.version.sdk]")) {
        sdk = parseGetpropLine(str);
      } else if (str.contains("[ro.product.cpu.abi]")) {
        abi = parseGetpropLine(str);
      } else if (str.contains("[ro.product.manufacturer]")) {
        manufacturer = parseGetpropLine(str);
      } else if (str.contains("[dalvik.vm.heapsize]")) {
        heapsize = parseGetpropLine(str);
      } else if (str.contains("[dalvik.vm.heapgrowthlimit]")) {
        heapgrowthlimit = parseGetpropLine(str);
      }
    }
  }

  private String parseGetpropLine(String line) {

    // p = Pattern.compile("\\:\\s\\[");
    // strs = p.split(line);

    Matcher m = p.matcher(line);
    if (m.find()) {
      return m.group(1).trim();
    } else {
      LogUtil.error("No Match!");
      return null;
    }
  }

  public String getAbi() {
    return abi;
  }

  public String getSdk() {
    return sdk;
  }

  public String getHeapgrowthlimit() {
    return heapgrowthlimit;
  }

  public String getHeapsize() {
    return heapsize;
  }
}
