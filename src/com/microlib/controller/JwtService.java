package com.microlib.controller;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import com.microlib.dataformat.*;
import com.microlib.jndi.service.*;
import java.io.UnsupportedEncodingException;
import javax.naming.NamingException;

public class JwtService implements ExecInterface {

  int nLoop = 0;
  private boolean bRunning = false;
  private String name;
  private static org.apache.commons.logging.Log log;
  final static String jndiName = "java/KeyStore";
  private KeyPairStoreImpl keyStore;
 

  public boolean isRunning() {
    return bRunning;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public String doProcess(Map<String, Object> map) {
    String response = "";
    JsonFormat json = new JsonFormat();

    bRunning = true;
    if (null == map.get("action")) {
      response = json.message("ERROR no action command found ", "KO");
    } else {
      log.info("map " + map.get("action"));
      if (map.get("action").toString().equals("getToken")) {
        try {
          String token = JWT.create().withIssuer("auth0").sign(Algorithm.HMAC256("secret"));
          log.info("Token " + token);
          response = json.message("Token " + token, "OK");
        } catch (JWTCreationException | UnsupportedEncodingException exception) {
          log.error(exception);
          response = json.message("ERROR " + exception.toString(), "KO");
        }
      } else if (map.get("action").toString().equals("createAndSignToken")) {
        try {
          // create a token with our generated private rsa key
          // use the key-id to retrieve the correct kea
          if (null == map.get("key-id")) {
            log.error("no key-id found ");
            response = json.message("ERROR no key-id found ", "KO");
          } else {
            byte[] keyBytes = keyStore.getPrivateByteArray(map.get("key-id").toString());
            if (null == keyBytes) {
              log.error("key-id " + map.get("key-id").toString() + " not found");
              response = json.message("ERROR key-id " + map.get("key-id").toString() + " not found ", "KO");
            } else {
              PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
              KeyFactory kf = KeyFactory.getInstance("RSA");
              RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(spec);
              String token = JWT.create().withIssuer("auth0").sign(Algorithm.RSA256(key));
              log.info("Signed Token : " + token);
              response = json.message("Signed Token " + token, "OK");
            }
          }
        } catch (JWTCreationException | NoSuchAlgorithmException | InvalidKeySpecException exception) {
          log.error(exception);
          response = json.message("ERROR " + exception.toString(), "KO");
        }
      } else if (map.get("action").toString().equals("verifyToken")) {
        try {
          if (null == map.get("key-id")) {
            log.error("no key-id found ");
            response = json.message("ERROR no key-id found ", "KO");
          } else {
            byte[] keyBytes = keyStore.getPublicByteArray(map.get("key-id").toString());
            if (null == keyBytes) {
              log.error("key-id " + map.get("key-id").toString() + " not found");
              response = json.message("ERROR key-id " + map.get("key-id").toString() + " not found ", "KO");
            } else {
              X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
              KeyFactory kf = KeyFactory.getInstance("RSA");
              RSAPublicKey key = (RSAPublicKey) kf.generatePublic(spec);
              String token = map.get("token").toString();
              JWTVerifier verifier = JWT.require(Algorithm.RSA256(key)).withIssuer("auth0").build();
              JWT jwt = (JWT) verifier.verify(token);
              log.info("Token verified " + jwt);
              response = json.message("Token verified ", "OK");
            }
          }
        } catch (JWTVerificationException | JWTCreationException | NoSuchAlgorithmException
            | InvalidKeySpecException exception) {
          log.error(exception);
          response = json.message("ERROR " + exception.toString(), "KO");
        }
      }
    }
    bRunning = false;
    return response;
  }

  public void init(String sIn) {
    try {
      log = LogFactory.getLog(JwtService.class);
      Hashtable<String, String> ht = new Hashtable<String, String>();
      ht.put("java.naming.factory.initial", "com.microlib.jndi.DSInitCtxFactory");
      Context ctx = new InitialContext(ht);
      keyStore = (KeyPairStoreImpl) ctx.lookup(jndiName);
    } catch(NamingException e) {
      // we can't assume that the log is available
    }
  }
}