package it.topnetwork.smartdpi.dto.request.utente;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class InsertUtenteRequest extends BaseRequest {

	private String username;
	private String password;
	private String email;
	private String numeroTelefono;
	private String nome;
	private String cognome;
	private long   idRuolo;

	public InsertUtenteRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		boolean emailValid = m.matches();
		
		isValid = Utility.isValid(username) &&
				Utility.isValid(password) &&
				Utility.isValid(email) && emailValid &&
				Utility.isValid(numeroTelefono) &&
				Utility.isValid(nome) &&
				Utility.isValid(cognome) &&
				idRuolo != 0;
		return isValid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public long getIdRuolo() {
		return idRuolo;
	}

	public void setIdRuolo(long idRuolo) {
		this.idRuolo = idRuolo;
	}

}
