/**
 * @(#) JsonFormat
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: JsonFormat.java
 *
 */

package com.microlib.dataformat;

import java.net.URLDecoder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.StringReader;
import org.dojotoolkit.json.JSONParser;
import org.dojotoolkit.json.JSONSerializer;
import com.microlib.common.StringUtils;

public class JsonFormat {

	private Map<String,Object> map = new HashMap<String,Object>();

	@SuppressWarnings("unchecked")
	public Map<String,Object> parse(StringBuffer json) {
		try {
			map = (Map<String, Object>)JSONParser.parse(new StringReader(json.toString()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@SuppressWarnings("unchecked")
    public StringBuffer buildJson(Map<String,Object> map) {
        StringBuffer sb = new StringBuffer();
	    try {
            sb.append ("{ 'domainclass':'" + map.get("domainclass") + "',");
            sb.append ("'package':'" + map.get("package") + "',");
            sb.append ("'table':'" + map.get("table") + "',\n");
            sb.append ("'metadata': [ ");
            
			for (String internal : map.keySet()) {
                sb.append("{\n\t\t'dbfield':'" + internal + "',\n");
				sb.append("\t\t'label':'" + internal + "',\n");
				sb.append("\t\t'formname':'txt_" + internal + "',\n");
				sb.append("\t\t'dbtype':'String',\n");
				sb.append("\t\t'cssclass':'',\n");
				sb.append("\t\t'validation':'',\n");
				sb.append("\t\t'custom':'',\n");
				sb.append("\t\t'tooltip':'',\n");
				sb.append("\t\t'mode':'',\n");
				sb.append("\t\t'javascriptevent':'',\n");
				sb.append("\t\t'controltype':'input',\n");
				sb.append("\t\t'primarykey':''\n\t},");
            }
            sb.append ("\n\n\t{'end':'end'} ] }");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return sb;
    }

    public String dataTable(List<Object[]> lst) {
		int nRows = 0;
		@SuppressWarnings("unused")
		int nCols = 0;
		StringBuffer sb = new StringBuffer("");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

		// remove header info
		lst.remove(0);
		nCols = 0;
		sb.append("{ \"timestamp\":\"" + f.format(new Date()) + "\",");
        sb.append("\"message\":\"datatable\",");
        sb.append("\"status\":\"OK\",");
        sb.append("\"iTotalRecords\" : \"" + map.get("iTotalRecords") + "\",");
        sb.append("\"iTotalDisplayRecords\" : \"" +  map.get("iTotalRecords") + "\",");
			
		sb.append("\"aaData\": [ \n");
		if ( lst.size() > 0 ) {
			for (Object[] obj : lst) {
				nCols = 0;
				nRows++;
				sb.append("[");
				for (int j = 0 ; j < obj.length-2 ; j++) {
					if (j == (obj.length - 3))
						sb.append("\"" + obj[j] + "\" ");
					else 
						sb.append("\"" + obj[j]  + "\",");
				}
				if (nRows == lst.size())
					sb.append("]\n");
				else 
					sb.append("],\n");
			}
		}
		sb.append("] }");
		return sb.toString();
	}

	public String message(String sMessage,String sStatus) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		return "{ \"timestamp\":\"" + f.format(new Date()) + "\", \"message\":\"" + sMessage + "\",\"status\":\"" + sStatus + "\" }\n";
	}
	
	public Map<String,Object> getMap() {
		return this.map;
	}

	public void setMap(Map<String,Object> map) {
		this.map = map;
	}
}
