package com.microlib.common;

import java.util.*;
import java.text.*;

public class Log {
				
	public static void info (String msg) {
		System.out.println (getTimeStamp()+ " : INFO\t: " + msg); 
	}

	public static void debug (String msg) {
		System.out.println (getTimeStamp()+ " : DEBUG\t: " + msg); 
	}

	public static void error (String msg) {
		System.out.println (getTimeStamp()+ " : ERROR\t: " + msg); 
	}

	public static String getTimeStamp() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(new Date()); 
	}
}
