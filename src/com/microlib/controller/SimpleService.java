package com.microlib.controller;

import com.microlib.common.*;
import com.microlib.service.*;
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
        JdbcManager jdbc = null;
		try {
            bRunning = true;
            json = new JsonFormat();
            jdbc = new JdbcManager();
            List<Object[]> lst = jdbc.customQuery("select * from filetransfer");
            json.setMap(map);
            response = json.dataTable(lst);
		}
		catch(Exception e) {
			e.printStackTrace();
            response = json.message("ERROR " + e.toString(),"KO");
		}
		finally {
			try {
				bRunning = false;
			}
			catch(Exception e) {}
		}
        return response;
	}
}
