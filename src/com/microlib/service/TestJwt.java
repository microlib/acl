package com.microlib.service;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;

public class TestJwt {

	public static void main(String args[]) {
		// create token demo
		try {
    		String token = JWT.create().withIssuer("auth0").sign(Algorithm.HMAC256("secret"));
			System.out.println("Token " + token);
		} catch (JWTCreationException|UnsupportedEncodingException exception){
    			
		}

		String token = "";
		
		try {
			// create a token demo with our generated private rsa key
			byte[] keyBytes = Files.readAllBytes(new File("private_key.der").toPath());
			PKCS8EncodedKeySpec spec =  new PKCS8EncodedKeySpec(keyBytes);
    		KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPrivateKey key = (RSAPrivateKey)kf.generatePrivate(spec);
			token = JWT.create().withIssuer("auth0").sign(Algorithm.RSA256(key));
			System.out.println("Signed Token : " + token);
		} catch (JWTCreationException|IOException|NoSuchAlgorithmException|InvalidKeySpecException e){
			e.printStackTrace();
		}	

		try {
			byte[] keyBytes = Files.readAllBytes(new File("public_key.der").toPath());
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    		KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey key = (RSAPublicKey)kf.generatePublic(spec);
			token =  "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRoMCJ9.gF-JDds389H5l4tk2o7qpuSIzSAEgjfVwTb7c3Tf1InuD7EWk5gjY4kKPP__MGc39HfOobjqUMsUFAJBAJYOJxKmfLMBCLr5TXMMeLcc3-qZw3NZ0DDhq76yLiVA_P3pBm1k-kKtZQvwRY8VrLN9JfBm0BDy3f2wvNRmDXQLHAU33fi4zACpGcTJ9TfNBoY84sOGUBhd73yxPLr4lBhYrFjcqGboZDNzg2LdisTVP1I_9KlHA4d8-H5LHYOcwiFD-hFZteKl52jslKfNucHgrhn0D1iLf4YiE92yNVobLAkVN_qPG8ZX8sNlA5AahIqKenk6hK_C0f1LTGzc6ZxXMA";
			//JWT jwt = JWT.decode(token);
    		JWTVerifier verifier = JWT.require(Algorithm.RSA256(key)).withIssuer("auth0").build(); 
			JWT jwt = (JWT)verifier.verify(token);
			System.out.println("Token verified " + jwt);
		} catch (JWTVerificationException|JWTCreationException|IOException|NoSuchAlgorithmException|InvalidKeySpecException e){
			e.printStackTrace();
		}
	}
}
