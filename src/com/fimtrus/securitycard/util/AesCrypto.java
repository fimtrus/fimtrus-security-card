package com.fimtrus.securitycard.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ResourceBundle;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import twitter4j.internal.http.BASE64Encoder;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.jhlibrary.util.ByteUtils;
import com.jhlibrary.util.Util;


/**
 * @author jong-hyun.jeong
 *  AES 128 UTIL
 *  encrypt : 암호화
 *  decrypt : 복호화
 */
public class AesCrypto {
	
	private static final String CRYPTO_NAME = "AES";
	public static final String TRANSFORM = "AES/ECB/PKCS7Padding";
	public static final String KEY_GENERATED_KEY = "generated_key";
	
	public static String encrypt(Context context, String plainText) throws Exception {
		
//		ResourceBundle resource = ResourceBundle.getBundle("assets.auth-config");
		String key = (String) Util.getPreference(context, AesCrypto.KEY_GENERATED_KEY);
		KeyGenerator kgen = KeyGenerator.getInstance(CRYPTO_NAME);
		kgen.init(128);
		byte[] raw = key.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, CRYPTO_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORM);

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes());
		return asHex(encrypted);
	}
	public static String encrypt(Context context, String key, String plainText) throws Exception {
		
//		ResourceBundle resource = ResourceBundle.getBundle("assets.auth-config");
		
		KeyGenerator kgen = KeyGenerator.getInstance(CRYPTO_NAME);
		kgen.init(128);
		byte[] raw = new byte[16];
		byte[] keyBytes = key.getBytes();
		int keyByteLength = keyBytes.length;
		
		for ( int i = 0; i < 16; i++ ) {
			if ( i < keyByteLength ) {
				raw[i] = keyBytes[i];
			} else {
				raw[i] = 0x0;
			}
		}
		
		SecretKeySpec skeySpec = new SecretKeySpec(raw, CRYPTO_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORM);
		
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes());
		return asHex(encrypted);
	}
	
	public static SecretKeySpec getSecretKeySpec ( String key ) {
		
		byte[] raw = new byte[16];
		byte[] keyBytes = key.getBytes();
		int keyByteLength = keyBytes.length;
		
		for ( int i = 0; i < 16; i++ ) {
			if ( i < keyByteLength ) {
				raw[i] = keyBytes[i];
			} else {
				raw[i] = 0x0;
			}
		}
		
		SecretKeySpec skeySpec = new SecretKeySpec(raw, CRYPTO_NAME);
		return skeySpec;
	}

	public static String decrypt(Context context, String key, String cipherText) throws Exception {
		
//		ResourceBundle resource = ResourceBundle.getBundle("assets.auth-config");
//		String key = resource.getString("crypt.key");
		
		KeyGenerator kgen = KeyGenerator.getInstance(CRYPTO_NAME);
		kgen.init(128);
		byte[] raw = new byte[16];
		byte[] keyBytes = key.getBytes();
		int keyByteLength = keyBytes.length;
		
		for ( int i = 0; i < 16; i++ ) {
			if ( i < keyByteLength ) {
				raw[i] = keyBytes[i];
			} else {
				raw[i] = 0x0;
			}
		}
		
		SecretKeySpec skeySpec = new SecretKeySpec(raw, CRYPTO_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORM);
		
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal(fromString(cipherText));
		String originalString = new String(original);
		return originalString;
	}
	public static String decrypt(Context context, String cipherText) throws Exception {
		
//		ResourceBundle resource = ResourceBundle.getBundle("assets.auth-config");
//		String key = resource.getString("crypt.key");
		String key = (String) Util.getPreference(context, AesCrypto.KEY_GENERATED_KEY);
		
		KeyGenerator kgen = KeyGenerator.getInstance(CRYPTO_NAME);
		kgen.init(128);

		byte[] raw = key.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, CRYPTO_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORM);

		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal(fromString(cipherText));
		String originalString = new String(original);
		return originalString;
	}

	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	private static byte[] fromString(String hex) {
		int len = hex.length();
		byte[] buf = new byte[((len + 1) / 2)];

		int i = 0, j = 0;
		if ((len % 2) == 1)
			buf[j++] = (byte) fromDigit(hex.charAt(i++));

		while (i < len) {
			buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex
					.charAt(i++)));
		}
		return buf;
	}

	private static int fromDigit(char ch) {
		if (ch >= '0' && ch <= '9')
			return ch - '0';
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 10;
		if (ch >= 'a' && ch <= 'f')
			return ch - 'a' + 10;

		throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
	}
	
	public static String generateKey ( Context context ) {
		KeyGenerator generator = null;
		SecureRandom random = null;
		String key = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			generator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		generator.init(128, random);
		Key secureKey = generator.generateKey();
		if ( secureKey != null ) {
			key = Base64.encodeToString(secureKey.getEncoded(), Base64.DEFAULT);
			Util.setPreference(context, KEY_GENERATED_KEY, key.substring(0, 16));
			Toast.makeText(context, "generate key success!", Toast.LENGTH_SHORT).show();
			
		} else {
			
			Toast.makeText(context, "generate key failed!", Toast.LENGTH_SHORT).show();
		}
		return key;
	}
}
