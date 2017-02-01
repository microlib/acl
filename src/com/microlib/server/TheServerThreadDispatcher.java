package com.microlib.server;

import com.microlib.controller.*;
import com.microlib.common.*;
import com.microlib.dataformat.*;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;

public class TheServerThreadDispatcher implements Runnable {

    private Map<String,Object> map = null;
    private boolean bRunning = false;
    private String name;
    private Socket socket;

    public TheServerThreadDispatcher(Socket socket) {
        this.socket = socket;
    }

    public boolean isRunning() {
        return bRunning;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void run() {
        JsonFormat json = new JsonFormat();
        bRunning = true;
        String response = "";
        String jsonString = "";
        BufferedInputStream localbin = null;
        BufferedOutputStream localbout = null;

        try {

            byte[] b = new byte[8192];
            localbin = new BufferedInputStream(socket.getInputStream());
            localbout = new BufferedOutputStream(socket.getOutputStream());
            int len = localbin.read(b);
            String input = new String(b,0,len,"UTF-8");
            Pattern pattern = Pattern.compile("\\{.*\\}",Pattern.MULTILINE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                jsonString += matcher.group();
            }
            if (jsonString.equals("")) {
                response = json.message("ERROR json input string is empty","KO");
            }
            else {
                map = json.parse(new StringBuffer(jsonString));
                ExecInterface ei = (ExecInterface)Class.forName(map.get("controller").toString()).newInstance();
                response = ei.doProcess(map);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            response = json.message("ERROR " + e.toString(),"KO");
        }
        finally {
            try {
                // send back the response
                byte[] bout = new byte[8192];
                int n = 0;
                ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes());
                while ( (n = bis.read(bout)) != -1) {
                    localbout.write(bout,0,n);
                    localbout.flush();
                }
                bis.close();
                localbin.close();
                localbout.close();
                socket.close(); 
                bRunning = false;
             }
             catch(Exception e) {
                 e.printStackTrace();
             }
        }
    }
}
