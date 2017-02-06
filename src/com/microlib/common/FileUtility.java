/**
 * @(#) FileUtility
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: FileUtility.java
 *
 */
 
package com.microlib.common;

import java.io.*;
import java.util.logging.*;
import java.util.Date;
import java.util.zip.*;
import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;

public class FileUtility {


	/**
	 * Legge un file
	 *
	 * @param String - sFileName (nome file)
	 * @return StringBuffer - contenuto del file 
	 */	
	public static StringBuffer readFile(String sFileName) throws IOException {
		int nI = 0;
		byte buf[] = new byte[4096];
		StringBuffer sHold = new StringBuffer();
		BufferedInputStream bfin = null;

		try {
			bfin = new BufferedInputStream(new FileInputStream(sFileName));
			while ((nI = bfin.read(buf)) != -1) {
				if (nI != -1)
					sHold.append(new String(buf,0,nI));
			}
			bfin.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return sHold;
	}

	
	public static synchronized void fileCopy(String sFrom , String sTo) throws IOException {

		File f1 = new File(sFrom);
		File f2 = new File(sTo);
		InputStream in = new FileInputStream(f1);
       	OutputStream out = new FileOutputStream(f2);

      	byte[] buf = new byte[1024];
      	int len;
      	while ((len = in.read(buf)) > 0){
        	out.write(buf, 0, len);
      	}
      	in.close();
      	out.close();
      	System.out.println("File copied.");
      	
	}

	 public static void zipFile(ZipOutputStream zipOut, String path, File file) throws IOException {
				        
        if (!file.canRead()) {
            System.out.println("Cannot read " + file.getCanonicalPath() + " (maybe because of permissions)");
            return;
        }

        System.out.println("Compressing " + file.getName());
        zipOut.putNextEntry(new ZipEntry(file.getName()));

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[4092];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1) {
            zipOut.write(buffer, 0, byteCount);
            System.out.print('.');
            System.out.flush();
        }
        System.out.println();

        fis.close();
        zipOut.closeEntry();
    }
    
    public static void zipFiles(String output, String sDir, String sSearch) throws Exception {
		
		File dir = new File(sDir).getCanonicalFile();;
		File[] files = dir.listFiles();
		
		List<File> lst = new ArrayList<File>();
		
		for (File f : files) {
			if (sSearch.equals("*")) {
				lst.add(f);
			}
			else if (sSearch.indexOf("*.")>=0) {
				if (f.getName().indexOf(sSearch.substring(sSearch.indexOf(".")+1)) >= 1) {
					lst.add(f);
				}
			}
		}
		
		if (lst.size() > 0) {
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output));
			zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
			for (File f : lst) {
				zipFile(zipOut, sDir, f);
			}
			zipOut.close();
        }
	}
   
	public static void deleteFiles(String sDir,String sSearch) throws IOException {
		
		File dir = new File(sDir).getCanonicalFile();
		File[] files = dir.listFiles();
				
		for (File f : files) {
			if (sSearch.equals("*")) {
				f.delete();
			}
			else {
				String[] sHold = sSearch.split(",");
				for (String s : sHold) {
					if (s.indexOf("*.")>=0) {
						if (f.getName().indexOf(s.substring(s.indexOf(".")+1)) >= 1) {
							f.delete();
						}
					}
				}
			}
		}
	}
   
}	
