package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "sedi_commesse")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
            name = "SedeCommessa.salvaSedeCommessa",
            procedureName = "SALVA_SEDE_COMMESSA",
            parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_SEDE_COMMESSA", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NOME", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_COMMESSA", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ID_ENTITA_CREATA", type = Long.class)
            }
    ),
    @NamedStoredProcedureQuery(
	        name = "SedeCommessa.eliminaSedeCommessa",
	        procedureName = "ELIMINA_SEDE_COMMESSA",
	        parameters = {
	        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_SEDE_COMMESSA", type = Long.class),
	        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
	            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
	            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
public class SedeCommessa extends BaseEntity {

	private String nome;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_commessa", nullable = false)
	@JsonIgnoreProperties("sediCommessa")
	private Commessa commessa;
	
	@OneToMany(mappedBy = "sedeCommessa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("sedeCommessa")
	private Set<UtenteSedeCommessa> utenteSediCommesse;

//	@OneToMany(mappedBy = "sedeCommessa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("sedeCommessa")
//	private Set<OperatoreSedeCommessa> operatoreSediCommesse;

//	@OneToMany(mappedBy = "sedeCommessa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("sedeCommessa")
//	private Set<Intervento> interventi;
	
	public SedeCommessa() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Commessa getCommessa() {
		return commessa;
	}

	public void setCommessa(Commessa commessa) {
		this.commessa = commessa;
	}

	public Set<UtenteSedeCommessa> getUtenteSediCommesse() {
		return utenteSediCommesse;
	}

	public void setUtenteSediCommesse(Set<UtenteSedeCommessa> utenteSediCommesse) {
		this.utenteSediCommesse = utenteSediCommesse;
	}

//	public Set<OperatoreSedeCommessa> getOperatoreSediCommesse() {
//		return operatoreSediCommesse;
//	}
//
//	public void setOperatoreSediCommesse(Set<OperatoreSedeCommessa> operatoreSediCommesse) {
//		this.operatoreSediCommesse = operatoreSediCommesse;
//	}

//	public Set<Intervento> getInterventi() {
//		return interventi;
//	}
//
//	public void setInterventi(Set<Intervento> interventi) {
//		this.interventi = interventi;
//	}
	
}
