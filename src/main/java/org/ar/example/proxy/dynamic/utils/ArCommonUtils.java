package org.ar.example.proxy.dynamic.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ArCommonUtils {
	
	/**
	 * Ar Dynamic Demo
	 * provide static common utils method
	 * @author ArLandlate
	 */
	
	public static final SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
	public static final String classpath = Thread.currentThread().getClass().getResource("/").getPath();
	
	public static String whatTimeIsIt() {
		return format.format(new Date());
	}
	
	public static class Clock {
		private Clock() {};
		private Date date;
		public void timingStart() {
			date = new Date();
			System.out.println("timing start! the time is now-- " + format.format(date));
		}
		public void timingStop() {
			Date old = date;
			long time = (date = new Date()).getTime() - old.getTime();
			System.out.println("timing stop! the time is now-- " + format.format(date));
			System.out.println("use " + time + " milliseconds in the aggregate");
		}
	}
	
	public static Clock getAClock() {
		return new Clock();
	}
	
}
