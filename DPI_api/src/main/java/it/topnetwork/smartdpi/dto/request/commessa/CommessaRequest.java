package it.topnetwork.smartdpi.dto.request.commessa;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class CommessaRequest extends BaseRequest {

	private long idCommessa;
	private String nome;
	private long idSettore;
	
	public CommessaRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = Utility.isValid(nome) &&
				idSettore != 0;
		return isValid;
	}

	public long getIdCommessa() {
		return idCommessa;
	}

	public void setIdCommessa(long idCommessa) {
		this.idCommessa = idCommessa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getIdSettore() {
		return idSettore;
	}

	public void setIdSettore(long idSettore) {
		this.idSettore = idSettore;
	}
	
}
