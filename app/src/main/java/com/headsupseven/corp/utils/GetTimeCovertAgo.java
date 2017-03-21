package com.headsupseven.corp.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class GetTimeCovertAgo {

	public GetTimeCovertAgo() {

	}

	public static String getTime(String milisecond, String EndTime) {

		String inputString = "";
		Date date = null;
		long totalday = 0;
		long Endday=0;
		int millisec = 0, sec = 0, min = 0, hour = 0, day = 0, week = 0, month = 0, year = 0;
		int miniute = 60;// 60 second
		int houre = 60 * 60; //
		int dayTime = 60 * 60 * 24;
		int WeakIme = 60 * 60 * 24 * 7;
		int monthTime = 60 * 60 * 24 * 30;
		int yearTime = 60 * 60 * 24 * 30 * 12;

		totalday = Long.parseLong(milisecond);
		Endday = Long.parseLong(EndTime);


		// /===get currentTime by milisecond==================
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		long lDateTime = today.getTime(); // System.currentTimeMillis(); // new
											// Date().getTime();

		if(lDateTime>totalday){
			if (lDateTime>Endday){
//				inputString = "EXPIRED";

			}else{

				inputString = "TODAY";
			}
//			inputString = "EXPIRED";
//			long mainday = (lDateTime) - totalday;
//			System.out.println("expired differece======="+mainday);
		}else{
			long mainday = (totalday) - lDateTime;

			if (mainday > 1000) {

				sec = (int) (mainday / 1000);

				if (sec < 60) {
					inputString = "TODAY";// Integer.toString(sec) + " Secound ago";
				} else if (sec >= 60 && sec < houre) {
					min = sec / 60;
					inputString = "TODAY";// Integer.toString(min) + " minute ago";
				} else if (sec >= houre && sec < dayTime) {
					hour = sec / houre;
					inputString = "TODAY";// Integer.toString(hour) + " hour ago";
				} else if (sec >= dayTime && sec < WeakIme) {
					day = sec / dayTime;
					inputString = "THIS WEEK";// Integer.toString(day) + " day ago";
				} else if (sec >= WeakIme && sec < monthTime) {
					week = sec / WeakIme;
					if (week > 1) {
						inputString = "NEXT WEEK";//"THIS WEEK"
					} else {
						inputString = "THIS MONTH";
					}

//					inputString = Integer.toString(week) + "";

				} else if (sec >= monthTime && sec < yearTime) {
					month = sec / monthTime;
					inputString = Integer.toString(month) + "NEXT MONTH";
					inputString = "NEXT MONTH";

				} else if (sec >= yearTime) {
					year = sec / yearTime;
					inputString = "NEXT YEAR";
					// inputString = Integer.toString(month) + " year ago";
				}

				// millisec=(int)mainday%1000;

			} else {
				inputString = "no time converiton";
			}
		}

		 Log.w("inputString", inputString);


		return inputString;

	}



	public static String ConvertDataTime(long totalday, long Endday) {





		String inputString = "";
		Date date = null;
//		long totalday = 0;
//		long Endday=0;
		int millisec = 0, sec = 0, min = 0, hour = 0, day = 0, week = 0, month = 0, year = 0;
		int miniute = 60;// 60 second
		int houre = 60 * 60; //
		int dayTime = 60 * 60 * 24;
		int WeakIme = 60 * 60 * 24 * 7;
		int monthTime = 60 * 60 * 24 * 30;
		int yearTime = 60 * 60 * 24 * 30 * 12;

//		totalday = Long.parseLong(StartDate);
//		Endday = Long.parseLong(EndDate);


		// /===get currentTime by milisecond==================
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		long lDateTime = today.getTime(); // System.currentTimeMillis(); // new
		// Date().getTime();

		if(lDateTime>totalday){
			if (lDateTime>Endday){
//				inputString = "EXPIRED";

			}else{

				inputString = "TODAY";
			}
//			inputString = "EXPIRED";
//			long mainday = (lDateTime) - totalday;
//			System.out.println("expired differece======="+mainday);
		}else{
			long mainday = (totalday) - lDateTime;

			if (mainday > 1000) {

				sec = (int) (mainday / 1000);

				if (sec < 60) {
					inputString = "TODAY";// Integer.toString(sec) + " Secound ago";
				} else if (sec >= 60 && sec < houre) {
					min = sec / 60;
					inputString = "TODAY";// Integer.toString(min) + " minute ago";
				} else if (sec >= houre && sec < dayTime) {
					hour = sec / houre;
					inputString = "TODAY";// Integer.toString(hour) + " hour ago";
				} else if (sec >= dayTime && sec < WeakIme) {
					day = sec / dayTime;
					inputString = "THIS WEEK";// Integer.toString(day) + " day ago";
				} else if (sec >= WeakIme && sec < monthTime) {
					week = sec / WeakIme;
					if (week > 1) {
						inputString = "NEXT WEEK";//"THIS WEEK"
					} else {
						inputString = "THIS MONTH";
					}

//					inputString = Integer.toString(week) + "";

				} else if (sec >= monthTime && sec < yearTime) {
					month = sec / monthTime;
					inputString = Integer.toString(month) + "NEXT MONTH";
					inputString = "NEXT MONTH";

				} else if (sec >= yearTime) {
					year = sec / yearTime;
					inputString = "NEXT YEAR";
					// inputString = Integer.toString(month) + " year ago";
				}

				// millisec=(int)mainday%1000;

			} else {
				inputString = "no time converiton";
			}
		}

		Log.w("inputString", inputString);


		return inputString;

	}


}
