package com.xianzhi.webtool;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

	public static final String ALGORITHMS = "RSA";
	public static final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRhVeb6zaVRYfGu1j2VouyTUNExKFZExFrAKTTyybXW+KvO2uypiYTuP7QIPhopxJ7PkZ8XUumPNBJec+bGZ4s7I8+PDwlgkXHlnitshEdQEMnfZN+hjv7wFqVy7GGMF7vS0JB3tj+AhqxO/gcLefdh9t1/tXw+n3kQrPUlUnK1wIDAQAB";
	
	public static String encrypt(String content, String key) {
		try {
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHMS, key);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);
			byte plaintext[] = content.getBytes("UTF-8");
			byte[] output = cipher.doFinal(plaintext);
			String s = new String(Base64.encode(output));
			return s;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @param algorithm
	 * @param ins
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws AlipayException
	 */
	private static PublicKey getPublicKeyFromX509(String algorithm,
			String bysKey) throws NoSuchAlgorithmException, Exception {
		byte[] decodedKey = Base64.decode(bysKey);
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(x509);
	}
}