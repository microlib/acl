/* ---- Code auto indent ver 1.1 lmzsoftware (date time : Tue Jun 24 13:15:02 CEST 2008) ---- */

/**
 * @(#) MailImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: MailImpl.java
 *
 */

package com.microlib.jndi.service;

import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Flags;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;

public class MailImpl implements JndiInterface {

  private ArrayList<String> arToList;
  private ArrayList<String> arCCList = new ArrayList<String>();
  private ArrayList<String> arBCCList = new ArrayList<String>();;
  private ArrayList<String> arAttachments;
  private HashMap<String, String> mapImages;
  private String sReplyTo;
  private String sFrom;
  private String sSmtp;
  private String sPop;
  private String sSubject;
  private String sBody;
  private String sGreeting;
  private String sSignature;
  private String sUser;
  private String sPassword;
  private String sFileName = "Attachment.pdf";
  private boolean isHtml = false;
  private boolean bAuth = false;
  private boolean bDebug = false;
  private Log logger = null;
  private Session session;
  private String sHost;
  private PrintStream pr;
  private String sMimeType;

  public MailImpl() {
  }

  // to conform to the intrface method signature
  public void init(String value) {
    logger = LogFactory.getLog(MailImpl.class);
  }

  public void destroy() {
    logger = null;
  }

  public void setToList(ArrayList<String> ar) {
    this.arToList = ar;
  }

  public void setCCList(ArrayList<String> ar) {
    this.arCCList = ar;
  }

  public void setBCCList(ArrayList<String> ar) {
    this.arBCCList = ar;
  }

  public void setAttachments(ArrayList<String> ar) {
    this.arAttachments = ar;
  }

  public void setReplyTo(String sIn) {
    this.sReplyTo = sIn;
  }

  public void setFrom(String sIn) {
    this.sFrom = sIn;
  }

  public void setSmtp(String sIn) {
    this.sSmtp = sIn;
  }

  public void setPop(String sIn) {
    this.sPop = sIn;
  }

  public void setSubject(String sIn) {
    this.sSubject = sIn;
  }

  public void setBody(String sIn) {
    this.sBody = sIn;
  }

  public String getBody() {
    return this.sBody;
  }

  public void setFileName(String sIn) {
    this.sFileName = sIn;
  }

  public void setGreeting(String sIn) {
    this.sGreeting = sIn;
  }

  public void setSignature(String sIn) {
    this.sSignature = sIn;
  }

  public void setUser(String sIn) {
    this.sUser = sIn;
  }

  public void setPassword(String sIn) {
    this.sPassword = sIn;
  }

  public void setImages(HashMap<String, String> mapImg) {
    this.mapImages = mapImg;
  }

  public void setUseHtml(boolean isHtml) {
    this.isHtml = isHtml;
  }

  public void setAuth(boolean bAuth) {
    this.bAuth = bAuth;
  }

  public void setDebug(boolean bIn) {
    bDebug = bIn;
  }

  public void setHost(String sIn) {
    this.sHost = sIn;
  }

  public void setPrintStream(PrintStream pr) {
    this.pr = pr;
  }

  public void setMimeType(String sIn) {
    this.sMimeType = sIn;
  }

  public void send(byte[] b) throws RuntimeException {
    String sError = new String("ok");
    javax.mail.Message msg;

    try {
      java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
      Properties props = System.getProperties();
      props.put("mail.debug", "" + bDebug);
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.smtp.port", "587");
      props.put("mail.smtp.auth", "" + bAuth);
      props.put("mail.smtp.host", sSmtp);
      Authenticator auth = new MailAuthenticator(this.sUser, this.sPassword);
      //Authenticator auth = new SMTPAuthenticator();
      session = Session.getDefaultInstance(props, auth);
      msg = new MimeMessage(session);
    } catch (Exception e) {
      logger.error(e.toString());
      throw new RuntimeException("MailImpl : " + e.getMessage());
    } finally {
      //logger.commit();
    }

    InternetAddress[] iar = null;
    InternetAddress[] cciar = null;
    InternetAddress[] bcciar = null;
    int nI = 0;

    iar = new InternetAddress[arToList.size()];
    try {
      for (nI = 0; nI < arToList.size(); nI++) {
        iar[nI] = new InternetAddress(arToList.get(nI), false);
      }
      if (arToList.size() >= 1) {
        msg.setRecipients(javax.mail.Message.RecipientType.TO, iar);
      }
    } catch (Exception e) {
      logger.error("to address " + e.toString());
      throw new RuntimeException("MailImpl to address : " + e.toString());
    }

    cciar = new InternetAddress[arCCList.size()];
    try {
      for (nI = 0; nI < arCCList.size(); nI++) {
        cciar[nI] = new InternetAddress(arCCList.get(nI), false);
      }

      if (arCCList.size() >= 1) {
        msg.setRecipients(javax.mail.Message.RecipientType.CC, cciar);
      }
    } catch (Exception e) {
      logger.error("cc address " + e.toString());
      throw new RuntimeException("MailImpl cc address : " + e.toString());
    }

    bcciar = new InternetAddress[arBCCList.size()];
    try {
      for (nI = 0; nI < arBCCList.size(); nI++) {
        bcciar[nI] = new InternetAddress(arBCCList.get(nI), false);
      }

      if (arBCCList.size() >= 1) {
        msg.setRecipients(javax.mail.Message.RecipientType.BCC, bcciar);
      }
    } catch (Exception e) {
      logger.error("bcc address " + e.toString());
      throw new RuntimeException("MailImpl bcc address : " + e.toString());
    }

    try {
      msg.setFrom(new InternetAddress(sFrom));
      msg.setReplyTo(InternetAddress.parse(sReplyTo, false));
      msg.setSubject(sSubject);
      addAttachments(msg, sBody, b);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("addAttachments " + e.toString());
      throw new RuntimeException("MailImpl addAttachments : " + e.getMessage());
    }

    try {

      Transport tp = session.getTransport("smtp");
      tp.send(msg);
      logger.info("sent mail to server ");

    } catch (Exception e) {
      logger.error(e.toString());
      throw new RuntimeException("MailImpl : " + e.toString());
    } finally {
      //logger.commit();
    }
  }

