package com.quanql.test.perfutils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.quanql.test.core.utils.Constant;
import com.quanql.test.core.utils.FileUtil;
import com.quanql.test.core.utils.LogUtil;
import com.quanql.test.core.utils.TimeUtil;
import com.quanql.test.perfutils.PerfMonitor.PoolType;
import com.quanql.test.perfutils.data.CpuInfo;
import com.quanql.test.perfutils.data.FlowInfo;
import com.quanql.test.perfutils.data.FrameInfo;
import com.quanql.test.perfutils.data.MemoryInfo;
import com.quanql.test.perfutils.task.CpuTask;
import com.quanql.test.perfutils.task.FlowTask;
import com.quanql.test.perfutils.task.FrameTask;
import com.quanql.test.perfutils.task.MemTask;

public class PerfRecordAtOpt {
	private static PerfRecordAtOpt er;

	String file = Constant.TEST_SUITE_INDEX + Constant.PERF_SUMMARY_FILE;
	private String Sampling_Event_Name = ""; // 采样事件名称

	PerfMonitor pool = null;
	String cpu = null;
	String mem = null;
	String flow = null;
	String frame = null;
	String line = "";

	private PerfRecordAtOpt() {
		pool = new PerfMonitor(PoolType.FIXED, 10);
		FileUtil.write2Csv(file, String.join(",", "SamplingTime", CpuInfo.getPrintTitle(), MemoryInfo.getPrintTitle(), FlowInfo.getPrintTitle(), FrameInfo.getPrintTitle(), "EventName\n"));
	}

	public static PerfRecordAtOpt getInstance() {
		if (er == null) {
			er = new PerfRecordAtOpt();
		}
		return er;
	}

	public void record() {
		record("");
	}
	public void record(String eventDes) {
		this.Sampling_Event_Name = eventDes;
		try {
			long ts = System.currentTimeMillis();
			// when all complete or the timeout(5s) expires, invokeAll() return
			List<Future<String>> results = pool.executor.invokeAll(Arrays.asList(new CpuTask(false), new MemTask(), new FlowTask(), new FrameTask()), 5000, TimeUnit.MILLISECONDS);
			// results.get(0).isDone();
			long t1 = System.currentTimeMillis();
			LogUtil.info("run()_1-耗时ms: " + (t1 - ts));

			cpu = results.get(0).get();
			mem = results.get(1).get();
			flow = results.get(2).get();
			frame = results.get(3).get();
			line = String.join(",", TimeUtil.getCurrentDateTime(4), cpu, mem, flow, frame, this.Sampling_Event_Name);
			FileUtil.write2Csv(file, line + "\n");
			LogUtil.info("run()_2-耗时ms: " + (System.currentTimeMillis() - t1));

			this.Sampling_Event_Name = "";
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void stopRecord() {
		pool.shutdown();
	}

	public void setSamplingEventName(String sampling_Event_Name) {
		Sampling_Event_Name = sampling_Event_Name;
	}

}
