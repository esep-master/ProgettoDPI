package it.topnetwork.smartdpi.dto.request.operatore;

public class UpdateOperatoreRequest extends InsertOperatoreRequest {

	private long idOperatore;

	public UpdateOperatoreRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;

		isValid = idOperatore != 0;// && super.isValid();

		return isValid;
	}

	public long getIdOperatore() {
		return idOperatore;
	}

	public void setIdOperatore(long idOperatore) {
		this.idOperatore = idOperatore;
	}

}
