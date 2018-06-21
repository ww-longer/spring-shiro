package com.sys.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author jiewai
 * 
 */
public class DateUtils {

	/**
	 * 获取当前时间之前或者未来day天的日期 负数(-1):表示昨天;正数(1):表示明天
	 * @param day
	 * @param fmt
     * @return
     */
	public static Date getSomeday(int day, String fmt) {
		return getSomeday(new Date(), day);
	}

	/**
	 * 获取某个时间戳之前或未来 day 天的日期字符串
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getSomeday(Date date, int day) {
		return getSomeYMDStr(date, 0, 0, day);
	}

	/**
	 * 获取某个时间戳之前或未来 mouth ,day 的日期字符串
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getSomeMouthDay(Date date, int month, int day) {
		return getSomeYMDStr(date, 0, month, day);
	}

	/**
	 * 获取某个时间戳之前或未来 year, mouth , day 的日期字符串
	 * @param date
	 * @param mouth
	 * @return
	 */
	public static Date getSomeYMDStr(Date date, int year, int mouth, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, mouth);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static String dateToString(Date date, String fmt) {
		return new SimpleDateFormat(fmt).format(date);
	}

	/**
	 * 比较两个 字符串时间的大小
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareStrDate(String date1, String date2) {
		return date1.compareTo(date2);
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date strToDate(String str, String fmt) {
		SimpleDateFormat format = new SimpleDateFormat(fmt);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 给传入时间加减天数,得到想要的某一天
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date changeDateDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}

	/**
	 * 通过时间秒毫秒数判断两个时间的间隔(date2 > date1) : > 0
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = 0;
		if (date1 == null && date2 != null) {
			days = 1;
		} else if (date1 != null && date2 != null){
			days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		} else if (date1 != null && date2 == null) {
			days = -1;
		}
		return days;
	}

	/**
	 * date2比date1 大 (date2 > date1)
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) // 同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
				{
					timeDistance += 366;
				} else // 不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1);
		} else // 不同年
		{
			System.out.println("判断day2 - day1 : " + (day2 - day1));
			return day2 - day1;
		}
	}

	public static void main(String[] args) {
		Date str = new Date();
		System.out
				.println(DateUtils.getSomeYMDStr(new Date(), 0, 1, -1));
		System.out
				.println(DateUtils.differentDaysByMillisecond(new Date(), str));
	}
}
