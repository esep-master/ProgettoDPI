package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "funzioni")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Funzione extends BaseEntity {
	
	private String nome;
	
	@OneToMany(mappedBy = "funzione", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("funzione")
	@JsonIgnore
	private Set<FunzioneRuolo> funzioniRuolo;
	
	public Funzione() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Set<FunzioneRuolo> getFunzioniRuolo() {
		return funzioniRuolo;
	}

	public void setFunzioniRuolo(Set<FunzioneRuolo> funzioniRuolo) {
		this.funzioniRuolo = funzioniRuolo;
	}

}
