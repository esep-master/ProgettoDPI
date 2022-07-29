package it.topnetwork.smartdpi.dto.request.operatore;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class OperatoreSedeCommessaRequest extends BaseRequest {

	private long idOperatore;
	private long idSedeCommessa;
	
	public OperatoreSedeCommessaRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idOperatore != 0 && idSedeCommessa != 0;
		return isValid;
	}

	public long getIdOperatore() {
		return idOperatore;
	}

	public void setIdOperatore(long idOperatore) {
		this.idOperatore = idOperatore;
	}

	public long getIdSedeCommessa() {
		return idSedeCommessa;
	}

	public void setIdSedeCommessa(long idSedeCommessa) {
		this.idSedeCommessa = idSedeCommessa;
	}
	
}
