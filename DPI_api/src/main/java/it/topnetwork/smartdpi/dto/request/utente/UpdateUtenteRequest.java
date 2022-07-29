package it.topnetwork.smartdpi.dto.request.utente;

public class UpdateUtenteRequest extends InsertUtenteRequest {

	private long idUtente;
	
	public UpdateUtenteRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = idUtente != 0;// && super.isValid();
		
		return isValid;
	}


	public long getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(long idUtente) {
		this.idUtente = idUtente;
	}
	
}
