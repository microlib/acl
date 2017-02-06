package com.microlib.jndi.service;

/**
 * @(#) BasicDataSourceInit
 *
 * Copyright (c) 2007 Luigi Mario Zuccarelli All Rights Reserved.
 * This software is the proprietary information of Luigi Mario Zuccarelli
 * You are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Thu Sep 17 15:30:20 CEST 2009
 * @file: BasicDataSourceInit.java
 *
 */


import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.commons.logging.*;
import java.sql.*;
import java.util.*;

/**
 * @(#) BasicDataSourceImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: BasicDataSourceImpl.java
 *
 *
 *  
 * Tags for CVS
 * $Author$
 * $Id$
 * $Date$
 *
 */
 
public class BasicDataSourceImpl implements JndiInterface  {

	private DataSource ds;
	private Log logger;
			
	
    public BasicDataSourceImpl() {
    }

    public void init(String sDataSource) {
		try {
			//logger = new LogFactoryBuffer("SwingV8Gui","SwingV8Gui","info",LogFactoryBuffer.BOTH);
			logger = LogFactory.getLog(BasicDataSourceImpl.class);
			ResourceBundle resource = java.util.ResourceBundle.getBundle("com.microlib.jndi.service.BasicDataSourceImpl");
			String sDriverClass = resource.getString("driver." + sDataSource);
			String sUser = resource.getString("user." + sDataSource);
			String sPwd = resource.getString("password." + sDataSource);
			String sUrl = resource.getString("url." + sDataSource);
			String sInitialSize = resource.getString("initialSize." + sDataSource);
			String sMaxActive = resource.getString("maxActive." + sDataSource);
			String sMinActive = resource.getString("minActive." + sDataSource);
			String sMaxIdle = resource.getString("maxIdle." + sDataSource);
			String sMinIdle = resource.getString("minIdle." + sDataSource);
			String sMaxWait = resource.getString("maxWait." + sDataSource);
			String jndiName = resource.getString("jndi." + sDataSource);

			
			PoolProperties p = new PoolProperties();
			
			p.setUrl(sUrl);
			p.setDriverClassName(sDriverClass);
			p.setUsername(sUser);
			p.setPassword(sPwd);
			p.setJmxEnabled(true);
			p.setTestWhileIdle(false);
			p.setTestOnBorrow(true);
			p.setValidationQuery("SELECT 1");
			p.setTestOnReturn(false);
			p.setValidationInterval(30000);
			p.setTimeBetweenEvictionRunsMillis(30000);
			p.setMaxActive(Integer.parseInt(sMaxActive));
			p.setMaxIdle(Integer.parseInt(sMaxIdle));
			p.setInitialSize(Integer.parseInt(sInitialSize));
			p.setMaxWait(Integer.parseInt(sMaxWait));
			p.setRemoveAbandonedTimeout(60);
			p.setMinEvictableIdleTimeMillis(30000);
			p.setMinIdle(Integer.parseInt(sMinIdle));
			p.setLogAbandoned(true);
			p.setRemoveAbandoned(true);
			p.setJdbcInterceptors(
            "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
            "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
			ds = new DataSource();
			ds.setPoolProperties(p);
			
			logger.info("DB connection pool details ");
			logger.info("max size : " + sMaxActive);
			logger.info("driver : " + sDriverClass);
			logger.info("user : " + sUser);
			logger.info("url : " + sUrl);
			//logger.info("jndi : " + jndiName);
			
			// quick test
			logger.info("Connection pool before " + ds.getNumActive() + " " + ds.getNumIdle());
			java.sql.Connection con = ds.getConnection();
			logger.info("Connection pool after " + ds.getNumActive() + " " + ds.getNumIdle());
			con.close();
			logger.info("Connection pool after close " + ds.getNumActive() + " " + ds.getNumIdle());
		}
		catch(Exception e) {
			System.out.println("Error " + e.toString());
			e.printStackTrace();
		}
		finally {
			//logger.commit();
		}
	}

	public DataSource getDataSource() {
		return ds;
	}

	public synchronized Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public void destroy() {
		Driver driver = null; 
		try {
		    logger.info(" ");			
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
				logger.info(String.format("Deregistering jdbc driver: %s",driver));
			}
			if (null != ds) {
				ds.close();
				logger.info("Connection pool closed");
				logger.info("Datasource released");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Error deregistering driver %s",driver));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			//logger.commit();
		}
	}
}

