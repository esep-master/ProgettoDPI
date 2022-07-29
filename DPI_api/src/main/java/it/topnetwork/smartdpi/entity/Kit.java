package it.topnetwork.smartdpi.entity;

import java.util.Date;
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
@Table(name = "kit")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Kit.eliminaKit",
        procedureName = "ELIMINA_KIT",
        parameters = {
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_KIT", type = Long.class),
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
public class Kit extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_operatore", nullable = true)
	@JsonIgnoreProperties("kit")
	private Operatore operatore;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_settore", nullable = true)
	@JsonIgnoreProperties("kit")
	private Settore settore;
	
	private String modello;
	
	private String note;
	
	@Column(name = "note_sblocco_totale")
	private String noteSbloccoTotale;
	
	@Column(name = "data_assegnazione")
	private Date dataAssegnazione;
	
	@OneToMany(mappedBy = "kit", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("kit")
	private Set<DPIKit> dpiKit;
	
	@OneToMany(mappedBy = "kit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("kit")
	@JsonIgnore
	private Set<Intervento> interventi;
	
	@OneToMany(mappedBy = "kit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("kit")
	@JsonIgnore
	private Set<Allarme> allarmi;

	public Kit() {
		super();
	}

	public Operatore getOperatore() {
		return operatore;
	}

	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	public Settore getSettore() {
		return settore;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNoteSbloccoTotale() {
		return noteSbloccoTotale;
	}

	public void setNoteSbloccoTotale(String noteSbloccoTotale) {
		this.noteSbloccoTotale = noteSbloccoTotale;
	}

	public Date getDataAssegnazione() {
		return dataAssegnazione;
	}

	public void setDataAssegnazione(Date dataAssegnazione) {
		this.dataAssegnazione = dataAssegnazione;
	}

	public Set<DPIKit> getDpiKit() {
		return dpiKit;
	}

	public void setDpiKit(Set<DPIKit> dpiKit) {
		this.dpiKit = dpiKit;
	}

	public Set<Intervento> getInterventi() {
		return interventi;
	}

	public void setInterventi(Set<Intervento> interventi) {
		this.interventi = interventi;
	}

	public Set<Allarme> getAllarmi() {
		return allarmi;
	}

	public void setAllarmi(Set<Allarme> allarmi) {
		this.allarmi = allarmi;
	}
	
}
