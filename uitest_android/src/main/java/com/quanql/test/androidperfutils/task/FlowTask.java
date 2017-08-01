package com.quanql.test.androidperfutils.task;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.TimeUtil;
import com.quanql.test.androidperfutils.data.AppInfo;
import com.quanql.test.androidperfutils.data.FlowInfo;

/**
 * 流量测试线程任务
 * 
 * @author 权芹乐
 *
 */
public class FlowTask implements Callable<String>, Runnable {
	private FlowInfo flowInfo = null;

	private String sDataLine = null;
	private boolean bWriteFile = false;
	private String file = "-" + AppInfo.getAppVersion() + "-flow";
	private String firstFlow = null;

	public FlowTask() {

		String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
		file = StringUtils.stripEnd(fileName, ".java") + file;
		initFields(true, getFile());
	}

	public FlowTask(boolean bWriteFile) {

		String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
		file = StringUtils.stripEnd(fileName, ".java") + file;
		initFields(bWriteFile, getFile());
	}

	public FlowTask(boolean bWriteFile, String filename) {

		String fileName = Thread.currentThread().getStackTrace()[2].getFileName();
		file = StringUtils.stripEnd(fileName, ".java") + file;
		initFields(bWriteFile, filename);
	}

	private void initFields(boolean bWriteFile, String filename) {
		this.flowInfo = new FlowInfo();
		this.bWriteFile = bWriteFile;
		flowInfo.getCurrentFlowData();
		firstFlow = flowInfo.getPrintLine();
		setFile(filename);
		if (bWriteFile) {
			FileUtil.write2Csv(getFile(), String.join(",", "SamplingTime", FlowInfo.getPrintTitle(), "EventName\n"));
		}
	}

	@Override
	public String call() throws Exception {
		return getFlowData();
	}

	@Override
	public void run() {
		getFlowData();
	}

	private String getFlowData() {
		LogUtil.info(Thread.currentThread().getName() + " flow");
		flowInfo.getCurrentFlowData();
		sDataLine = flowInfo.getPrintLine();
		if (bWriteFile) {
			FileUtil.write2Csv(getFile(), String.join(",", TimeUtil.getCurrentDateTime(4), sDataLine, " ") + "\n");
		}

		return sDataLine;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void writeFlow2Csv() {
		writeFlow2Csv("");
	}
	/**
	 * 写页面加载流量到文件中
	 */
	public void writeFlow2Csv(String tabname) {

		flowInfo.getCurrentFlowData();
		sDataLine = flowInfo.getPrintLine();
		flowInfo.countFlow(firstFlow, sDataLine);
		firstFlow = sDataLine;
		FileUtil.write2Csv(getFile(),
				String.join(",", TimeUtil.getCurrentDateTime(4), flowInfo.getPrintLine(), tabname)+ "\n");

	}

	/**
	 * 重新获取流量
	 */
	public void getfirstFlow() {

		flowInfo.getCurrentFlowData();
		firstFlow = flowInfo.getPrintLine();
	}
}