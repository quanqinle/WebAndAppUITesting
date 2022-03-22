package com.quanql.test.androidperfutils.data;

import com.quanql.test.androidperfutils.CommandResult;
import com.quanql.test.androidperfutils.ShellUtils;
import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.DoubleUtil;
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
  private boolean isJunk = false; // 是否掉帧

  public FrameInfo() {}

  public FrameInfo(double dDraw, double dPrepare, double dProcess, double dExecute) {
    this.dDraw = dDraw;
    this.dPrepare = dPrepare;
    this.dProcess = dProcess;
    this.dExecute = dExecute;
    this.dTotal = dDraw + dPrepare + dProcess + dExecute;
    this.isJunk = Double.compare(dTotal, 16f) > 0 ? true : false;
  }

  public void getCurrentFrameData() {

    int iFrameTotal = 0; // 二维：帧数

    double dDraw = 0f;
    double dPrepare = 0f;
    double dProcess = 0f;
    double dExecute = 0f;
    double dTotal = 0f;

    double dDrawAVG = 0f;
    double dPrepareAVG = 0f;
    double dProcessAVG = 0f;
    double dExecuteAVG = 0f;
    double dTotalAVG = 0f;

    int junkCount = 0;

    boolean isPrintLog = false;
    int columnCount = 0; // 每帧由n列组成

    CommandResult cr =
        ShellUtils.execCommand("adb shell dumpsys gfxinfo " + Constant.APP_PACKAGE_NAME);
    //		LogUtil.info(cr.getSuccessMsg());

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
            continue; // 过滤0值
          }
          junkCount += Double.compare(dTotal, 16.67f) > 0 ? 1 : 0;
          // frames[iSamplingIndex][iFrameNum] = new
          // FrameInfo(dDraw, dPrepare, dProcess, dExecute);
          iFrameTotal++;

          dDrawAVG += dDraw;
          dPrepareAVG += dPrepare;
          dProcessAVG += dProcess;
          dExecuteAVG += dExecute;
          dTotalAVG += dTotal;
        }
      }
    } // parse result end
    if (0 == iFrameTotal) {
      /*this.printLine = dDrawAVG + "," + dPrepareAVG + "," + dProcessAVG + "," + dExecuteAVG + "," + dTotalAVG + ","
      + iFrameTotal + "," + junkCount + "," + 0;*/
      this.printLine = null;
    } else {
      this.printLine =
          dDrawAVG / iFrameTotal
              + ","
              + dPrepareAVG / iFrameTotal
              + ","
              + dProcessAVG / iFrameTotal
              + ","
              + dExecuteAVG / iFrameTotal
              + ","
              + dTotalAVG / iFrameTotal
              + ","
              + iFrameTotal
              + ","
              + junkCount
              + ","
              + DoubleUtil.div(junkCount, iFrameTotal, 4);
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
