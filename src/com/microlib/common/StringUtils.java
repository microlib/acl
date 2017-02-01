package com.microlib.common;

import java.util.List;

public class StringUtils {

	public static String checkForNull(Object oIn) {
		if (oIn == null) {
			return "";
		}
		else if (oIn.toString().equalsIgnoreCase("null")) {
			return "";
		}
		else
			return oIn.toString().trim();
	}

	public static boolean find(List<Object[]> lst,String sTable,String sField) {
		boolean bFound = false;
		int nCount = 0;

		if (lst.size() == 0) return false;

		while (nCount < lst.size() && !bFound) {
			if (lst.get(nCount)[1] != null && lst.get(nCount)[1].toString().equalsIgnoreCase(sTable) && lst.get(nCount)[2] != null &&  lst.get(nCount)[2].toString().equalsIgnoreCase(sField)) {
				bFound = true;
			}
			nCount++;
		}
		return bFound;
	}

}
