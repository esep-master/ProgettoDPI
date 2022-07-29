package it.topnetwork.smartdpi.dto.request.auth;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class RecoveryPasswordRequest extends BaseRequest {
	
	private String email;
	
	public RecoveryPasswordRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = Utility.isValid(email);
		return isValid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
