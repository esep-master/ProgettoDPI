package it.topnetwork.smartdpi.dto.request.auth;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class LoginAppRequest extends BaseRequest {
	
	private String matricola;
	private String password;
	
	public LoginAppRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = Utility.isValid(matricola) && Utility.isValid(password); 
		return isValid;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
