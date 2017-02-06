package com.microlib.controller;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import java.util.Map;
import java.util.List;
import com.microlib.dataformat.*;

public class JwtService implements ExecInterface {

	int nLoop = 0;
	private boolean bRunning = false;
	private String name;
	private static org.apache.commons.logging.Log log;

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
        JsonFormat json = new JsonFormat();
		
        bRunning = true;
		if (null == map.get("action")) {
			response = json.message("ERROR no action command found ","KO");
		} else {
			log.info("map " + map.get("action"));
			if (map.get("action").toString().equals("getToken")) {
				try {
   					String token = JWT.create().withIssuer("auth0").sign(Algorithm.HMAC256("secret"));
					log.info("Token " + token);
					response = json.message("Token " + token ,"OK");
				} catch (JWTCreationException|UnsupportedEncodingException exception) {
					log.error(exception);
					response = json.message("ERROR " + exception.toString(),"KO");
				}
			} else if (map.get("action").toString().equals("createAndSignToken")) {
				try {
					// create a token demo with our generated private rsa key
					byte[] keyBytes = Files.readAllBytes(new File("private_key.der").toPath());
					PKCS8EncodedKeySpec spec =  new PKCS8EncodedKeySpec(keyBytes);
    				KeyFactory kf = KeyFactory.getInstance("RSA");
					RSAPrivateKey key = (RSAPrivateKey)kf.generatePrivate(spec);
					String token = JWT.create().withIssuer("auth0").sign(Algorithm.RSA256(key));
					log.info("Signed Token : " + token);
					response = json.message("Signed Token " + token ,"OK");
				} catch (JWTCreationException|IOException|NoSuchAlgorithmException|InvalidKeySpecException exception){
					log.error(exception);
					response = json.message("ERROR " + exception.toString(),"KO");
				}	
			} else if (map.get("action").toString().equals("verifyToken")) {
				try {
					byte[] keyBytes = Files.readAllBytes(new File("public_key.der").toPath());
					X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    				KeyFactory kf = KeyFactory.getInstance("RSA");
					RSAPublicKey key = (RSAPublicKey)kf.generatePublic(spec);
					String token = map.get("token").toString();
    				JWTVerifier verifier = JWT.require(Algorithm.RSA256(key)).withIssuer("auth0").build(); 
					JWT jwt = (JWT)verifier.verify(token);
					log.info("Token verified " + jwt);
					response = json.message("Token verified ", "OK");
				} catch (JWTVerificationException|JWTCreationException|IOException|NoSuchAlgorithmException|InvalidKeySpecException exception){
					log.error(exception);
					response = json.message("ERROR " + exception.toString(),"KO");
				}
			}
		}
		bRunning = false;
        return response;
	}

	public void init(String sIn) {
		log = LogFactory.getLog(JwtService.class);
	}
}