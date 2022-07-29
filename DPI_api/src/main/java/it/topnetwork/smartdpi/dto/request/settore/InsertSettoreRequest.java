package it.topnetwork.smartdpi.dto.request.settore;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class InsertSettoreRequest extends BaseRequest {

	private String nome;
	
	public InsertSettoreRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = Utility.isValid(nome);
		
		return isValid;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
