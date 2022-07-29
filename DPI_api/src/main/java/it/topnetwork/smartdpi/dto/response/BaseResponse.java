package it.topnetwork.smartdpi.dto.response;

import it.topnetwork.smartdpi.exception.ApplicationException;

public class BaseResponse {

	private String apiVersion;
	private int    code;
	private String message;
	
	public BaseResponse(String apiVersion) {
		this.apiVersion = apiVersion;
		this.code = 200;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setException(ApplicationException e) {
		this.code = e.getCode();
		this.message = e.getMessage();
	}
	
}
