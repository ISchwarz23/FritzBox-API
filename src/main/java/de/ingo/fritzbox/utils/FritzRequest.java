package de.ingo.fritzbox.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Performs a request for enabling/disabling functionalities on the FritzBox.
 * The request will continue until the functionality is triggered.
 *
 * @author Ingo Schwarz
 */
public class FritzRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FritzRequest.class);
	private final URL requestUrl;


	/**
	 * Creates a FritzRequest object.
	 *
	 * @param requestUrl
	 *            The URL of the functionality to trigger.
	 */
	public FritzRequest(final URL requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * Executes the request non-blocking (asynchronous). Exceptions during
	 * communication are swallowed.
	 */
	public void execute() {
		new Thread() {

			@Override
			public void run() {
				try {
					FritzRequest.this.executeBlocking();
				} catch (final Throwable e) {
					LOGGER.error("Exception during async FritzRequest", e);
				}
			}

		}.start();
	}

	/**
	 * Executes the request blocking (synchronous).
	 *
	 * @throws IOException
	 *             Exception during communication with the FritzBox.
	 */
	public void executeBlocking() throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.requestUrl.openConnection().getInputStream()));

			StringBuilder response;
			do {
				response = new StringBuilder();
				String inputLine;

				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					// do nothing
				}

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} while (response.toString().equals("done:0"));

			in.close();
		} finally {
			try {
				in.close();
			} catch (final IOException e) {
				// ignore
			}
		}
	}

}
