package com.microlib.server;

/**
 * @(#) TheServer
 *
 * Copyright (c) 2016 Microlib All Rights Reserved.
 * This software is the proprietary information of Microlib
 * You are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: 
 * @file: TheServer.java
 *
 */

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Hashtable;
import java.util.Properties;
import java.util.ResourceBundle;
import java.net.Socket;
import java.net.ServerSocket;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import com.microlib.common.*;
import com.microlib.jndi.service.*;

/**
 * TheServer - a simple remote read and write server
 *  
 * Tags for CVS
 * $Author$
 * $Id$
 * $Date$
 *
 */

public class TheServer {

  private ExecutorService executor = null;
  private boolean bStop = false;
  private int port = 9000;
  private org.apache.commons.logging.Log log;

  public static void main(String[] args) {
    TheServer srv = new TheServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
  }

  public TheServer(int port, int threads) {

    try {
      log = LogFactory.getLog(TheServer.class);
      executor = Executors.newFixedThreadPool(threads);
      ServerSocket listener = new ServerSocket(port);

      Runtime.getRuntime().addShutdownHook(new CleanUp());

      // initialise jndi
      System.setProperty("java.naming.factory.initial", "com.microlib.jndi.DSInitCtxFactory");
      Properties env = System.getProperties();
      Context initctx = new InitialContext(env);

      // read config file
      ResourceBundle resource = java.util.ResourceBundle.getBundle("com.microlib.server.TheServer");
      int count = Integer.parseInt(resource.getString("jndiplugin.count"));
      String name = "";
      String use = "";
      for (int x = 0; x < count; x++) {
        name = resource.getString("name." + x);
        use = resource.getString("use." + x);
        if (use.equals("true")) {
          String value = resource.getString("value." + x);
          String jndi = resource.getString("jndi." + x);
          JndiInterface pi = (JndiInterface) Class.forName(name).newInstance();
          pi.init(value);
          initctx.bind(jndi, pi);
          log.info(name + " bound to jndi " + jndi);
        }
      }

      log.info("Initialising application server ...");
      log.info("Listening on port : " + port);
      log.info("Threads : " + threads);
      log.info(" ");
      log.info("Press CTRL-C to gracefully shutdown the server ");
      log.info(" ");
      log.info(" ");

      while (!bStop) {
        Socket socket = listener.accept();
        TheServerThreadDispatcher td = new TheServerThreadDispatcher(socket);
        executor.execute(td);
      }
    } catch (Exception ioe) {
      ioe.printStackTrace();
      System.exit(-1);
    } finally {
      System.exit(0);
    }
  }

  public class CleanUp extends Thread {
    public void run() {
      try {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("java.naming.factory.initial", "com.microlib.jndi.DSInitCtxFactory");
        Context ctx = new InitialContext(ht);
        NamingEnumeration<NameClassPair> list = ctx.list("");
        while (list.hasMore()) {
          String name = list.next().getName();
          log.info("Found jndi resource " + name);
          log.info("Calling destroy() method ");
          JndiInterface pi = (JndiInterface) ctx.lookup(name);
          pi.destroy();
        }
        ctx.close();
        log.info("Server shutting down ...");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
