package it.topnetwork.smartdpi.dto.response.model;

import java.util.List;

import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.entity.Utente;

public class LoginResponse {
	
	private Utente user;
	private List<Configurazione> configurazioni;
	private String token;
	
	public LoginResponse() {}

	public Utente getUser() {
		return user;
	}

	public void setUser(Utente user) {
		this.user = user;
	}

	public List<Configurazione> getConfigurazioni() {
		return configurazioni;
	}

	public void setConfigurazioni(List<Configurazione> configurazioni) {
		this.configurazioni = configurazioni;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
