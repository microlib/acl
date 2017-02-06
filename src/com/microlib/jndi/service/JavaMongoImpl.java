/**
 * @(#) JavaMongoImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: JavaMongoImpl.java
 *
 */

package com.microlib.jndi.service;

import java.util.*;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.WriteConcern;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.*;
import static java.util.concurrent.TimeUnit.SECONDS;


public class JavaMongoImpl implements JndiInterface {

	private MongoClient mongoClient;
	private DB db;
	private Log log;
	
	public JavaMongoImpl() {
	}
	
	@SuppressWarnings("unchecked")
	public void init(String value) {
	 	try {
	 	    log = LogFactory.getLog(JavaMongoImpl.class);
			ResourceBundle resource = java.util.ResourceBundle.getBundle("com.microlib.jndi.service.JavaMongoImpl");
			String server = resource.getString("server." + value);
			String user = resource.getString("user." + value);
			String port = resource.getString("port." + value);
			String database = resource.getString("database." + value);
	 	    long lRes = System.currentTimeMillis();
			mongoClient = new MongoClient( server , Integer.parseInt(port) );
			//System.out.println("DEBUG client " + (System.currentTimeMillis() - lRes) + " ms");
			lRes = System.currentTimeMillis();
            db = mongoClient.getDB( database );
            mongoClient.setWriteConcern(WriteConcern.JOURNALED);
            //System.out.println("DEBUG db " + (System.currentTimeMillis() - lRes) + " ms");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }
    
   	public JavaMongoImpl(DB db) {
	    this.db = db;
    }
   

	public StringBuffer executeMetadataQuery(String collection) throws Exception {
        DBObject myDoc = null;
		try {	
		    DBCollection coll = db.getCollection(collection);
		    myDoc = coll.findOne();
		}
		catch(Exception se){
			se.printStackTrace();
		}
		finally{
			try {
				
			}
			catch(Exception se2){
			}
			
		}
		System.out.println("\n[INFO] Completed");
        return new StringBuffer(myDoc.toString());
	}
		
	
	@SuppressWarnings("unchecked")
	public StringBuffer customQueryDatatable(Map<String,Object> map) {
	
	    BasicDBObject query = new BasicDBObject();
	    BasicDBObject inner = new BasicDBObject();
	    BasicDBList complex = new BasicDBList();
	    Map<String,Object> options = new HashMap<String,Object>();
	    Map<String,Object> columns = new HashMap<String,Object>();
	    String digits = "[0-9\\.]+";
        String dates = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer sb = new StringBuffer();
        int nCount = 0;
        int nTotal = 0;
        
		try {
		    List<Map<String,Object>> optList = (List<Map<String,Object>>)map.get("options");
		    for (Map<String,Object> m : optList) {
		        options.put(m.get("name").toString(),m.get("value"));
		    }
		    sb.append("{ \"data\" : [ ");	
		    DBCollection coll = db.getCollection(map.get("controller").toString());
		    /*
		    String key = map.keySet().iterator().next();
		    List<Map<String,Object>> lst = (List<Map<String,Object>>)map.get(key);
		    for (Map<String,Object> mp : lst) {
		        String k = mp.keySet().iterator().next();
		        String v = mp.get(k).toString();
		        if (v.matches(digits)) {
		            inner = new BasicDBObject(k,new Double(v));
		        }
		        else if (v.matches(dates)) {
		            Date dt = sf.parse(v);
		            inner = new BasicDBObject(k,dt);
		        }
		        else {
		            inner = new BasicDBObject(k,v);
		        }
		        complex.add(inner);
		    }
		    
		    query = new BasicDBObject( key , complex);
		    */
		    int nStart = Integer.parseInt(options.get("start").toString());
		    int nLength = Integer.parseInt(options.get("length").toString());
            //System.out.println("DEBUG LMZ " + options);

            // check the column order
            List<Map<String,Object>> lstOrder = (List<Map<String,Object>>)options.get("order");
            Map<String,Object> order = lstOrder.get(0);
            List<Map<String,Object>> lstCols = (List<Map<String,Object>>)options.get("columns");
            for (Map<String,Object> col : lstCols) {
                // check if its is orderable
                if (col.get("orderable").toString().equals("true")) {
                    columns.put(col.get("data").toString(),col.get("name").toString());
                }
            }

            int dir = order.get("dir").toString().equals("asc") ? 1 : -1;
            String selected = order.get("column").toString();

            // now check if we have a custom search value
            Map<String,Object> search = (Map<String,Object>)options.get("search");

            if (search.get("value") != null && !search.get("value").toString().equals("")) {
                for (String key : columns.keySet()) {
                    complex.add(new BasicDBObject(columns.get(key).toString(),  java.util.regex.Pattern.compile(search.get("value").toString())));
                }
            }
             
            DBCursor cursor = null;
            if (columns.get(selected) != null) {
                if (complex.size() > 0) {
                    // always '$or'
		            cursor = coll.find(new BasicDBObject("$or",complex)).sort(new BasicDBObject(columns.get(selected).toString(),dir)).skip(nStart).limit(nLength);
                }
                else {
		            cursor = coll.find().sort(new BasicDBObject(columns.get(selected).toString(),dir)).skip(nStart).limit(nLength);
                }
            }
            else {
                if (complex.size() > 0) {
                    cursor = coll.find(new BasicDBObject("$or",complex)).skip(nStart).limit(nLength);
                }
                else {
                    cursor = coll.find().skip(nStart).limit(nLength);
                }
            }

            //System.out.println("DEBUG LMZ " + dir + " " + selected + " " + columns);

		    nTotal = cursor.count();
		    try {
                while(cursor.hasNext()) {
                    sb.append(cursor.next().toString());
                    sb.append(",");
                    nCount++;
                 }
            } finally {
                cursor.close();
            }
		}
		catch(Exception se){
			se.printStackTrace();
		}
		
		int nPos = sb.toString().length();
		sb.setCharAt(nPos-1,' ');
		
		sb.append(" ] , \"draw\" : " + map.get("draw") + " , \"recordsTotal\" : " + nTotal + " , \"recordsFiltered\" : " + nCount + " , \"status\" : \"OK\" , \"message\" : \"success\" }");
        //System.out.println("DEBUG LMZ " + sb.toString());
		return sb;
	}
	
	public StringBuffer executeInsert(String collection,Map<String,Object> map) throws Exception {
        BasicDBObject doc = new BasicDBObject();
        String digits = "[0-9\\.]+";
        String dates = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuffer sb = new StringBuffer();
		try {	
		    DBCollection coll = db.getCollection(collection);
		    for (String key : map.keySet()) {
		        if (map.get(key) instanceof List) {
		            List<Map<String,Object>> lst = (List<Map<String,Object>>)map.get(key);
		            for (Map<String,Object> sub : lst) {
		                String k = sub.keySet().iterator().next();
		                doc.append(key,new BasicDBObject(k,sub.get(k).toString()));
		            }
		        }
		        else {
		            if (key.indexOf(".") >= 0) {
		                String[] tmp = key.split("\\.");
		                if (map.get(key).toString().matches(dates)) {
		                    Date dt = sf.parse(map.get(key).toString());
		                    doc.append(key,new BasicDBObject(tmp[1],dt));
		                }
		                else if (map.get(key).toString().matches(digits)) {
		                    doc.append(key,new BasicDBObject(key,new Double(map.get(key).toString())));
		                }
		                else {
		                    doc.append(key,new BasicDBObject(key,map.get(key).toString()));
		                }
		            }
		            else {    
		                if (map.get(key).toString().matches(dates)) {
		                    Date dt = sf.parse(map.get(key).toString());
		                    doc.append(key,dt);
		                }
		                else if (map.get(key).toString().matches(digits)) {
		                    doc.append(key,new Double(map.get(key).toString()));
		                }
		                else {
		                    doc.append(key,map.get(key).toString());
		                }
		            }
		        }
            }
       	    coll.insert(doc);
       	    sb.append("{ \"status\" : \"OK\" , \"message\" : \"" + collection + " data inserted successfully\" }");
		}
		catch(Exception se){
			se.printStackTrace();
			sb.append("{ \"status\" : \"KO\" , \"message\" : \"" + se.toString() + "\" }");
		}
		finally{
			try {
				
			}
			catch(Exception se2){
			}
			
		}
		log.info("\n[INFO] Completed");
        return sb;
	}
	
	public void destroy() {
	    log.info("Closing mongoclient connections ");
	    mongoClient.close();
	}

	

}
