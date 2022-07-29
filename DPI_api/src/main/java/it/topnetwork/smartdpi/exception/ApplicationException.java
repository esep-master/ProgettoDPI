package it.topnetwork.smartdpi.exception;

public class ApplicationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public ApplicationException(int code, String  message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ApplicationException [code=" + code + ", getMessage()=" + getMessage() + "]";
	}

}
