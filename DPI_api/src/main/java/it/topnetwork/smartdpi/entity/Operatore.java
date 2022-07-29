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
@Table(name = "operatori")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Operatore.eliminaOperatore",
        procedureName = "ELIMINA_OPERATORE",
        parameters = {
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_OPERATORE", type = Long.class),
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
@Where(clause = "data_cancellazione > SYSDATE()")
public class Operatore extends BaseEntity {
	
	private String matricola;
	
	@JsonIgnore
	private String password;
	
	private String nominativo;
	
	@Column(name = "id_dispositivo")
	private String idDispositivo;
	
	@Column(name = "numero_telefono")
	private String numeroTelefono;
	
	private String email;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_operatore", nullable = false)
	@JsonIgnoreProperties("operatori")
	private TipoOperatore tipoOperatore;
	
	@JsonIgnore
	@Column(name = "password_originale")
	private String passwordOriginale;
	
	@OneToMany(mappedBy = "operatore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("operatore")
	private Set<OperatoreSedeCommessa> operatoreSediCommesse;
	
	@OneToMany(mappedBy = "operatore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("operatore")
	private Set<Kit> kit;
	
	public Operatore() {
		super();
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	public String getIdDispositivo() {
		return idDispositivo;
	}

	public void setIdDispositivo(String idDispositivo) {
		this.idDispositivo = idDispositivo;
	}

	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TipoOperatore getTipoOperatore() {
		return tipoOperatore;
	}

	public void setTipoOperatore(TipoOperatore tipoOperatore) {
		this.tipoOperatore = tipoOperatore;
	}

	public String getPasswordOriginale() {
		return passwordOriginale;
	}

	public void setPasswordOriginale(String passwordOriginale) {
		this.passwordOriginale = passwordOriginale;
	}

	public Set<OperatoreSedeCommessa> getOperatoreSediCommesse() {
		return operatoreSediCommesse;
	}

	public void setOperatoreSediCommesse(Set<OperatoreSedeCommessa> operatoreSediCommesse) {
		this.operatoreSediCommesse = operatoreSediCommesse;
	}

	public Set<Kit> getKit() {
		return kit;
	}

	public void setKit(Set<Kit> kit) {
		this.kit = kit;
	}

}
