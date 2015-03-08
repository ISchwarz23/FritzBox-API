package de.ingo.fritzbox.exceptions;


/**
 * Exception for authentication issues.
 *
 * @author Ingo Schwarz
 */
public class AuthenticationException extends RuntimeException {

	private static final long serialVersionUID = -392552264113059122L;

	/**
	 * Creates a AuthenthicationException.
	 * 
	 * @param exceptionMessage
	 *            The message of the exception.
	 */
	public AuthenticationException(final String exceptionMessage) {
		super(exceptionMessage);
	}

}
