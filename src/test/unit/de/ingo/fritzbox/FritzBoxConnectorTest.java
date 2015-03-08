package de.ingo.fritzbox;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import de.ingo.fritzbox.exceptions.AuthenticationException;

public class FritzBoxConnectorTest {
	
	private FritzBoxConnector cut;

	@Before
	public void setUp() throws Exception {
		this.cut = new FritzBoxConnector();
	}

	@Test(expected = AuthenticationException.class)
	public void wrongPasswordTest() throws IOException, ParseException {
		cut.login("wrong password");
	}

}
