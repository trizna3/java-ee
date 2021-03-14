package src;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public interface VravCryptedCommunicator {

	String ALGORITHM_CODE = "SHA1withRSA";

	String SIGNATURE_ENCODING = "ISO-8859-1";
	
	public PublicKey getPublicKey();
	public PrivateKey getPrivateKey();

	/**
	 * Returns signature.
	 */
	default String signMessage(String messageText) {
		byte[] message = messageText.getBytes();
		try {
			Signature signature = Signature.getInstance(ALGORITHM_CODE);
			signature.initSign(getPrivateKey());
			signature.update(message);
			byte[] sign = signature.sign();
			return new String(sign, SIGNATURE_ENCODING);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	default boolean evaluateSignature(String messageText, String signBytes) {
		byte[] message = messageText.getBytes();
		try {
			Signature signature = Signature.getInstance(ALGORITHM_CODE);
			signature.initVerify(getPublicKey());
			signature.update(message);
			return signature.verify(signBytes.getBytes(SIGNATURE_ENCODING));
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}
}
