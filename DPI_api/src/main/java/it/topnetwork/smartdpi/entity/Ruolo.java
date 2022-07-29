package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ruoli")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Ruolo extends BaseEntity {
	
	private String nome;
	
	@Column(name = "super_admin")
	private boolean superAdmin;
	
	@OneToMany(mappedBy = "ruolo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("ruolo")
	@JsonIgnore
	private Set<Utente> utenti;
	
	@OneToMany(mappedBy = "ruolo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("ruolo")
	private Set<FunzioneRuolo> funzioniRuolo;
	
	public Ruolo() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(boolean superAdmin) {
		this.superAdmin = superAdmin;
	}

	public Set<Utente> getUtenti() {
		return utenti;
	}

	public void setUtenti(Set<Utente> utenti) {
		this.utenti = utenti;
	}

	public Set<FunzioneRuolo> getFunzioniRuolo() {
		return funzioniRuolo;
	}

	public void setFunzioniRuolo(Set<FunzioneRuolo> funzioniRuolo) {
		this.funzioniRuolo = funzioniRuolo;
	}

}
