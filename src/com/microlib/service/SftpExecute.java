/**
 * @(#) SftpExecute
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: SftpExecute.java
 *
 */

package com.microlib.service;

import com.jcraft.jsch.*;
import java.io.File;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;

/**
 * Class to handle secure ftp file upload or download
 * makes use of the Jsch (open source library from JCraft)
 *
 * @author lzuccarelli
 * @version 1.0.0
 */

public class SftpExecute implements ExecutorInterface {
  
    private static Log logger = LogFactory.getLog(SftpExecute.class);
	private static final String SEND = "SEND";
	private static final String SEPERATOR = "/";
	private static final String LOGFILE = "logs/sftp-response.log";
	

    /**
	 * Function that reads a string (xml data) and the either retrieves or sends the file (via sftp)
	 * @param sMap - String xml data - file name , login credentials etc
	 * @return boolean - true if all processing succeeded
	 *
	 */
	public boolean doTransfer(FtpObject obj) {
        JSch jsch = null;
        Session session = null;
		Channel channel = null;
        ChannelSftp sftpChannel = null;
        boolean bReturn = false;

        try{
			//JSch.setLogger(new MyLogger());
            jsch = new JSch();
            //int port = 22;
			
			// We dont want to enable the strict host key checking - it causes authentication failure
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			
            session = jsch.getSession(obj.getUserName(), obj.getIp(), obj.getPort());
			session.setConfig(config);
            session.setPassword(obj.getPassword());
            session.connect();
			logger.info("Connected to server " + obj.getServer() + " using ssh (sftp channel)"); 
            channel = session.openChannel( "sftp" );
            channel.connect();

            sftpChannel = (ChannelSftp) channel;
            if (obj.getMode().equals(SEND)) {
				logger.info("File to upload (Sftp) : " + obj.getLocal() + SEPERATOR + obj.getFileName() );
				logger.info("To location on remote server (Sftp) : " + obj.getRemote() + SEPERATOR + obj.getFileName() );
				sftpChannel.put(obj.getLocal() + SEPERATOR + obj.getFileName(),obj.getRemote() + SEPERATOR + obj.getFileName() );
			}
			else {
				logger.info("File to download (Sftp) : " + obj.getRemote());
                sftpChannel.get(obj.getRemote() + SEPERATOR + obj.getFileName(), obj.getLocal() + SEPERATOR + obj.getFileName() );
			}
             
            sftpChannel.exit();
            session.disconnect();
			bReturn = true;
		}
          
        catch (Exception e) {
			logger.error("Could not connect to server. " + e.toString());
			e.printStackTrace();
			bReturn = false;
		}
		finally {
			if (!bReturn) {
				try {
					session.disconnect();
				}
				catch (Exception f) {
				}
			}
		}
        return bReturn;
    }
        


	public static class MyLogger implements com.jcraft.jsch.Logger {
		static java.util.Hashtable<Integer,String> name = new java.util.Hashtable<Integer,String>();
		static {
			name.put(new Integer(DEBUG), "DEBUG: ");
		  	name.put(new Integer(INFO), "INFO: ");
		  	name.put(new Integer(WARN), "WARN: ");
		  	name.put(new Integer(ERROR), "ERROR: ");
		  	name.put(new Integer(FATAL), "FATAL: ");
		}
    	public boolean isEnabled(int level) {
      		return true;
    	}
    	public void log(int level, String message) {
      		System.err.print(name.get(new Integer(level)));
      		System.err.println(message);
    	}
  	}

}


