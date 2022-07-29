package it.topnetwork.smartdpi.dto.request.sede;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class SedeCommessaRequest extends BaseRequest {

	private long idSedeCommessa;
	private String nome;
	private long idCommessa;
	
	public SedeCommessaRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = Utility.isValid(nome) &&
				idCommessa != 0;
		return isValid;
	}

	public long getIdSedeCommessa() {
		return idSedeCommessa;
	}

	public void setIdSedeCommessa(long idSedeCommessa) {
		this.idSedeCommessa = idSedeCommessa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getIdCommessa() {
		return idCommessa;
	}

	public void setIdCommessa(long idCommessa) {
		this.idCommessa = idCommessa;
	}
	
}