  public void receive() {
    try {
      Properties props = System.getProperties();
      props.put("mail.debug", "" + bDebug);
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.transport.protocol", "pop");
      props.put("mail.smtp.port", "110");
      props.put("mail.smtp.auth", "" + bAuth);
      props.put("mail.smtp.host", sPop);
      Authenticator auth = new MailAuthenticator(this.sUser, this.sPassword);
      //Authenticator auth = new SMTPAuthenticator();
      session = Session.getDefaultInstance(props, auth);
      Store store = session.getStore("pop3");

      if (pr == null) {
        pr = System.out;
      }

      pr.println("store " + store);
      store.connect(this.sPop, this.sUser, this.sPassword);
      pr.println("connected...");
      Folder inbox = store.getDefaultFolder().getFolder("inbox");
      inbox.open(Folder.READ_ONLY);
      Message[] msg = inbox.getMessages();

      for (Message m : msg) {
        pr.println("------------ Message ------------");
        pr.println("SentDate : " + m.getSentDate());
        pr.println("From : " + m.getFrom()[0]);
        pr.println("Subject : " + m.getSubject());
        pr.print("Message : ");
        InputStream stream = m.getInputStream();
        while (stream.available() != 0) {
          pr.print((char) stream.read());
        }
        pr.println();
        //m.setFlag(Flags.Flag.DELETED, true);
      }

      inbox.close(true);
      store.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addAttachments(javax.mail.Message msg, String sBody, byte[] b) throws MessagingException {
    MimeMultipart multipart = null;
    MimeBodyPart messageBodyPart = null;
    String sImage = new String();

    if (this.isHtml) {
      messageBodyPart = new MimeBodyPart();
      multipart = new MimeMultipart("related");
      messageBodyPart.setContent(sBody, "text/html");
      multipart.addBodyPart(messageBodyPart);

      for (String sKey : mapImages.keySet()) {
        messageBodyPart = new MimeBodyPart();
        sImage = mapImages.get(sKey);
        DataSource fds = new FileDataSource(sImage);
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setHeader("Content-ID", sKey);
        if (sImage.indexOf("png") >= 0)
          messageBodyPart.setHeader("Content-Type", "image/png");
        if (sImage.indexOf("gif") >= 0)
          messageBodyPart.setHeader("Content-Type", "image/gif");
        if (sImage.indexOf("jpg") >= 0)
          messageBodyPart.setHeader("Content-Type", "image/jpeg");
        if (sImage.indexOf("tiff") >= 0)
          messageBodyPart.setHeader("Content-Type", "image/tiff");
        multipart.addBodyPart(messageBodyPart);
      }
    } else {
      multipart = new MimeMultipart();
      messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(sBody, this.sMimeType);
      multipart.addBodyPart(messageBodyPart);
    }

    if (b != null) {
      MimeBodyPart mbp = new MimeBodyPart();
      mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(b, this.sMimeType)));
      mbp.setFileName(this.sFileName);
      multipart.addBodyPart(mbp);
    }
    msg.setContent(multipart);
  }

  private class MailAuthenticator extends javax.mail.Authenticator {
    private String sUser;
    private String sPwd;

    private MailAuthenticator(String sUser, String sPwd) {
      this.sUser = sUser;
      this.sPwd = sPwd;
    }

    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.sUser, this.sPwd);
    }
  }
}
