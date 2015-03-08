package de.ingo.fritzbox.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Encryption utility class to encrypt a string.
 *
 * @author Ingo Schwarz
 */
public class Encryption {

	/**
	 * Creates a MD5 hash for the given string.
	 *
	 * @param stringToHash
	 *            The string that should be hashed.
	 * @return The hash as hexadecimal string.
	 * @throws NoSuchAlgorithmException
	 */
	public static String hashMD5(final String stringToHash) throws NoSuchAlgorithmException {
		MessageDigest digest;
		digest = MessageDigest.getInstance("MD5");
		digest.update(stringToHash.getBytes());

		final byte byteData[] = digest.digest();
		final BigInteger bigInt = new BigInteger(1, byteData);

		return bigInt.toString(16);
	}

}
