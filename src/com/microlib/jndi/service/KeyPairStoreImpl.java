package com.microlib.jndi.service;


import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;

/**
 * @(#) KeyPairStoreImpl
 *
 * In the light of open source software you are free to do what you like with this code.
 * Redistribution and use in source and binary forms, with or without
 * modification, is absolutely permitted - just keep the credits please.
 *
 * @author: Luigi Mario Zuccarelli
 * @version: 1.10
 * @date: Generated on Mon Dec 29 15:30:20 CEST 2014
 * @file: KeyPairStoreImpl.java
 *
 *
 *  
 * Tags for CVS
 * $Author$
 * $Id$
 * $Date$
 *
 */

public class KeyPairStoreImpl implements JndiInterface {

  private static org.apache.commons.logging.Log log;
  private Map<String, byte[]> publicKeyStore = new HashMap<String, byte[]>();
  private Map<String, byte[]> privateKeyStore = new HashMap<String, byte[]>();

  public KeyPairStoreImpl() {
    log = LogFactory.getLog(KeyPairStoreImpl.class);
  }

  public void init(String sIn) {

    try {
      File[] directories = new File("app-keystore").listFiles(File::isDirectory); 
      for (File f : directories) {
        log.info("Loading keypair for " + f.getName());
        privateKeyStore.put(f.getName(), Files.readAllBytes(new File(f.getPath() + "/private_key.der").toPath()));
        publicKeyStore.put(f.getName(), Files.readAllBytes(new File(f.getPath() + "/public_key.der").toPath()));
      }
    } catch (IOException io) {
      log.error(io);
    }
  }

  public byte[] getPrivateByteArray(String sIn) {
    return privateKeyStore.get(sIn);
  }

  public byte[] getPublicByteArray(String sIn) {
    return publicKeyStore.get(sIn);
  }

  public void destroy() {
    try {
      log.info(" ");
      privateKeyStore = null;
      publicKeyStore = null;
    } catch (Exception e) {
      log.error(e);
    } finally {
      log = null;
    }
  }
}
