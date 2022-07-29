package it.topnetwork.smartdpi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public class BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	//#cambiato da False a True per permettere di utilizzare il metodo setDatiCancellazione, altrimenti non avrebbe aggiornato la data_cancellazione
	@Column(name = "data_cancellazione", insertable = false, updatable = true)
	@JsonIgnore
	protected Date dataCancellazione;

	@Column(name = "utente_inserimento")
	@JsonIgnore
	protected Long utenteInserimento;
	
	@Column(name = "data_inserimento")
	@JsonIgnore
	protected Date dataInserimento;
	
	@Column(name = "utente_ultima_modifica")
	@JsonIgnore
	protected Long utenteUltimaModifica;
	
	@Column(name = "data_ultima_modifica")
	@JsonIgnore
	protected Date dataUltimaModifica;

	public BaseEntity() {}
	
	/**
	 * imposta dati inserimento
	 * @param idUtente
	 */
	public void setDatiInserimento(long idUtente) {
		this.setUtenteInserimento(idUtente);
		this.setDataInserimento(new Date());
		this.setDatiUltimaModifica(idUtente);
	}
	
	/**
	 * imposta dati ultima modifica
	 * @param idUtente
	 */
	public void setDatiUltimaModifica(Long idUtente) {
		this.setUtenteUltimaModifica(idUtente);
		this.setDataUltimaModifica(new Date());
	}
	
	/**
	 * imposta dati cancellazione
	 * @param idUtente
	 */
	public void setDatiCancellazione(Long idUtente) {
		Date deleteDate = new Date();
		this.setUtenteUltimaModifica(idUtente);
		this.setDataUltimaModifica(deleteDate);
		this.setDataCancellazione(deleteDate);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataCancellazione() {
		return dataCancellazione;
	}

	public void setDataCancellazione(Date dataCancellazione) {
		this.dataCancellazione = dataCancellazione;
	}

	public Long getUtenteInserimento() {
		return utenteInserimento;
	}

	public void setUtenteInserimento(Long utenteInserimento) {
		this.utenteInserimento = utenteInserimento;
	}

	public Date getDataInserimento() {
		return dataInserimento;
	}

	public void setDataInserimento(Date dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	public Long getUtenteUltimaModifica() {
		return utenteUltimaModifica;
	}

	public void setUtenteUltimaModifica(Long utenteUltimaModifica) {
		this.utenteUltimaModifica = utenteUltimaModifica;
	}

	public Date getDataUltimaModifica() {
		return dataUltimaModifica;
	}

	public void setDataUltimaModifica(Date dataUltimaModifica) {
		this.dataUltimaModifica = dataUltimaModifica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
