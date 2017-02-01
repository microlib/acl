/**
 * @(#) RemoteScriptLaunch
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: RemoteScriptLaunch.java
 *
 */

package com.microlib.service;

import com.jcraft.jsch.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.*;
import java.util.Properties;
import org.apache.commons.codec.binary.*;

public class RemoteScriptLaunch {

	Log logger = LogFactory.getLog(RemoteScriptLaunch.class);
	private String sUser;
	private String sPwd;
	private String sIp;	

	public RemoteScriptLaunch(String sUser,String sPwd,String sIp) {
		this.sUser = sUser;
		this.sPwd = sPwd;
		this.sIp = sIp;
	}

	public RemoteScriptLaunch(String sUser,String sPwd,String sIp,boolean bDecode) {
		this.sUser = sUser;
		this.sIp = sIp;
		
		try {
			Base64 base64 = new Base64();
			byte[] decode = base64.decode(sPwd.getBytes());
			this.sPwd = new String(decode, "US-ASCII");
		}
		catch(Exception e) {
			e.printStackTrace();
		} 
	}

	public String exec(String command) {

		StringBuffer sb = new StringBuffer("");
		int status = -1;
		InputStream in = null;
		Session session = null;
		Channel channel = null;

		try {

			JSch jsch=new JSch();
			logger.info("[User : IP] [" + sUser + " : " + sIp + "]");
			
           // jsch.addIdentity(privateKey,(String)null);
           // logger.debug("Identity added " + privateKey);

			// We dont want to enable the strict host key checking - it causes authentication failure
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			// config.put("PreferredAuthentications","publickey" );
			session = jsch.getSession(sUser,sIp, 22);
			session.setConfig(config);
			session.setTimeout(120000);
			session.setPassword(sPwd);
			session.connect();
			
			channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);
			logger.info("Command " + command);
			
			// X Forwarding
			// channel.setXForwarding(true);
			// channel.setInputStream(System.in);

			channel.setInputStream(null);
			channel.setOutputStream(System.out);
			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);

			// ((ChannelExec)channel).setErrStream(System.err);
			in=channel.getInputStream();
			channel.connect();
			logger.debug("channel " + channel);
		}
		catch(Exception ee){ 
			ee.printStackTrace();
			try {
				if (channel != null) channel.disconnect();
				if (session != null) session.disconnect();
			}
			catch(Exception ex) {
				System.exit(-1);
			}
		}

		try {

			byte[] tmp = new byte[2048];
			boolean bCompleted = false;
			int i = 0;
				
			while((i = in.read(tmp)) != -1) {
				if(i != -1) {
					sb.append(new String(tmp, 0, i));
				}
			}
				
			logger.info("completed read ");
				
			if(channel.isClosed()) {
				status = channel.getExitStatus();
				logger.info("exit-status: "+ status);
			}

			try {
				Thread.sleep(100);
			}
			catch(Exception ee){ 
				ee.printStackTrace(); 
			}
			
			channel.disconnect();
			session.disconnect();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		if (status != 0) {
			sb.append("ERROR");
			logger.error(status);
		}
		
		logger.trace(sb.toString());
		return sb.toString();
	}

	public static void main(String[] sArgs) {
		RemoteScriptLaunch s = new RemoteScriptLaunch(sArgs[0],sArgs[1],sArgs[2],true);
		String ret = s.exec(sArgs[3]);
		System.out.println("\n" + ret);
		System.exit(0);
	}

}

