/**
 * @(#) SocketInterface
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: SocketInterface.java
 *
 */

package com.microlib.service;


public interface SocketInterface extends Runnable {

	public void initSetup(String sHost, String sPort , String sUrl , String sFile) throws Exception; 
	public void initSetup(String sUrl , String sFile) throws Exception; 
	public void initSetup(String sUrl) throws Exception; 
	public void setStreamInterrupt(boolean b);
	public void run();
	public StringBuffer getResult();
	public void setData(String sData);
	public boolean isComplete();

}

