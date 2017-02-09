package com.microlib.common;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.File;

public class DataStore {

  private static Map<String, String> map = null;
  private static boolean bStandAlone = false;

  public static void setStore(Map<String, String> in) {
    map = in;
  }

  public static Map<String, String> getStore(String sIn) {
    checkForceRead(sIn);
    return map;
  }

  public static String getByName(String sIn) {
    return map.get(sIn);
  }

  public static void updateByName(String sIn, String sData) {
    map.put(sIn, sData);
  }

  private static void forceRead(String sIn) {
    try {
      FileUtility ut = new FileUtility();
      ElementMapping em = new ElementMapping();
      StringBuffer sb = ut.readFile(sIn);
      List<String> lst = em.getTag(sb.toString(), "job");
      String sName = "";
      for (String data : lst) {
        sName = em.getTagToString(data, "name");
        map.put(sName, data);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setStandAlone(boolean bIn) {
    bStandAlone = bIn;
  }

  private static synchronized void checkForceRead(String sIn) {
    try {
      if (bStandAlone) {
        File f = new File("conf/forceread.txt");
        if (f.exists()) {
          forceRead(sIn);
          f.delete();
        }
      }
    } catch (Exception e) {
    }
  }
}
