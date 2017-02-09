package com.microlib.common;

/**
 * Element Mapping Exception Class
 * 
 * @author Luigi Mario Zuccarelli
 * @version $Revision: 1.2 $ $Date: 2008/07/25 14:17:37 $
 * 
 * history
 *
 */
public class ElementMappingException extends Exception {

  private String sMessage = null;

  public ElementMappingException() {
  }

  public ElementMappingException(String sMessage) {
    this.sMessage = sMessage;
  }

  public String toString() {
    String sMessage = new String();
    sMessage = "ERROR  ElementMappingException : " + this.sMessage;
    return sMessage;
  }

  public int getSeverity() {
    return 0;
  }

}
