package com.ap.rest.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

public class CreateTweet {
	
	public static void main(String[] args) {
		try {
			sendTweetWithMedia("{\"text\" : \"Test\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String CONSUMER_KEY  = "T8GIzsRV4YOXxrAHzr10WgKO2";
	public static String CONSUMER_SECRET  = "fxdy0wK4oLjvl6p9InBXy0RECAtJ2A4KP4ocemkgyTgr5H9v6t";
	public static String ACCESS_TOKEN  = "1711345213023682561-Hh9VcLpvndRspraeLBlfloaz1LB4KH";
	public static String ACCESS_TOKEN_SECRET  = "mFOdkd25h0ySjUZa4XOIbJqdivUZfmlXtRJfxUEG82aNi";

	public static String requestUrl = "https://api.twitter.com/2/tweets";
	    
	     
	    public static void sendTweetWithMedia(String body) throws IOException {
	         String tweetUrl = "https://api.twitter.com/2/tweets";

	         // Generate the OAuth parameters
	         Map<String, String> oauthParams = generateOAuthParams();

	         // Generate the OAuth signature
	         String signature = null;
	         try {
	          signature = generateOAuthSignature(requestUrl, "POST", oauthParams);
	         } catch (Exception e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	         }

	         // Build the OAuth authorization header
	         String oauthHeader = buildOAuthHeader(oauthParams, signature);

	         System.out.println("oauthHeader: " + oauthHeader);
	         URL url = new URL(requestUrl);
	         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	         connection.setRequestMethod("POST");
	         connection.setRequestProperty("Authorization", oauthHeader);
	         connection.setRequestProperty("Content-Type", "application/json");
	         connection.setDoOutput(true);

	         OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	         writer.write(body);
	         writer.flush();

	         int responseCode = connection.getResponseCode();
	         System.out.println("Response code: " + responseCode);

	         writer.close();
	         connection.disconnect();
	     }

	     private static Map<String, String> generateOAuthParams() {
	         Map<String, String> params = new HashMap<>();
	         params.put("oauth_consumer_key", CONSUMER_KEY);
	         params.put("oauth_token", ACCESS_TOKEN);
	         params.put("oauth_signature_method", "HMAC-SHA1");
	         params.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
	         params.put("oauth_nonce", generateNonce());
	         params.put("oauth_version", "1.0");
	         return params;
	     }

	     private static String generateOAuthSignature(String requestUrl, String requestMethod, Map<String, String> oauthParams) throws Exception {
	         // Combine the request parameters and OAuth parameters
	         Map<String, String> allParams = new HashMap<>(oauthParams);

	         // Sort the parameters alphabetically by key
	         String[] sortedKeys = allParams.keySet().toArray(new String[0]);
	         Arrays.sort(sortedKeys);

	         // Construct the parameter string
	         StringBuilder paramBuilder = new StringBuilder();
	         for (String key : sortedKeys) {
	             if (paramBuilder.length() > 0) {
	                 paramBuilder.append("&");
	             }
	             paramBuilder.append(key).append("=").append(allParams.get(key));
	         }
	         String parameterString = paramBuilder.toString();

	         // Construct the base string
	         String baseString = requestMethod + "&" + encode(requestUrl) + "&" + encode(parameterString);

	         // Construct the signing key
	         String signingKey = encode(CONSUMER_SECRET) + "&" + encode(ACCESS_TOKEN_SECRET);

	         // Generate the HMAC-SHA1 signature
	         Mac mac = Mac.getInstance("HmacSHA1");
	         SecretKeySpec secretKey = new SecretKeySpec(signingKey.getBytes(), "HmacSHA1");
	         mac.init(secretKey);
	         byte[] baseStringBytes = baseString.getBytes("UTF-8");
	         byte[] signatureBytes = mac.doFinal(baseStringBytes);
	         String signature = new String(Base64.getEncoder().encode(signatureBytes));
	         return signature;
	     }

	     private static String buildOAuthHeader(Map<String, String> oauthParams, String signature) {
	         StringBuilder headerBuilder = new StringBuilder();
	         headerBuilder.append("OAuth ");

	         List<String> encodedParams = new ArrayList<>();

	         encodedParams.add("oauth_consumer_key=\"" + encode(oauthParams.get("oauth_consumer_key")) + "\"");
	         encodedParams.add("oauth_token=\"" + encode(oauthParams.get("oauth_token")) + "\"");
	         encodedParams.add("oauth_signature_method=\"" + encode(oauthParams.get("oauth_signature_method")) + "\"");
	         encodedParams.add("oauth_timestamp=\"" + encode(oauthParams.get("oauth_timestamp")) + "\"");
	         encodedParams.add("oauth_nonce=\"" + encode(oauthParams.get("oauth_nonce")) + "\"");
	         encodedParams.add("oauth_version=\"" + encode(oauthParams.get("oauth_version")) + "\"");
	         encodedParams.add("oauth_signature=\"" + encode(signature) + "\"");

	         String header = String.join(", ", encodedParams);
	         headerBuilder.append(header);

	         return headerBuilder.toString();
	     }

	     private static String encode(String value) {
	         try {
	             return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	         } catch (UnsupportedEncodingException e) {
	             throw new RuntimeException("Failed to encode parameter: " + value, e);
	         }
	     }

	     private static String generateNonce() {
	         String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	         Random rand = new Random();
	         StringBuilder nonceBuilder = new StringBuilder();
	         for (int i = 0; i < 32; i++) {
	             nonceBuilder.append(characters.charAt(rand.nextInt(characters.length())));
	         }
	         return nonceBuilder.toString();
	     }

}
