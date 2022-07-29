package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "funzioni_ruoli")
@Where(clause = "data_cancellazione > SYSDATE()")
public class FunzioneRuolo extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_ruolo", nullable = false)
	@JsonIgnoreProperties("funzioniRuolo")
	private Ruolo ruolo;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_funzione", nullable = false)
	@JsonIgnoreProperties("funzioniRuolo")
	private Funzione funzione;
	
	public FunzioneRuolo() {
		super();
	}

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	public Funzione getFunzione() {
		return funzione;
	}

	public void setFunzione(Funzione funzione) {
		this.funzione = funzione;
	}
	
}
