package it.topnetwork.smartdpi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CRMData {
	
	private String matricola;
	private String imei;
	private String idAttivita;
	private String nomeApp;
	private String idTerminale;
	private String commessa;
	private String settore;
	private String operatore;
	private String numeroTelefono;
	private String email;
	
	public CRMData() {}

	@JsonProperty("matricola")
	public String getMatricola() {
		if(matricola == null) {
			return imei;
		}
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@JsonProperty("id_attivita")
	public String getIdAttivita() {
		return idAttivita;
	}

	public void setIdAttivita(String idAttivita) {
		this.idAttivita = idAttivita;
	}

	@JsonProperty("nome_app")
	public String getNomeApp() {
		return nomeApp;
	}

	public void setNomeApp(String nomeApp) {
		this.nomeApp = nomeApp;
	}

	@JsonProperty("id_terminale")
	public String getIdTerminale() {
		return idTerminale;
	}

	public void setIdTerminale(String idTerminale) {
		this.idTerminale = idTerminale;
	}

	public String getCommessa() {
		return commessa;
	}

	public void setCommessa(String commessa) {
		this.commessa = commessa;
	}

	public String getSettore() {
		return settore;
	}

	public void setSettore(String settore) {
		this.settore = settore;
	}

	@JsonProperty("Squadra")
	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	@JsonProperty("telefono")
	public String getNumeroTelefono() {
		if(numeroTelefono == null) {
			return "";
		}
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	@JsonProperty("email")
	public String getEmail() {
		if(email == null) {
			return "";
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "CRMData [imei=" + imei + ", idAttivita=" + idAttivita + ", nomeApp=" + nomeApp + ", idTerminale="
				+ idTerminale + ", commessa=" + commessa + ", settore=" + settore + ", operatore=" + operatore + "]";
	}

}
