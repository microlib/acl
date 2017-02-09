/**
 * @(#) HtmlFormat
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: HtmlFormat.java
 *
 */

package com.microlib.dataformat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.microlib.common.StringUtils;

public class HtmlFormat {

  public StringBuffer buildControls(Map<String, Object> map) {
    StringBuffer sb = new StringBuffer();
    String digits = "[0-9\\.]+";
    String dates = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    try {

      sb.append("<p style=\"align:center\"><label id=\"txtFeedback\" style=\"color:red\"></label></p>\n");

      for (String key : map.keySet()) {
        sb.append("\t\t\t\t\t<div id=\"div" + key + "\" class=\"template-holder\">\n");
        sb.append("\t\t\t\t\t\t<div class=\"template-label\">\n");
        sb.append("\t\t\t\t\t\t\t<b><label id=\"lbl" + key + "\">" + key + "&nbsp;:&nbsp;</label></b>\n");
        sb.append("\t\t\t\t\t\t</div>\n");
        sb.append("\t\t\t\t\t\t<div class=\"value  template-control\">\n");
        sb.append("\t\t\t\t\t\t\t<input id=\"txt_" + key + "\" name=\"txt_" + key
            + "\" style=\"width: 320px;\"   title=\"" + key + "\"  />\n");
        sb.append("\t\t\t\t\t\t\t&nbsp;&nbsp;<label id=\"lblError" + key + "\" style=\"color:red\" title=\"\" />\n");
        sb.append("\t\t\t\t\t\t</div>\n");
        sb.append("\t\t\t\t\t</div>\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
      sb.append("\t\t\t\t\t<p>ERROR building html form :" + e.toString() + "</p>");
    }
    return sb;
  }

  public StringBuffer buildColumns(String[] fields) {
    StringBuffer sb = new StringBuffer();
    String digits = "[0-9\\.]+";
    String dates = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
    int nCount = 1;

    try {

      sb.append("\"order\": [[ 1, \"asc\" ]],\n");
      sb.append("\t\t\t\t\t\"columnDefs\": [{ \n");
      sb.append("\t\t\t\t\t    \"name\": \"id\",\n");
      sb.append("\t\t\t\t\t    \"searchable\": false,\n");
      sb.append("\t\t\t\t\t    \"orderable\": false,\n");
      sb.append("\t\t\t\t\t    \"visible\": false, \n");
      sb.append("\t\t\t\t\t    \"render\": function ( data, type, row ) {\n");
      sb.append("\t\t\t\t\t         return row._id.toString();\n");
      sb.append("\t\t\t\t\t    },\n");
      sb.append("\t\t\t\t\t    \"targets\": 0\n");
      sb.append("\t\t\t\t\t  },\n");

      for (String key : fields) {
        //System.out.println ("DEBUG LMZ " + fields[nCount-1]);
        sb.append("\t\t\t\t\t  {\n");
        sb.append("\t\t\t\t\t     \"name\":\"" + key.toLowerCase() + "\",\n");
        sb.append("\t\t\t\t\t     \"title\":\"" + key.toUpperCase() + "\",\n");
        sb.append("\t\t\t\t\t     \"render\": function ( data, type, row ) {\n");
        sb.append("\t\t\t\t\t         return row." + key.toLowerCase() + ";\n");
        sb.append("\t\t\t\t\t      },\n");
        sb.append("\t\t\t\t\t     \"visible\": true,\n");
        sb.append("\t\t\t\t\t     \"targets\": " + nCount + "\n");
        if (nCount == fields.length)
          sb.append("\t\t\t\t\t  }\n");
        else
          sb.append("\t\t\t\t\t  },\n");
        nCount++;
      }

      sb.append("\t\t\t\t\t]\n");
    } catch (Exception e) {
      e.printStackTrace();
      sb.append("<p>ERROR building html columns :" + e.toString() + "</p>");
    }
    return sb;
  }

}
