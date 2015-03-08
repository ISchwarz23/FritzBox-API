package de.ingo.fritzbox.data;


/**
 * This enumeration contains all known security modes. Every security mode
 * contains the value with which they are identified at the FritzBox.
 *
 * @author Ingo Schwarz
 */
public enum SecurityMode {

	/**
	 * WPA (TKIP)
	 */
	WPA(2),

	/**
	 * WPA2 (CCMP)
	 */
	WPA2(3),

	/**
	 * WPA + WPA2
	 */
	WPA_WPA2(4),

	/**
	 * No security
	 */
	NONE(5);

	private int value;

	SecurityMode(final int value) {
		this.value = value;
	}

	/**
	 * Gives the value the FritzBox is working with.
	 *
	 * @return The value of the SecurityMode.
	 */
	public int getValue() {
		return this.value;
	}

}
