package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 图像帧数据 FIXME 改成针对帧集合，而不是单个帧
 *
 * @author 权芹乐
 */
public class FrameInfo {

  private static String printTitle =
      "DrawAvg,PrepareAvg,ProcessAvg,ExecuteAvg,TotalAvg,TotalFrameCnt,DropFrameCnt,DropFrameRate";
  private String printLine = "";

  private double dDraw = 0f;
  private double dPrepare = 0f;
  private double dProcess = 0f;
  private double dExecute = 0f;
  private double dTotal = 0f;
  // TODO 增加该帧绘制时的时间
  /** 是否掉帧 */
  private boolean isJunk = false;

  public FrameInfo() {}

  public FrameInfo(double dDraw, double dPrepare, double dProcess, double dExecute) {
    this.dDraw = dDraw;
    this.dPrepare = dPrepare;
    this.dProcess = dProcess;
    this.dExecute = dExecute;
    this.dTotal = dDraw + dPrepare + dProcess + dExecute;
    this.isJunk = Double.compare(dTotal, 16f) > 0;
  }

  public void getCurrentFrameData() {

    // 二维：帧数
    int iFrameTotal = 0;

    double dDraw = 0f;
    double dPrepare = 0f;
    double dProcess = 0f;
    double dExecute = 0f;
    double dTotal = 0f;

    double dDrawAvg = 0f;
    double dPrepareAvg = 0f;
    double dProcessAvg = 0f;
    double dExecuteAvg = 0f;
    double dTotalAvg = 0f;

    int junkCount = 0;

    boolean isPrintLog = false;
    // 每帧由n列组成
    int columnCount = 0;

    CommandResult cr =
        ShellUtils.execCommand("adb shell dumpsys gfxinfo " + Constant.APP_PACKAGE_NAME);
    LogUtil.debug(cr.getSuccessMsg());

    for (String str : cr.getSuccessMsgArray()) {
      if (str.contains("View hierarchy:")) {
        isPrintLog = false;
      }
      if (str.contains("Draw\tPrepare\tProcess\tExecute")) {
        isPrintLog = true;
        columnCount = 4;
      } else if (str.contains("Draw\tProcess\tExecute")) {
        isPrintLog = true;
        // Android 4.2 前的系统只有3列数据
        columnCount = 3;
      }

      if (isPrintLog) {
        LogUtil.debug(str);
        String[] array = str.split("\t");

        if (array.length >= 3 && StringUtils.isNumeric(array[0].trim())) {
          dDraw = Double.parseDouble(array[0].trim());
          if (4 == columnCount) {
            dPrepare = Double.parseDouble(array[1].trim());
            dProcess = Double.parseDouble(array[2].trim());
            dExecute = Double.parseDouble(array[3].trim());
          } else if (3 == columnCount) {
            dPrepare = 0f;
            dProcess = Double.parseDouble(array[1].trim());
            dExecute = Double.parseDouble(array[2].trim());
          }

          dTotal = dDraw + dPrepare + dProcess + dExecute;
          if (Double.compare(dTotal, 0f) <= 0) {
            // 过滤0值
            continue;
          }
          junkCount += Double.compare(dTotal, 16.67f) > 0 ? 1 : 0;
          // frames[iSamplingIndex][iFrameNum] = new
          // FrameInfo(dDraw, dPrepare, dProcess, dExecute);
          iFrameTotal++;

          dDrawAvg += dDraw;
          dPrepareAvg += dPrepare;
          dProcessAvg += dProcess;
          dExecuteAvg += dExecute;
          dTotalAvg += dTotal;
        }
      }
    } // parse result end
    if (0 == iFrameTotal) {
      /*this.printLine = dDrawAvg + "," + dPrepareAvg + "," + dProcessAVG + "," + dExecuteAVG + "," + dTotalAVG + ","
      + iFrameTotal + "," + junkCount + "," + 0;*/
      this.printLine = null;
    } else {
      this.printLine =
          dDrawAvg / iFrameTotal
              + ","
              + dPrepareAvg / iFrameTotal
              + ","
              + dProcessAvg / iFrameTotal
              + ","
              + dExecuteAvg / iFrameTotal
              + ","
              + dTotalAvg / iFrameTotal
              + ","
              + iFrameTotal
              + ","
              + junkCount
              + ","
              + String.format("%.4f", ((double) junkCount / iFrameTotal));
    }
  }

  public static String getPrintTitle() {
    return printTitle;
  }

  public String getPrintLine() {
    return printLine;
  }

  public double getDraw() {
    return dDraw;
  }

  public void setDraw(double dDraw) {
    this.dDraw = dDraw;
  }

  public double getPrepare() {
    return dPrepare;
  }

  public void setPrepare(double dPrepare) {
    this.dPrepare = dPrepare;
  }

  public double getProcess() {
    return dProcess;
  }

  public void setProcess(double dProcess) {
    this.dProcess = dProcess;
  }

  public double getExecute() {
    return dExecute;
  }

  public void setExecute(double dExecute) {
    this.dExecute = dExecute;
  }

  public double getTotal() {
    return dTotal;
  }

  public void setTotal(double dTotal) {
    this.dTotal = dTotal;
  }

  public boolean isJunk() {
    return isJunk;
  }

  public void setJunk(boolean isJunk) {
    this.isJunk = isJunk;
  }

  @Override
  public String toString() {
    return dDraw + "," + dPrepare + "," + dProcess + "," + dExecute + "," + dTotal;
  }
}
