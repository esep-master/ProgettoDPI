package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "tipi_azioni_operatori")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoAzioneOperatore extends BaseEntity {

	private String nome;

//	@OneToMany(mappedBy = "tipoAzioneOperatore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnore
//	private Set<AzioneOperatore> azioni;

	public TipoAzioneOperatore() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

//	public Set<AzioneOperatore> getAzioniOperatori() {
//		return azioniOperatori;
//	}
//
//	public void setAzioniOperatori(Set<AzioneOperatore> azioniOperatori) {
//		this.azioniOperatori = azioniOperatori;
//	}

}
