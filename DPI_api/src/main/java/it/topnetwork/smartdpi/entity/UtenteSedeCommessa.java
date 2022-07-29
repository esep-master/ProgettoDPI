package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "utenti_sedi_commesse")
@Where(clause = "data_cancellazione > SYSDATE()")
public class UtenteSedeCommessa extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_utente", nullable = false)
	@JsonIgnoreProperties("utenteSediCommesse")
	private Utente utente;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_sede_commessa", nullable = false)
	@JsonIgnoreProperties("utenteSediCommesse")
	private SedeCommessa sedeCommessa;
	
	public UtenteSedeCommessa() {
		super();
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public SedeCommessa getSedeCommessa() {
		return sedeCommessa;
	}

	public void setSedeCommessa(SedeCommessa sedeCommessa) {
		this.sedeCommessa = sedeCommessa;
	}
	
}
