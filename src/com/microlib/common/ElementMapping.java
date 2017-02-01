package com.microlib.common;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;


/**
 * Xml parser using regex
 * 
 * @author Luigi Mario Zuccarelli
 * @version $Revision: 1.2 $ $Date: 2008/07/25 14:17:37 $
 * 
 * history
 *
 */
public class ElementMapping  {

	
	public ElementMapping() {
	}
		
	
	/**
 	 *
	 *
 	 * @param String sSection - xml completo
	 * @return String  - xml section
	 *
 	 */
	public String getSection(String sIn , String sSection) throws ElementMappingException {
		String sResult = new String("");
		Pattern pattern = Pattern.compile("<" + sSection + "\\b.*?>.*?</" + sSection + ">",Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sIn);
		while (matcher.find()) {
			sResult = matcher.group();
		}
				
		return sResult;
	}
	

	/**
 	 * Metodo specifico per trovare tutti i elementi per ogni section
	 *
 	 * @param String sIn - xml section
	 * @param String sTagName - specifici elementi nel xml section
	 * @return ArrayList - elenco dei elementi
	 *
 	 */
	public ArrayList<String> getTag(String sIn,String sTagName) throws ElementMappingException  {
		ArrayList<String> arList = new ArrayList<String>();
		Pattern pattern = Pattern.compile("<" + sTagName + "\\b.*?>.*?</" + sTagName + ">",Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sIn);
		while (matcher.find()) {
			arList.add(matcher.group());
		}
		return arList;	
	}


	/**
 	 * Metodo specifico per trovare tutti i elementi per ogni section
	 *
 	 * @param String sIn - xml section
	 * @param String sTagName - specifici elementi nel xml
	 * @return String - string dei elementi
	 *
 	 */
	public String getTagToString(String sIn,String sTagName) throws ElementMappingException  {
		String sResult = new String("");
		Pattern pattern = Pattern.compile("<" + sTagName + "\\b.*?>.*?</" + sTagName + ">",Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(sIn);
		while (matcher.find()) {
			sResult = matcher.group().replaceAll("<" + sTagName +"\\b.*?>","").replaceAll("</" + sTagName +">","");
		}
		return sResult;	
	}
	
	/**
 	 *
	 *
 	 * @param String sSection - xml completo
	 * @return String  - xml section
	 *
 	 */
	public List<String> getMatchString(String sIn , String sExp) throws ElementMappingException {
		String sResult = new String("");
		List<String> ar = new ArrayList<String>();
		try {
			Pattern pattern = Pattern.compile(sExp ,Pattern.MULTILINE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(sIn);
			while (matcher.find()) {
				ar.add(matcher.group());
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return ar;
	}
	
	
}
