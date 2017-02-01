/**
 * @(#) PostToSocket
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: PostToSocket.java
 *
 */

package com.microlib.service;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.commons.codec.binary.*;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;

public class PostToSocket implements SocketInterface  {

	private byte b[] = new byte[4096];
	private int nIn = 0;
	private Socket socket = null;
	private Log logger = null;
	private String sUrl = new String();
	private String sHost = new String();
	private String sFileTo = new String("");
	private int nPort = 0;
	private boolean bStreamInterrupt = false;
	private URL server = null;
	private StringBuffer sbBuf = null;
	private boolean bWriteToFile = false;
	private String sData = "";
	boolean bComplete = false;


	public void PostToSocket() {
	}

	public void setStreamInterrupt(boolean bIn) {
		bStreamInterrupt = bIn;
	}

	public boolean isComplete() {
			return this.bComplete;
	}

	public void initSetup(String sHost, String sPort, String sUrl, String sFile) {
		this.sHost = sHost;
		this.nPort = Integer.parseInt(sPort);
		this.sUrl = sUrl;
		try {
			this.server = new URL("http://" + sHost + ":" + sPort + sUrl);
		}
		catch(Exception e) {}
		this.sFileTo = sFile;
		this.bWriteToFile = true;
		logger = LogFactory.getLog(PostToSocket.class);
		logger.debug("Server URL : " + this.server);
	}

	public void initSetup(String sUrl, String sFile) {
		try {
			this.server = new URL(sUrl);
		}
		catch(Exception e) {}
		this.sFileTo = sFile;
		this.bWriteToFile = true;
		logger = LogFactory.getLog(PostToSocket.class);
		logger.debug("Server URL : " + this.server);
	}

	public void initSetup(String sUrl) {
		try {
			this.server = new URL(sUrl);
		}
		catch(Exception e) {}
		this.bWriteToFile = false;
		logger = LogFactory.getLog(PostToSocket.class);
		logger.debug("Server URL : " + this.server);
	}


	public void run() {
		boolean bRunOnce = true;
		while(bRunOnce) {
			doProcess();
			bRunOnce = false;	
		}
	}

	public void doProcess() {

		Locale loc = java.util.Locale.ROOT;
		ResourceBundle resource = null;

		try {
			resource = java.util.ResourceBundle.getBundle("PostToSocket",loc);
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}


		try {
			//final String sAuthUser = resource.getString("authuser");
			//final String sAuthPwd = resource.getString("authpassword");

			//Base64 base64 = new Base64();
			//byte[] encodeUser = base64.encode((sAuthUser +":" + sAuthPwd).getBytes());

			socket = new Socket(server.getHost(),server.getPort());

			Writer writer = new OutputStreamWriter(socket.getOutputStream(),"US-ASCII");
			writer.write("POST " + server.toExternalForm() + " HTTP/1.0\r\n");
			writer.write("Host: " + server.getHost() + "\r\n");
			//writer.write("Authorization: Basic " + new String(encodeUser,0,encodeUser.length)  + "\r\n");
			writer.write("Content-Length: " + sData.length() + "\r\n");
			writer.write("Content-Type: application/x-www-form-urlencoded\r\n\r\n");

			writer.write(sData + "\r\n");
			writer.flush();

			logger.info("Socket connection no proxy " + server.getHost() + " ok"); 			


			try {
				sbBuf = new StringBuffer();
				BufferedInputStream inp = new BufferedInputStream(socket.getInputStream());

				String sHeader = "\nURL : " + server.toExternalForm() + "\n\n";
				sbBuf.append(sHeader);
				if (bWriteToFile) {
					logger.debug("Writing file " + sFileTo); 
					FileOutputStream buf = new FileOutputStream(sFileTo);
					buf.write(sbBuf.toString().getBytes());
					while ((nIn = inp.read(b))!= -1 && !bStreamInterrupt) {
						buf.write(b,0,nIn);
						buf.flush();
					}
					buf.flush();
					buf.close();
				}
				else {
					while ((nIn = inp.read(b))!= -1 && !bStreamInterrupt) {
						sbBuf.append(new String(b,0,nIn));
					}
				}
				writer.close();
				socket.close();
			}
			catch (InterruptedIOException ie) {
				logger.error(ie.toString());
			}
			catch (IOException ie) {
				logger.error(ie.toString());
			}
			logger.info("Completed resource download " + sFileTo + " successfully\n"); 
		}
		catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		finally {
			this.bComplete = true;
		}
	}

	public String toString() {
		return server.toExternalForm() + " " + sFileTo;
	}

	public StringBuffer  getResult() {
		return this.sbBuf;
	}

	public void setData(String sData) {
		this.sData = sData;
	}
}
