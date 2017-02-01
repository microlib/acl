/**
 * @(#) ShellExecute
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: ShellExecute.java
 *
 */

package com.microlib.service;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class ShellExecute extends Thread {
	
		private String sResponse = "";
		private String sExecute = "";
		
		public ShellExecute() {
		}

		public ShellExecute(String sExecute) {
			this.sExecute = sExecute;
		}

		public void setExecute(String sExecute) {
			this.sExecute = sExecute;
		}

		public String getResult() {
			return sResponse;
		}

		@SuppressWarnings("unused")
		public void run() {
				
			try {
				String sOs = System.getProperty("os.name");
				Runtime rt = Runtime.getRuntime();
				int nLog = 0;
				String[] commands = new String[3];
				long lId = System.currentTimeMillis();
				
				if (sOs.indexOf("Win") >= 0 ) {
					commands = new String[]{"cmd.exe", "/c", sExecute };
				}
				else {
					commands = new String[]{"sh", "-c", sExecute };
				}
				
				Process pr = rt.exec(commands);
				BufferedInputStream bin = new BufferedInputStream(pr.getInputStream());
				StringBuffer sb = new StringBuffer();
				byte[] b = new byte[2048];
				int nIn = 0;
				while ((nIn = bin.read(b))!= -1) {
					sb.append(new String(b,0,nIn));
				}
				sResponse = sb.toString();
				bin.close();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			finally {
			}
		
		}
}

