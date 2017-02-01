package com.microlib.common;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.File;
import com.microlib.service.FileUtility;

public class DataStore {

	private static Map<String,String> map = null;
	private static boolean bStandAlone = false;

	public static void setStore(Map<String,String> in) {
		map = in;
	}

	public static Map<String,String> getStore() {
		checkForceRead();
		return map;
	}

	public static String getByName(String sIn) {
		return map.get(sIn);
	}

	public static void updateByName(String sIn,String sData) {
		map.put(sIn,sData);
	}

	private static void forceRead() {
		try {
			FileUtility ut = new FileUtility();
			ElementMapping em = new ElementMapping();
			StringBuffer sb = ut.readFile("conf/scheduler.xml");
			List<String> lst = 	em.getTag(sb.toString(),"job");	
			String sName = "";	
			for (String data : lst) {
				sName = em.getTagToString(data,"name");
				map.put(sName,data);
			}
		}:while (condition) {
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setStandAlone(boolean bIn) {
		bStandAlone = bIn;
	}

	private static synchronized void checkForceRead() {
		try {
			if (bStandAlone) {
				File f = new File("conf/forceread.txt");
				if (f.exists()) {
					forceRead();
					f.delete();
				}
			}
		}
		catch(Exception e) {
		}
	}
}
