package com.khoi.customer.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenUtil {
  public static final String MAGIC_KEY = "MyMagicKey";

  /**
   * <p>This method receives information of the user then create an access token</p>
   * @param userDetails details of one's user
   * @return access token for that user
   */
  public static String createToken(UserDetails userDetails) {
    long expires = System.currentTimeMillis() + 1000L * 60 * 60;
    return userDetails.getUsername() + ":" + expires + ":" + computeSignature(userDetails, expires);
  }

  /**
   * <p>This methods generate a unique string considered as user access token using some magical algorithm</p>
   * @param userDetails information of the user
   * @param expires valid time for the token
   * @return a unique access token for user
   */
  public static String computeSignature(UserDetails userDetails, long expires) {
    StringBuilder signatureBuilder = new StringBuilder();
    signatureBuilder.append(userDetails.getUsername()).append(":");
    signatureBuilder.append(expires).append(":");
    signatureBuilder.append(userDetails.getPassword()).append(":");
    signatureBuilder.append(TokenUtil.MAGIC_KEY);

    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No MD5 algorithm available!");
    }
    return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
  }

  /**
   * <p>Return username of received access token</p>
   * @param authToken user access token
   * @return username of that access token
   */
  public static String getUserNameFromToken(String authToken) {
    if (authToken == null) {
      return null;
    }
    String[] parts = authToken.split(":");
    return parts[0];
  }

  /**
   * <p>Validate that token still valid and belongs to that user who provided this token</p>
   * @param authToken Token that user provides
   * @param userDetails Information of user who provides this token
   * @return a boolean value according to that toke validation
   */
  public static boolean validateToken(String authToken, UserDetails userDetails) {
    String[] parts = authToken.split(":");
    long expires = Long.parseLong(parts[1]);
    String signature = parts[2];
    String signatureToMatch = computeSignature(userDetails, expires);
    return expires >= System.currentTimeMillis() && signature.equals(signatureToMatch);
  }
}
