/**
 * @(#) SchedulerImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: SchedulerImpl.java
 *
 */

package com.microlib.jndi.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.DateBuilder.*;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.Date;
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
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Job;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;
import com.microlib.common.*;
import com.microlib.dataformat.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SchedulerImpl - Main controller makes use of the quartz scheduler to handle scheduled file transfer tasks 
 * 
 *
 * $Author$
 * $Id$
 * $Date$
 *
 *
 */

@SuppressWarnings("all")
public class SchedulerImpl implements JndiInterface, Runnable {

  private org.apache.commons.logging.Log logger = null;
  private static final String LOGLEVEL = "LogLevel";
  private SchedulerFactory sf;
  private Scheduler sched;
  private ResourceBundle resource = null;
  private String sHibernateSession = null;
  private String sRuleMap = null;
  private String sPluginClass = null;
  private String sMachine = null;
  private String sTempDir = null;
  private List<String> lstFiles = null;
  private boolean bRunning = false;

  public SchedulerImpl() {
  }

  public void init(String value) {
    ExecutorService exec = Executors.newFixedThreadPool(1);
    exec.execute(this);
  }

  public void run() {
    try {

      // Get resource file for configuration
      // resource = java.util.ResourceBundle.getBundle("com.microlib.jndi.service.SchedulerImpl");
      logger = LogFactory.getLog(SchedulerImpl.class);
      logger.info("Initialising scheduler");
      FileUtility ut = new FileUtility();
      StringBuffer sb = ut.readFile("conf/scheduler.json");
      JsonFormat json = new JsonFormat();
      Map<String, Object> map = json.parse(sb);

      sf = new StdSchedulerFactory();
      sched = sf.getScheduler();

      String sName = "";
      String sScript = "";
      String sCrontab = "";
      String sEnabled = "";

      List<Map<String, Object>> lst = (List<Map<String, Object>>) map.get("jobs");
      logger.debug("List of jobs found " + lst);

      for (Map<String, Object> kv : lst) {
        sName = kv.get("name").toString();
        sCrontab = kv.get("crontab").toString();
        sEnabled = kv.get("enabled").toString();
        sScript = kv.get("script").toString();
        //map.put(sName,kv);
        if (sEnabled.equals("true")) {
          logger.info("------- Scheduling Job -------");
          JobDetail job = newJob(QuartzScriptManager.class).withIdentity(sName, "JavaScript").build();
          job.getJobDataMap().put("job", sName + ":" + sEnabled + ":" + sCrontab + ":" + sScript);
          CronTrigger trigger = newTrigger().withSchedule(cronSchedule(sCrontab)).build();
          Date ft = sched.scheduleJob(job, trigger);
          logger.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
              + trigger.getCronExpression() + "\n");
        } else {
          logger.info("------- Scheduling Job -------");
          logger.info("Job " + sName + " is disabled and won't be scheduled\n");
        }
      }

      //DataStore.setStore(lst);
      sched.start();
      bRunning = true;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.toString());
    }
  }

  public void destroy() {
    try {
      logger.info("Shutting down service...");
      sched.shutdown(true);
      SchedulerMetaData metaData = sched.getMetaData();
      logger.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs");
      logger.info("Shutdown Complete");
      //logger.commit();
      bRunning = false;
    } catch (Exception e) {
      logger.error(e);
      //logger.commit();
    }
  }

  public boolean isRunning() {
    return bRunning;
  }

}
