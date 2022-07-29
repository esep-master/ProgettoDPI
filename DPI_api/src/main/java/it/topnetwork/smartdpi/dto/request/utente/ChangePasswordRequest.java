package it.topnetwork.smartdpi.dto.request.utente;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class ChangePasswordRequest extends BaseRequest {

	private String oldPassword;
	private String newPassword;
	
	public ChangePasswordRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = Utility.isValid(oldPassword) &&
				Utility.isValid(newPassword);
		return isValid;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}
