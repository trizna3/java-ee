package src.common;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface VravCryptedCommunicator {

	String SIGNATURE_ALGORITHM_CODE = "SHA1withRSA";
	String ENCRYPTION_ALGORITHM_CODE = "RSA";

	String ENCRYPTION_ENCODING = "ISO-8859-1";
	
	public PublicKey getPublicKey();
	public PrivateKey getPrivateKey();

	/**
	 * Returns signature.
	 */
	default String signMessage(String messageText) {
		byte[] message = messageText.getBytes();
		try {
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_CODE);
			signature.initSign(getPrivateKey());
			signature.update(message);
			byte[] sign = signature.sign();
			return new String(sign, ENCRYPTION_ENCODING);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	default boolean evaluateSignature(String messageText, String signBytes) {
		byte[] message = messageText.getBytes();
		try {
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_CODE);
			signature.initVerify(getPublicKey());
			signature.update(message);
			return signature.verify(signBytes.getBytes(ENCRYPTION_ENCODING));
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	default String encrypt(String message) {
		if (message == null) {
			return "";
		}
		Cipher crypto;
		try {
			crypto = Cipher.getInstance(ENCRYPTION_ALGORITHM_CODE);
			crypto.init(Cipher.ENCRYPT_MODE, getPublicKey());
			return new String(crypto.doFinal(message.getBytes(ENCRYPTION_ENCODING)),ENCRYPTION_ENCODING);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	default String decrypt(String message) {
		if (message == null) {
			return "";
		}
		Cipher crypto;
		try {
			crypto = Cipher.getInstance(ENCRYPTION_ALGORITHM_CODE);
			crypto.init(Cipher.DECRYPT_MODE, getPrivateKey());
			return new String(crypto.doFinal(message.getBytes(ENCRYPTION_ENCODING)),ENCRYPTION_ENCODING);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
