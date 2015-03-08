package de.ingo.fritzbox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingo.fritzbox.data.Call;
import de.ingo.fritzbox.data.SecurityMode;
import de.ingo.fritzbox.utils.CsvParser;
import de.ingo.fritzbox.utils.FritzRequest;
import de.ingo.fritzbox.utils.HttpRequest;


/**
 * The interface for FritzBox devices to access specific features. This
 * interfaces is provided by a FritzBoxConnector.
 *
 * @author Ingo Schwarz
 */
public final class FritzBoxInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(FritzBoxInterface.class);

	private final String fritzBoxAddress;
	private final String sid;


	protected FritzBoxInterface(final String firtzBoxAddress, final String sid) {
		this.fritzBoxAddress = firtzBoxAddress;
		this.sid = sid;
	}

	/**
	 * Gives the session ID of the current connection.
	 *
	 * @return The session id as String-object.
	 */
	public String getSessionID() {
		return this.sid;
	}

	/**
	 * Turns on a WiFi guest access point with the given SSID and the given
	 * password.
	 *
	 * @param ssid
	 *            The SSID of the guest access point.
	 * @param password
	 *            The password for the guest access point. (Should be a string
	 *            of at least 8 characters)
	 * @throws IOException
	 */
	public void turnOnGuestWiFi(final String ssid, final String password) throws IOException {
		this.turnOnGuestWiFi(ssid, SecurityMode.WPA2, password, false, true, false);
	}

	/**
	 * Turns on a WiFi guest access point with the given properties.
	 *
	 * @param ssid
	 *            The SSID of the guest access point.
	 * @param securityMode
	 *            The encryption of the WiFi.
	 * @param password
	 *            The password for the guest access point. (Should be a string
	 *            of at least 8 characters) This is only needed if the
	 *            SecurityMode differs from "NONE".
	 * @param activatePushService
	 *            Whether or not to inform about client connections and
	 *            disconnections from the guest WiFi. (The push service has to
	 *            be set up on the FritzBox)
	 * @param limitWebAccess
	 *            Allow the clients in the guest WiFi only the to browse the web
	 *            and send mails.
	 * @param allowClientCommunication
	 *            Whether or not to allow the clients inside the guest WiFi to
	 *            communicate to each other.
	 * @throws IOException
	 */
	public void turnOnGuestWiFi(final String ssid, final SecurityMode securityMode, final String password,
			final boolean activatePushService, final boolean limitWebAccess, final boolean allowClientCommunication)
			throws IOException {

		// missing values:

		// down_time_value
		// disconnect_guest_access

		try {
			final URL requestUrl = new URL("http://" + this.fritzBoxAddress + "/wlan/guest_access.lua?sid=" + this.sid);
			final StringBuilder paramBuilder = new StringBuilder();
			paramBuilder.append("activate_guest_access=on");
			paramBuilder.append("&autoupdate=on");
			paramBuilder.append("&guest_ssid=" + ssid);
			paramBuilder.append("&sec_mode=" + securityMode.getValue());
			paramBuilder.append("&wpa_key=" + password);
			if (activatePushService) {
				paramBuilder.append("&push_service=on");
			}
			if (limitWebAccess) {
				paramBuilder.append("&group_access=on");
			}
			if (allowClientCommunication) {
				paramBuilder.append("&user_isolation=on");
			}

			HttpRequest.doPost(requestUrl, paramBuilder.toString());
		} catch (final MalformedURLException e) {
			// will never appear
			LOGGER.warn("Malformed turn on guest WiFi request URL", e);
			throw e;
		}
	}

	/**
	 * Turns of the guest WiFi. This has only an effect if the guest WiFi was
	 * enabled.
	 *
	 * @throws IOException
	 */
	public void turnOffGuestWiFi() throws IOException {
		try {
			final URL requestUrl = new URL("http://" + this.fritzBoxAddress + "/wlan/guest_access.lua?sid=" + this.sid);
			final String params = "autoupdate=on";
			HttpRequest.doPost(requestUrl, params);
		} catch (final MalformedURLException e) {
			// will never appear
			LOGGER.warn("Malformed turn off guest WiFi request URL", e);
			throw e;
		}
	}

	/**
	 * Gives the IP address which the FritzBox is identified by in the Internet.
	 *
	 * @return The IP of the FritzBox as String.
	 * @throws IOException
	 */
	public String getInternetIP() throws IOException {

		try {
			final URL requestUrl = new URL("http://" + this.fritzBoxAddress + "/internet/inetstat_monitor.lua?sid="
					+ this.sid);
			String result = HttpRequest.doGet(requestUrl);

			result = result.substring(result.indexOf("IP-Adresse: ") + 12);
			result = result.substring(0, result.indexOf("</span></div></td></tr><tr>"));

			return result;
		} catch (final MalformedURLException e) {
			// will never appear
			LOGGER.warn("Malformed IP request URL", e);
			throw e;
		}
	}

	/**
	 * Reconnects the FritzBox to the Internet. This will cause an IP change.
	 *
	 * @throws IOException
	 */
	public void reconnectToInternet() throws IOException {
		try {
			// disconnect
			URL requestUrl = new URL("http://" + this.fritzBoxAddress + "/internet/inetstat_monitor.lua?sid="
					+ this.sid + "&useajax=1&action=disconnect");
			new FritzRequest(requestUrl).executeBlocking();

			// connect again
			requestUrl = new URL("http://" + this.fritzBoxAddress + "/internet/inetstat_monitor.lua?sid=" + this.sid
					+ "&useajax=1&action=connect");
			new FritzRequest(requestUrl).executeBlocking();

			// wait 7 seconds to be sure that FitzBox is connected again
			Thread.sleep(7000);
		} catch (final MalformedURLException e) {
			// will never appear
			LOGGER.warn("Malformed reconnect request URL", e);
			throw e;
		} catch (final InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Requests the call list from the FritzBox and returns it.
	 *
	 * @return A list of call objects.
	 * @throws IOException
	 *             when an exception during communication occurred.
	 */
	public List<Call> getCallList() throws IOException {
		try {
			final URL requestUrl = new URL("http://" + this.fritzBoxAddress + "/fon_num/foncalls_list.lua?sid="
					+ this.sid + "&csv=");
			final String response = HttpRequest.doGet(requestUrl);
			return CsvParser.parseCallList(response.toString());
		} catch (final MalformedURLException e) {
			// will never appear
			LOGGER.warn("Malformed CallList request URL", e);
			throw e;
		}
	}

}
