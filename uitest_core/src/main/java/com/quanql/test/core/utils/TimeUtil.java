package com.quanql.test.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间函数
 * 
 * @author 权芹乐
 */
public class TimeUtil {

	// for debug
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			System.out.println(getCurrentDateTime(i));
		}
	}

	/**
	 * 获取当前日期时间
	 * 
	 * @author 权芹乐 2013-1-23
	 * @return 当前日期时间，形如20130123150123
	 */
	public static String getCurrentDateTime() {
		return TimeUtil.getCurrentDateTime(0);
	}

	/**
	 * 根据设置格式类型，获取当前日期时间
	 * 
	 * @param type
	 *          返回日期时间形式<br>
	 *          0, 20130123150123<br>
	 *          1, 2013-01-23T15:01:23.357<br>
	 *          2, 20130123<br>
	 *          3, 150123<br>
	 *          4, 15:01:23.456<br>
	 *          默认, 20130123150123<br>
	 * 
	 * @return String 日期时间字符串
	 */
	public static String getCurrentDateTime(int type) {
		String pattern = "yyyyMMddHHmmss";

		switch (type) {
		case 0: // 默认值
			break;
		case 1:
			pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
			break;
		case 2:
			pattern = "yyyyMMdd";
			break;
		case 3:
			pattern = "HHmmss";
			break;
		case 4:
			pattern = "HH:mm:ss.SSS";
			break;
		default: // 默认值
			break;
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.CHINA); // 设置日期格式
		return df.format(new Date());
	}

}
