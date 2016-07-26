package com.hanvon.rc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @desc 时间处理工具类
 * @author  PengWenCai
 * @time 2015-6-25 下午12:08:21
 * @version
 */
public class TimeUtil
{
	public final static String FORMAT_TODAY = "今天 HH:mm";
	public final static String FORMAT_YESTERDAY = "昨天 HH:mm";
	public final static String FORMAT_THIS_YEAR = "M月d日";
	public final static String FORMAT_OTHER_YEAR = "yyyy-MM-dd";
	public final static String FORMAT_YEAR_MONTH = "yyyy年M月";
	public final static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
	public final static String FORMAT_M = "MM-dd HH:mm:ss";
	public final static String FORMAT_TO_M = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_H_M_S = "HH:mm:ss";

	private final static int SECOND_MILLISECONDS = 1000;
	private final static int YEAR_BASE = 1900;

	// "2011-01-05T15:19:21+00:00" to long
	public static long parseStringTolong(String s) {
		long result = (long) 0;
		String s1 = s.replace("T", " ");
		String s2 = s1.replace("+", " ");
		System.out.println(s2);
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			result = sf1.parse(s2).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取当前时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getcurTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_H_M_S);
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public  static String getCurTimeForMd5()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/*
	 * 获取当前时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getcurTimeYMDHM() {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_TO_M);
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/*
	 * 获取当前日期 年-月-日 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_OTHER_YEAR);
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/*
	 * 获取当前日期 年-月-日 yyyy-MM-dd h：
	 * 
	 * @return
	 */
	public static String getcurTime(String formatString) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatString);
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/*
	 * 使用给定的formatter格式化时间
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static String formatTime(long aSeconds, String aFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(aFormat);
		Date date = new Date();
		date.setTime(aSeconds);
		String formatDate = sdf.format(date);

		return formatDate;
	}

	/*
	 * 使用给定的formatter格式化时间
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static long parseTime(String stringTime) {
		long result = (long) 0;
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_FULL);

		try {
			result = sdf.parse(stringTime).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取年数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getYear(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		int year = date.getYear() + YEAR_BASE;

		return year;
	}

	/*
	 * 获取月数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getMonth(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		int month = date.getMonth() + 1;

		return month;
	}

	/*
	 * 获取日数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getDate(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		return date.getDate();
	}

	/*
	 * 获取小时数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getHour(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		return date.getHours();
	}

	/*
	 * 获取分钟数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getMinute(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		return date.getMinutes();
	}

	/*
	 * 获取秒数字
	 * 
	 * @param aSeconds
	 * @return
	 */
	public static int getSecond(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);

		return date.getSeconds();
	}

	/*
	 * 格式化时间
	 * 
	 * @param aFormat
	 * @param aSeconds
	 * @return
	 */
	public static String getFormattedDateString(long aSeconds) {
		Date date = new Date();
		date.setTime(aSeconds * SECOND_MILLISECONDS);
		String formatter = FORMAT_FULL;
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		return sdf.format(date);
	}

	/*
	 * 获取当前epoch时间
	 * 
	 * @return
	 */
	public static long getNowTime() {
		Date current = new Date();
		return current.getTime();
	}

	/*
	 * 获取当前epoch时间
	 * 
	 * @return
	 */
	public static long getNowTicks() {
		return getNowTime() / SECOND_MILLISECONDS;
	}

	public static String getDayOfWeek(int mWeek) {
		String days[] = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
		String dayOfWeek = "";
		if (mWeek >= 1 && mWeek <= 7) {
			dayOfWeek = days[mWeek - 1];
		} else if (mWeek == 0) {
			dayOfWeek = days[6];
		}
		return dayOfWeek;
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

	/*
	 * 检验时间是否过期。 需要 把设置时间提前两分钟 来做此判断。
	 * 
	 * @param 被设置的时间
	 *            。
	 * @return 真，过期； 假，没有过期。
	 */
	public static boolean isTimeOverdue(Date setDate) {
		Date convertDate = new Date(setDate.getTime() - 2 * 60 * 1000); // 把设置时间提前两分钟来计算。
		Date nowDate = new Date();
		return convertDate.before(nowDate); // 设置时间 比 现在时间 早 ，表示 时间过期。
	}

	public static boolean isTimeOverdue(long setDate) {
		Date convertDate = new Date(setDate - 2 * 60 * 1000); // 把设置时间提前两分钟来计算。
		Date nowDate = new Date();
		return convertDate.before(nowDate);
	}

	/*
	 * 转换从服务器获取时间为long 服务器返回日期类型 2012-03-06T11:45:00+08:00
	 * 2012-03-06T11:45:00+0800
	 */
	public static long parseDate(String time) {

		int index = time.lastIndexOf(":");
		String tt = time.substring(index + 1);
		String dd = time.substring(0, index);
		String date = dd + tt;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtil.i("时间格式转换异常");
			return System.currentTimeMillis();
		}
		return d.getTime();
	}

	public static Date getDate(String timestr)
	{
		SimpleDateFormat formater = new SimpleDateFormat(FORMAT_FULL);
		Date date = null;
		try
		{
			date = formater.parse(timestr);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return date;
	}
}
