package it.topnetwork.smartdpi.dto.request.auth;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class LoginRequest extends BaseRequest {

	private String username;
	private String password;
	
	public LoginRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = Utility.isValid(username) &&
				Utility.isValid(password);
		return isValid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
