/**
 * @(#) QuartzScriptManager
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: QuartzScriptManager.java
 *
 */

package com.microlib.jndi.service;

import java.util.Date;
import java.util.logging.*;
import java.util.ResourceBundle;
import java.util.Properties;
import java.io.*;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Calendar;
import java.text.*;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Job;
import com.microlib.common.*;

/**
 * QuartzScriptManager - the quartz scheduler handler
 * 
 * $Author$
 * $Id$
 * $Date$
 *
 */
public class QuartzScriptManager implements Job {

	public QuartzScriptManager() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			String jobData =  (String)data.get("job");
            System.out.println("DEBUG " + jobData);
            String[] tmp = jobData.split(":");
			
			if (tmp[1].equals("true")) {
				// make use of plug-in architecture - allows for various implementations
				// SchedulerInterface mi = (SchedulerInterface)Class.forName(em.getTagToString(sJob,"class")).newInstance();
				// mi.doProcess(em.getTagToString(sJob,"script"),em.getTagToString(sJob,"params"));
			}
			else {
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		}
	}
}

