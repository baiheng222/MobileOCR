package com.hanvon.rc.md.camera;

import java.util.Date;

public class CurrentTime
{

	public CurrentTime(){
		
	}
	public static String getCurrentTime(){
		Date date = new Date();
		String currentTime = String.format("%tY", date)
				+ String.format("%tm", date) + String.format("%td", date)
				+ String.format("%tH", date) + String.format("%tM", date)
				+ String.format("%tS", date);
		return currentTime;
	}
}
