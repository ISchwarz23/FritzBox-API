package de.ingo.fritzbox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingo.fritzbox.exceptions.AuthenticationException;
import de.ingo.fritzbox.utils.Encryption;
import de.ingo.fritzbox.utils.HttpRequest;


/**
 * Connector to establish a connection to a FritzBox device.
 *
 * @author Ingo Schwarz
 */
public final class FritzBoxConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(FritzBoxConnector.class);
	private final String firtzBoxAddress;


	/**
	 * Creates a FritzBox connector to connect to a FritzBox with the URL
	 * "fritz.box".
	 */
	public FritzBoxConnector() {
		this("fritz.box");
	}

	/**
	 * Creates a FritzBox connector to connect to a FritzBox under the given
	 * URL.
	 *
	 * @param urlOfFritzBox
	 *            The URL of the FritzBox (e.g. "fritz.box" or "192.168.178.1")
	 */
	public FritzBoxConnector(final String urlOfFritzBox) {
		this.firtzBoxAddress = urlOfFritzBox;
	}

	/**
	 * Logs in to the FritzBox with the given password.
	 *
	 * @param password
	 *            The password that should be used to log in.
	 * @return A FritzBoxInterface object to access on functionalities of the
	 *         FritzBox.
	 * @throws AuthenticationException
	 *             when user can not be authenticated at the FritzBox (wrong
	 *             password).
	 * @throws IOException
	 */
	public FritzBoxInterface login(final String password) throws AuthenticationException, IOException {
		final String challenge = this.getChallenge();
		final String sid = this.sendLogin(challenge, password);

		if (sid.equals("0000000000000000")) {
			throw new AuthenticationException("Not able to login to FritzBox. Wrong password!");
		}

		return new FritzBoxInterface(this.firtzBoxAddress, sid);
	}

	private String getChallenge() throws IOException {
		URL url;

		String result = "";
		url = new URL("http://" + this.firtzBoxAddress + "/login_sid.lua");
		result = HttpRequest.doGet(url);

		// get challenge challenge
		final String challenge = result.substring(result.indexOf("<Challenge>") + 11,
				result.indexOf("<Challenge>") + 19);

		return challenge;
	}

	private String sendLogin(final String challenge, final String password) throws IOException {
		final String stringToHash = challenge + "-" + password;
		String stringToHashUTF16;
		try {
			stringToHashUTF16 = new String(stringToHash.getBytes("UTF-16LE"), "UTF-8");

			final String md5 = Encryption.hashMD5(stringToHashUTF16);
			final String response = challenge + "-" + md5;

			String result = "0000000000000000";
			final URL url = new URL("http://" + this.firtzBoxAddress + "/login_sid.lua?user=&response=" + response);
			result = HttpRequest.doGet(url);

			// get challenge challenge
			final String sid = result.substring(result.indexOf("<SID>") + 5, result.indexOf("<SID>") + 21);

			return sid;
		} catch (final UnsupportedEncodingException e) {
			// will never appear
			LOGGER.error("UTF-16LE is not supported", e);
			return null;
		} catch (final NoSuchAlgorithmException e) {
			// will never appear
			LOGGER.error("MD5 is not supported", e);
			return null;
		}
	}

}
