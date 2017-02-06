package com.microlib.controller;

import com.microlib.common.*;
import com.microlib.jndi.service.*;
import com.microlib.dataformat.*;
import java.util.Map;
import java.util.List;

public class SimpleService implements ExecInterface {

	int nLoop = 0;
	private boolean bRunning = false;
	private String name;

	public boolean isRunning() {
		return bRunning;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String doProcess(Map<String,Object> map) {
        String response = "";
        JsonFormat json = null;
		try {
            bRunning = true;
            json = new JsonFormat();
            json.setMap(map);
			response = new String("{ \"msg\":\"hello testing one tow three\"}");
		}
		catch(Exception e) {
			e.printStackTrace();
            response = json.message("ERROR " + e.toString(),"KO");
		}
		finally {
			try {
				bRunning = false;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
        return response;
	}

	public void init(String sIn) {
	}
}
