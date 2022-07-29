package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "utenti")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Utente.eliminaUtente",
        procedureName = "ELIMINA_UTENTE",
        parameters = {
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE", type = Long.class),
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
public class Utente extends BaseEntity {

	@Column(unique = true)
	private String username;
	
	@JsonIgnore
	private String password;
	
	private String email;
	
	@Column(name = "numero_telefono")
	private String numeroTelefono;
	
	private String nome;
	
	private String cognome;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_ruolo", nullable = false)
	@JsonIgnoreProperties("utente")
	private Ruolo ruolo;
	
	@OneToMany(mappedBy = "utente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("utente")
	private Set<UtenteSedeCommessa> utenteSediCommesse;
	
	public Utente() {
		super();
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

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	public Set<UtenteSedeCommessa> getUtenteSediCommesse() {
		return utenteSediCommesse;
	}

	public void setUtenteSediCommesse(Set<UtenteSedeCommessa> utenteSediCommesse) {
		this.utenteSediCommesse = utenteSediCommesse;
	}
	
}
