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
@Table(name = "commesse")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Commessa.synchronizeCRMData",
        procedureName = "SYNCHRONIZE_CRM_RECORD",
        parameters = {
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "MATRICOLA", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "IMEI", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "COMMESSA", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "SETTORE", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "OPERATORE", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "NUMERO_TELEFONO", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "EMAIL", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class)
        }
    ),
    @NamedStoredProcedureQuery(
            name = "Commessa.salvaCommessa",
            procedureName = "SALVA_COMMESSA",
            parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_COMMESSA", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "NOME", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_SETTORE", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ID_ENTITA_CREATA", type = Long.class)
            }
    ),
    @NamedStoredProcedureQuery(
	        name = "Commessa.eliminaCommessa",
	        procedureName = "ELIMINA_COMMESSA",
	        parameters = {
	        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_COMMESSA", type = Long.class),
	        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
	            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
	            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
public class Commessa extends BaseEntity {
	
	private String nome;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "id_settore", nullable = true)
	@JsonIgnoreProperties("commesse")
	private Settore settore;
	
	@OneToMany(mappedBy = "commessa", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("commessa")
	private Set<SedeCommessa> sediCommessa;
	
	public Commessa() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Settore getSettore() {
		return settore;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public Set<SedeCommessa> getSediCommessa() {
		return sediCommessa;
	}

	public void setSediCommessa(Set<SedeCommessa> sediCommessa) {
		this.sediCommessa = sediCommessa;
	}

}
