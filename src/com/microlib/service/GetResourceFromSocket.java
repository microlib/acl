/**
 * @(#) GetResourceFromSocket
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: GetResourceFromSocket.java
 *
 */

package com.microlib.service;



import java.net.*;
import java.io.*;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;



public class GetResourceFromSocket implements SocketInterface {

		private byte b[] = new byte[4096];
		private int nIn = 0;
		private Socket s = null;
		private Writer out = null;
		private FileOutputStream f = null;
		private String sUrl = new String("");
		private String sHost = new String("");
		private int nPort = 0;		
		private String sFileTo = new String("");
		private boolean bComplete = false;
		private URL server = null;
		private StringBuffer sbBuf = null;
		private boolean bStreamInterrupt = false;
		private boolean bWriteToFile = false;
		private Log logger = null;

		GetResourceFromSocket() {
		}

		public void setStreamInterrupt(boolean bIn) {
			this.bStreamInterrupt = bIn;
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
			logger = LogFactory.getLog(GetResourceFromSocket.class);
		}
	
		public void initSetup(String sUrl, String sFile) {
			try {
				this.server = new URL(sUrl);
			}
			catch(Exception e) {}
			this.sFileTo = sFile;
			this.bWriteToFile = true;
			logger = LogFactory.getLog(GetResourceFromSocket.class);

		}


		public void initSetup(String sUrl) {
			try {
				this.server = new URL(sUrl);
			}
			catch(Exception e) {}
			bWriteToFile = false;
			logger = LogFactory.getLog(GetResourceFromSocket.class);
			logger.debug("Server URL : " + this.server);
		}


		public void run() {
			doProcess();
		}

		
		public void doProcess() {

			try {
				if (server.getPort() != -1)
					s = new Socket(server.getHost(),server.getPort());
				else 
					s = new Socket(server.getHost(),80);

				out  = new OutputStreamWriter(s.getOutputStream());
			}
			catch(Exception e) {
				logger.error(e.toString());
			}

			try {
				out.write("GET " + server.getFile() + " HTTP/1.1\r\n");
				out.write("Content-Type: text/plain\r\n");
				out.write("Content-Length: 0\r\n" );
				out.write("Host: " + server.getHost() + "\r\n");
				out.write("connection: close\r\n");
				//out.println("X-Forwarded-For: 44.55.66.77");
				out.write("\r\n");
				out.flush();
				logger.info("Socket connection to " + server.getHost() + " "  + server.getFile() + " [OK]"); 
	 		}
	 		catch(Exception e) {
				logger.error( e.toString());
			}
			finally {
				//logger.commit();
			}
			

			BufferedInputStream inp = null;
			
			try {
				sbBuf = new StringBuffer();
				inp = new BufferedInputStream(s.getInputStream());
				
				String sHeader = "\nURL : " + server.toExternalForm() + "\n\n";
				sbBuf.append(sHeader);
				
				if (bWriteToFile) {
					logger.debug("Writing file " + sFileTo); 
					f = new FileOutputStream(sFileTo);
					f.write(sbBuf.toString().getBytes());
										
					while ((nIn = inp.read(b)) != -1 && !bStreamInterrupt) {
						f.write(b,0,nIn);
						f.flush();
					}
					f.flush();
					f.close();
				}
				else {
					while ((nIn = inp.read(b)) != -1 && !bStreamInterrupt) {
						sbBuf.append(new String(b,0,nIn));
					}
				}
			}
			catch (InterruptedIOException ie) {
					ie.printStackTrace();
	      			logger.error(ie.toString());
			}
			catch (IOException ie) {
					ie.printStackTrace();
	      			logger.error(ie.toString());
			}
			finally {
				bComplete = true;
				logger.info("Completed buffer read");
				//logger.commit();
			}
			return;
		}

		public String toString() {
			return server.toExternalForm() + " " + sFileTo;
		}

		public StringBuffer  getResult() {
			return this.sbBuf;
		}

		public void setData(String sData) {
		}


		public void closeAll() {
			try {
				//if (f != null) f.close();
				//if (inp != null) inp.close();
				if (out != null) out.close();
				if (s != null) s.close();
			}
			catch(Exception e) {
				logger.error(e.toString());
			}
		}
}
