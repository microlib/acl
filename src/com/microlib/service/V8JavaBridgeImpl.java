/**
 * @(#) V8JavaBridgeImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: V8JavaBridgeImpl.java
 *
 */
 
package com.microlib.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.text.MessageFormat;
import org.dojotoolkit.json.JSONParser;
import org.dojotoolkit.json.JSONSerializer;
import org.dojotoolkit.rt.v8.V8Exception;
import org.dojotoolkit.rt.v8.V8JavaBridge;
import org.dojotoolkit.server.util.resource.ResourceLoader;
import com.microlib.dataformat.*;

 
public class V8JavaBridgeImpl extends V8JavaBridge {

	private ResourceLoader resourceLoader = null;
	private String main = "";
	private JdbcManager db = null;
	private String logLevel;
	private String sScriptsDir = "";
	private PrintStream pr;
 
	public V8JavaBridgeImpl(ResourceLoader resourceLoader) {
		super();
		this.resourceLoader = resourceLoader;
	}

	public V8JavaBridgeImpl() {
		super();
	}

	public String readResource(String path) throws IOException {
		return resourceLoader.readResource(path);
	}

	public void setDbImpl(JdbcManager db) {
		this.db = db;
	}

	public void setPrintStream(PrintStream pr) {
		if (pr == null) pr = System.out;
		this.pr = pr;
	}

