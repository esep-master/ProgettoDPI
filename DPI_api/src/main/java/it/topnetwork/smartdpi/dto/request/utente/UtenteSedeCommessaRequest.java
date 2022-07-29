package it.topnetwork.smartdpi.dto.request.utente;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class UtenteSedeCommessaRequest extends BaseRequest {

	private long idUtente;
	private long idSedeCommessa;

	public UtenteSedeCommessaRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idUtente != 0 && idSedeCommessa != 0;
		return isValid;
	}

	public long getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(long idUtente) {
		this.idUtente = idUtente;
	}

	public long getIdSedeCommessa() {
		return idSedeCommessa;
	}

	public void setIdSedeCommessa(long idSedeCommessa) {
		this.idSedeCommessa = idSedeCommessa;
	}
	
}
