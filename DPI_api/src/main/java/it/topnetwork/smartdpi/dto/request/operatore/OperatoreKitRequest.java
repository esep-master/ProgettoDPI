package it.topnetwork.smartdpi.dto.request.operatore;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class OperatoreKitRequest extends BaseRequest {

	private long idOperatore;
	private long idKit;
	
	public OperatoreKitRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idOperatore != 0 && idKit != 0;
		return isValid;
	}

	public long getIdOperatore() {
		return idOperatore;
	}

	public void setIdOperatore(long idOperatore) {
		this.idOperatore = idOperatore;
	}

	public long getIdKit() {
		return idKit;
	}

	public void setIdKit(long idKit) {
		this.idKit = idKit;
	}

}
