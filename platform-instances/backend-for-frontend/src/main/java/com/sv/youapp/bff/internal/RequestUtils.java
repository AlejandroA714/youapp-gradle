package com.sv.youapp.bff.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class RequestUtils {
	private static final SecureRandom SR;

	private static final Base64.Encoder urlEncoder;

	private static final MessageDigest md;

	static {
		SecureRandom sr1;
		try{
			sr1 = SecureRandom.getInstanceStrong();
		}catch (NoSuchAlgorithmException ex){
			sr1 = new SecureRandom();
		}
		SR = sr1;
		urlEncoder = Base64.getUrlEncoder().withoutPadding();
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encode256UrlSafe(){
		byte[] buf = new byte[32];
		SR.nextBytes(buf);
		return urlEncoder.encodeToString(buf);
	}

	public static String generateCodeVerifier() {
		return encode256UrlSafe();
	}

	public static String codeChallengeS256(String codeVerifier) {
		byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
		return urlEncoder.encodeToString(digest);
	}

}