	public void processScript(String sIn) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbFinal = new StringBuffer();
		try {
			sb = readFile(sIn);
			sbFinal = include(sb);
			long lRes = System.currentTimeMillis();
			runScript(sbFinal.toString(), new String[]{"main","executeQuery","executeUpdate","executeQueryToFile","getResource","getMediaResource","execShell","read","write","debug","info","trace"});
			pr.println("");
			
		} catch (V8Exception e) {
			e.printStackTrace(pr);
		} catch (IOException e) {
			e.printStackTrace(pr);
		}
	}
	

	public StringBuffer include(StringBuffer sbIn) throws IOException {
		StringBuffer sbReturn = new StringBuffer();
		String sHold[] = sbIn.toString().split("\n");
		for (String s : sHold) {
			if (s.indexOf("require") >= 0) {
				StringBuffer sbBuf = new StringBuffer();
				int nStart = s.indexOf("'");
				int nStop = s.indexOf("'",nStart + 1);
				sbBuf = readFile("scripts/" + s.substring(nStart + 1,nStop) + ".js");
				sbReturn.append(include(sbBuf).toString());
			}
			else {
				sbReturn.append(s);
			}
		}
		return sbReturn;
	}

	public String trace(String msg) {
		pr.println(MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss} : TRACE\t: {1} : {2} ",new Date(),"V8JavaBridgeImpl",msg));
		pr.flush();
		return "";
	}

	public String debug(String msg) {
		pr.println(MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss} : DEBUG\t: {1} : {2} ",new Date(),"V8JavaBridgeImpl",msg));
		pr.flush();
		return "";
	}

	public String info(String msg) {
		pr.println(MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss} : INFO\t: {1} : {2} ",new Date(),"V8JavaBridgeImpl",msg));
		pr.flush();
		return "";
	}

	@SuppressWarnings("unchecked")
	public String main(String json) {
		try {
			Map<String, Object> input = (Map<String, Object>)JSONParser.parse(new StringReader(json));
			logLevel = input.get("loglevel").toString();
			Map<String, Object> returnValue = new HashMap<String, Object>();
			Map<String, Object> subVal = new HashMap<String, Object>();
			returnValue.put("returnValue", "LMZ");
			subVal.put("varA", "valA");
			subVal.put("varB","valB");
			returnValue.put("complexObj", subVal);
			StringWriter sw = new StringWriter();
			JSONSerializer.serialize(sw, returnValue);
			return sw.toString();
		} catch (IOException e) {
			e.printStackTrace(pr);
			return "{}";
		}
	}


	public String executeQuery(String query) {
	    JsonFormat json = new JsonFormat();
	    try {
            List<Object[]> lst = db.customQuery(query);
            return json.dataTable(lst);
        }
        catch(Exception e) {
	        e.printStackTrace(pr);
			return json.message("ERROR " + e.toString(),"KO");
	    }
	}


	public String executeUpdate(String query) {
	    JsonFormat json = new JsonFormat();
	    try {
	        int n = this.db.executeUpdate(query);
	        
	        if (n > 1) return json.message("Executed update successfully","OK");
	        else return json.message("ERROR executing query","KO");
	    }
	    catch(Exception e) {
	        e.printStackTrace(pr);
			return json.message("ERROR " + e.toString(),"KO");
	    }
	}
	

	@SuppressWarnings("unchecked")
	public String executeQueryToFile(String json) {
		try {
			Map<String, String> input = (Map<String, String>)JSONParser.parse(new StringReader(json));
			return this.db.executeQueryToFile(input.get("sql"),input.get("file"),input.get("format"),input.get("header").equals("yes")).toString();
		}
		catch(Exception e) {
			e.printStackTrace(pr);
			return "{}";
		}
	}

	@SuppressWarnings("unchecked")
	public String getResource(String json) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Map<String, String> input = (Map<String, String>)JSONParser.parse(new StringReader(json));
			SocketInterface sock;
			if (input.get("proxy").equals("true")) {
				sock = new GetResourceFromSocketProxy();
			}
			else {
				sock = new GetResourceFromSocket();
			}

			Thread thread = new Thread(sock);
			if (input.get("file") != null && !input.get("file").equals(""))
				sock.initSetup(input.get("url"),input.get("file"));
			else
				sock.initSetup(input.get("url"));
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();

			while (!sock.isComplete()) {
				Thread.sleep(10);
			}	
	
			sock.setStreamInterrupt(true);
			thread.join();
			retMap.put("result", "OK");
			if (input.get("file") != null && !input.get("file").equals(""))
				retMap.put("message", "completed download " + input.get("file"));
			else
				retMap.put("message", new String(sock.getResult().toString().getBytes(),"UTF-8"));
			StringWriter sw = new StringWriter();
			JSONSerializer.serialize(sw, retMap);
			return sw.toString();
		}
		catch(Exception e){
			e.printStackTrace(pr);
			return "{}";
		}
	}

	@SuppressWarnings("unchecked")
	public String getMediaResource(String json) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Map<String, String> input = (Map<String, String>)JSONParser.parse(new StringReader(json));
			SocketInterface sock;
			Long duration = 3600000L;
			int  count = 0;
			
			if (input.get("proxy").equals("true")) {
				sock = new GetResourceFromSocketProxy();
			}
			else {
				sock = new GetResourceFromSocket();
			}
			if (input.get("duration") != null && !input.get("duration").equals("")) {
				duration = Long.parseLong(input.get("duration"));
			}
			
			Thread thread = new Thread(sock);
			sock.initSetup(input.get("url"),input.get("file"));
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			Thread.sleep(duration);
			sock.setStreamInterrupt(true);

			while (!sock.isComplete() && count < 10) {
				count++;
			}
	
			thread.join();
			retMap.put("result", "OK");
			retMap.put("message", "completed download " + input.get("file"));
			StringWriter sw = new StringWriter();
			JSONSerializer.serialize(sw, retMap);
			return sw.toString();
		}
		catch(Exception e){
			e.printStackTrace(pr);
			return "{}";
		}
	}

	@SuppressWarnings("unchecked")
	public String execShell(String json) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Map<String, String> input = (Map<String, String>)JSONParser.parse(new StringReader(json));
			ShellExecute shell = new ShellExecute(input.get("command"));
			Thread thread = new Thread(shell);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			thread.join();
			retMap.put("result", "OK");
			retMap.put("message", shell.getResult());
			StringWriter sw = new StringWriter();
			JSONSerializer.serialize(sw, retMap);
			return sw.toString();
		}
		catch(Exception e){
			e.printStackTrace(pr);
			return "{}";
		}
		
	}
	
	// used internally
	private StringBuffer readFile(String sFileName) throws IOException {
		int nI = 0;
		byte buf[] = new byte[8192];
		StringBuffer sHold = new StringBuffer();
		BufferedInputStream bfin = null;

		try {
			bfin = new BufferedInputStream(new FileInputStream(this.sScriptsDir + sFileName));
			while ((nI = bfin.read(buf)) != -1) {
				if (nI != -1)
					sHold.append(new String(buf,0,nI));
			}
			bfin.close();
		}
		catch(Exception e) {
			e.printStackTrace(pr);
		}
		pr.println(MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss} : INFO\t: {1} : {2} ",new Date(),"V8JavaBridgeImpl","File " + sFileName +  " read."));
		return sHold;
	}

	public String read(String sIn) {
		try { 
			return readFile(sIn).toString();
		}
		catch (Exception e) {
			e.printStackTrace(pr);
			return e.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public String write(String json) {
		
		try {
			Map<String, String> input = (Map<String, String>)JSONParser.parse(new StringReader(json));
			BufferedWriter out = new BufferedWriter(new FileWriter(input.get("name"),input.get("append").equals("true")));
			out.write(input.get("data") + "\n");
			out.flush();
			out.close();
			return "OK";
		}
		catch(Exception e) {
			e.printStackTrace(pr);
			return e.toString();
		}
	}
}
