package com.quanql.test.androidperfutils.task;

import com.quanql.test.androidperfutils.data.AppInfo;
import com.quanql.test.androidperfutils.data.CpuInfo;
import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;

/**
 * CPU测试线程任务
 *
 * @author 权芹乐
 */
public class CpuTask implements Callable<String>, Runnable {
  private CpuInfo cpuInfo = null;
  private String sDataLine = null;
  private boolean bWriteFile = false;
  private String file = "-" + AppInfo.getAppVersion() + "-cpu";
  private static String eventName = "";

  public CpuTask() {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(true, getFile());
  }

  public CpuTask(String eventName) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(true, getFile());
    CpuTask.eventName = eventName;
  }

  public CpuTask(boolean bWriteFile) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(bWriteFile, getFile());
  }

  public CpuTask(boolean bWriteFile, String filename) {

    file = StringUtils.stripEnd(getfileName(), ".java") + file;
    initFields(bWriteFile, filename);
  }

  private void initFields(boolean bWriteFile, String filename) {
    this.cpuInfo = new CpuInfo();
    this.bWriteFile = bWriteFile;
    setFile(filename);
    if (bWriteFile) {
      FileUtil.write2Csv(
          getFile(), String.join(",", "SamplingTime", CpuInfo.getPrintTitle(), "EventName\n"));
    }
  }

  @Override
  public String call() throws Exception {
    return getCpuData();
  }

  @Override
  public void run() {
    getCpuData();
  }

  private String getCpuData() {
    LogUtil.info(Thread.currentThread().getName() + " cpu");
    cpuInfo.getCurrentCpuData();
    sDataLine = cpuInfo.getPrintLine();
    if (bWriteFile) {
      FileUtil.write2Csv(
          getFile(), String.join(",", TimeUtil.getCurrentDateTime(4), sDataLine, eventName) + "\n");
    }

    return sDataLine;
  }

  public String getFile() {

    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  /**
   * 获取类名
   *
   * @return
   */
  private String getfileName() {

    StackTraceElement[] temp = Thread.currentThread().getStackTrace();
    for (int i = 3; i < temp.length; i++) {
      if (!"AndroidBaseTest.java".equals(temp[i].getFileName())) {
        return temp[i].getFileName();
      }
    }

    return null;
  }
}
