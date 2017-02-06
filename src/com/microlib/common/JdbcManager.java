/**
 * @(#) JdbcManager
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: JdbcManager.java
 *
 */
 
package com.microlib.common;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.sql.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.logging.*;
import org.apache.tomcat.jdbc.pool.DataSource;
import com.microlib.common.StringUtils;
import com.microlib.jndi.service.*;


/**
 * JdbcManager - used to perform specific search,update commands on the underlying database
 *  
 * Tags for CVS
 * $Author$
 * $Id$
 * $Date$
 *
 */

public class JdbcManager {

	private static org.apache.commons.logging.Log logger;
    private	DataSource ds ;

		
	public JdbcManager() {
		logger = LogFactory.getLog(JdbcManager.class);
        try {
            Hashtable<String,String> ht = 	new Hashtable<String,String>();
		    ht.put("java.naming.factory.initial", "com.microlib.jndi.DSInitCtxFactory");
		    Context ctx = new InitialContext(ht);
            BasicDataSourceImpl bds = (BasicDataSourceImpl)ctx.lookup("java/BDS");
            ds = bds.getDataSource();
	    }
        catch(Exception e) {
        }
    }
	
	public List<Object[]> customQuery(String sSql) throws Exception {
		Connection con = null;
		List<Object[]> objects = new ArrayList<Object[]>();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		int nCols = 0;
		long lRes = 0;

		try {
			lRes = System.currentTimeMillis();
			con = ds.getConnection();
			logger.debug("Connection : " + con);
			stmt = con.createStatement();
		 	rs = stmt.executeQuery(sSql);
		 	rsmd = rs.getMetaData();
			nCols = rsmd.getColumnCount();
		
			while (rs.next()) {
				Object[] o = new Object[nCols];
				for (int n = 1 ; n < nCols + 1 ; n++) {
					o[n-1] = rs.getString(n);
				}
				objects.add(o);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			}
			catch(Exception e) {}
		}
		logger.info("Time to execute : " + (System.currentTimeMillis() - lRes));
		logger.info("Size : " + objects.size());
		logger.trace("Result object : " + objects);
		return objects;
	}

	public List<Object[]> customQueryMetaData(String sSql) throws Exception {
		Connection con = null;
		List<Object[]> objects = new ArrayList<Object[]>();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		int nCols = 0;

		try {
			
			con = ds.getConnection();
			logger.trace("Connection : " + con);
			stmt = con.createStatement();
		 	rs = stmt.executeQuery(sSql);
		 	rsmd = rs.getMetaData();
			nCols = rsmd.getColumnCount();

			Object[] s = new Object[nCols];
			for (int n = 1 ; n < nCols + 1 ; n++) {
				s[n-1] = new String(rsmd.getColumnName(n) + "*" + rsmd.getColumnType(n));				
			}
			objects.add(s);
		
			while (rs.next()) {
				Object[] o = new Object[nCols];
				for (int n = 1 ; n < nCols + 1 ; n++) {
					o[n-1] = rs.getString(n);
				}
				objects.add(o);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			}
			catch(Exception e) {}
		}
		logger.trace("Result object : " + objects);
		return objects;
	}

	public int executeUpdate(String sSql) throws Exception {
		Connection con = null;
		Statement stmt = null;
		int nRes = 0;
		try {
			con = ds.getConnection();
			logger.debug("Connection : " + con);
			stmt = con.createStatement();
			nRes = stmt.executeUpdate(sSql);
			logger.trace("Sql (executeQuery) : " + sSql);
			logger.trace("Result (executeQuery) : " + nRes);
		}
		catch(Exception e) {
			nRes = -1;
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			}
			catch(Exception e) {}
		}
		return nRes;
	}
	
	public StringBuffer executeQueryToFile(String query,String file,String format,boolean bHeader) {
		StringBuffer sResult = new StringBuffer();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		BufferedWriter out = null;
		int nCount = 0;
		
		try {
			logger.trace(query);
			con = ds.getConnection();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int nCols = rs.getMetaData().getColumnCount() + 1;
			String sData = "";
			String sHeader = "";
			out = new BufferedWriter(new FileWriter(file,false));
							
			while (rs.next()) {
				for (int n = 1 ; n < nCols ; n++) {
					if (bHeader && nCount == 0) {
						if (n == (nCols-1))
							sHeader = sHeader + rsmd.getColumnLabel(n);
						else
							sHeader = sHeader + rsmd.getColumnLabel(n) + ";";
					}
								
					if (n == (nCols-1)) {
						sData = sData + "" + rs.getString(n);
					}
					else {
						sData = sData + "" + rs.getString(n) + ";";
					}
				}
				if (bHeader && nCount == 0) {
					out.write(sHeader + "\n");
					out.flush();
				}
				out.write(sData.replaceAll("&","and").replaceAll("'","") + "\n");
				out.flush();
				nCount++;
				sData = "";
				sHeader = "";
			}
			sResult.append("{\"result\":\"OK\",\"value\":" + nCount + ",\"message\":\"query and file write executed successfully\"}");
			logger.debug("" + nCount + " rows selected");
		}
		catch(Exception e) {
			e.printStackTrace();
			sResult.append("{\"result\":\"KO\",\"value\":0,\"message\":\"" + e.toString() + "\"}");
		}
		finally {
			try {
				out.flush();
				out.close();
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			}
			catch(Exception e) {}
			logger.trace(sResult.toString()); 
		}
		return sResult;
	}	

}
