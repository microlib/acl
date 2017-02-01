package com.microlib.controller;

import java.util.Map;

public interface ExecInterface {

	public void setName(String name);
	public String getName();
	public boolean isRunning();
    public String doProcess(Map<String,Object> map);
	
}
