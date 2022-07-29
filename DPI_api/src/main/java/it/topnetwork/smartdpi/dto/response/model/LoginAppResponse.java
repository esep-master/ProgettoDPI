package it.topnetwork.smartdpi.dto.response.model;

import java.util.List;

import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.entity.TipoAzioneOperatore;
import it.topnetwork.smartdpi.entity.Utente;

public class LoginAppResponse {

	private Operatore operatore;
	private List<Utente> admin;
	private List<TipoAzioneOperatore> tipiAzioneOperatori;
	private List<Configurazione> configurazioni;
	private List<TipoAllarme> tipiAllarmi;
	private String token;
	
	public LoginAppResponse() {}

	public Operatore getOperatore() {
		return operatore;
	}

	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	public List<Utente> getAdmin() {
		return admin;
	}

	public void setAdmin(List<Utente> admin) {
		this.admin = admin;
	}
	
	public List<TipoAzioneOperatore> getTipiAzioneOperatori() {
		return tipiAzioneOperatori;
	}

	public void setTipiAzioneOperatori(List<TipoAzioneOperatore> tipiAzioneOperatori) {
		this.tipiAzioneOperatori = tipiAzioneOperatori;
	}

	public List<Configurazione> getConfigurazioni() {
		return configurazioni;
	}

	public void setConfigurazioni(List<Configurazione> configurazioni) {
		this.configurazioni = configurazioni;
	}

	public List<TipoAllarme> getTipiAllarmi() {
		return tipiAllarmi;
	}

	public void setTipiAllarmi(List<TipoAllarme> tipiAllarmi) {
		this.tipiAllarmi = tipiAllarmi;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
