package com.sv.youapp.bff.internal;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;

public class RequestUtils {
  private static final SecureRandom SR;
  private static final MessageDigest md;
  private static final Base64.Encoder urlEncoder;

  static {
    SecureRandom sr1;
    try {
      sr1 = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException ex) {
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

  public static String resolveNonLoopbackAddress() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface ni = interfaces.nextElement();
        if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
          continue;
        }
        Enumeration<InetAddress> addrs = ni.getInetAddresses();
        while (addrs.hasMoreElements()) {
          InetAddress addr = addrs.nextElement();
          if (!addr.isLoopbackAddress()
              && addr instanceof Inet4Address
              && addr.isSiteLocalAddress()) {
            return addr.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
    return "127.0.0.1";
  }

  public static boolean isRelativeAppRedirect(String redirectUri) {
    if (redirectUri == null) {
      return false;
    }
    String value = redirectUri.trim();
    if (value.isEmpty()) {
      return false;
    }

    if (!value.startsWith("/")) {
      return false;
    }

    final URI uri;
    try {
      uri = new URI(value);
    } catch (URISyntaxException e) {
      return false;
    }
    if (uri.getScheme() != null) {
      return false;
    }
    if (uri.getHost() != null) {
      return false;
    }

    return uri.getRawAuthority() == null;
  }

  public static String encode256UrlSafe() {
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
