package it.topnetwork.smartdpi.dto.request.operatore;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class InsertOperatoreRequest extends BaseRequest {

	private String matricola;
	private String password;
	private String nominativo;
	private String idDispositivo;
	private String numeroTelefono;
	private String email;
	private long idTipoOperatore;
	
	public InsertOperatoreRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		
//		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
//		Matcher m = p.matcher(email);
//		boolean emailValid = m.matches();
		
		isValid = Utility.isValid(matricola) &&
				Utility.isValid(password) &&
				Utility.isValid(nominativo) &&
//				Utility.isValid(numeroTelefono) &&
//				Utility.isValid(email) && emailValid &&
				idTipoOperatore != 0;
		return isValid;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	public String getIdDispositivo() {
		return idDispositivo;
	}

	public void setIdDispositivo(String idDispositivo) {
		this.idDispositivo = idDispositivo;
	}

	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getIdTipoOperatore() {
		return idTipoOperatore;
	}

	public void setIdTipoOperatore(long idTipoOperatore) {
		this.idTipoOperatore = idTipoOperatore;
	}
	
}
