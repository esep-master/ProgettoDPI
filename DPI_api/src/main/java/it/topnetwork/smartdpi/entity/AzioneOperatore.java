package it.topnetwork.smartdpi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "azioni_operatori")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "AzioneOperatore.logAzioneOperatore",
        procedureName = "LOG_AZIONE_OPERATORE",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_OPERATORE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_TIPO_AZIONE_OPERATORE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_INTERVENTO", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_DPI", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_TIPO_ALLARME", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "DATA_AZIONE", type = Date.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class)
        }
    )
})
public class AzioneOperatore extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_operatore", nullable = false)
//	@JsonIgnoreProperties("azioni")
	private Operatore operatore;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_azione_operatore", nullable = false)
//	@JsonIgnoreProperties("azioni")
	private TipoAzioneOperatore tipoAzioneOperatore;
	
	@Column(name = "data_azione")
	private Date dataAzione;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_intervento", nullable = true)
//	@JsonIgnoreProperties("azioni")
	private Intervento intervento;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_dpi", nullable = true)
//	@JsonIgnoreProperties("azioni")
	private DPI dpi;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_tipo_allarme", nullable = true)
//	@JsonIgnoreProperties("azioni")
	private TipoAllarme tipoAllarme;
	
	public AzioneOperatore() {
		super();
	}

	public Operatore getOperatore() {
		return operatore;
	}

	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	public TipoAzioneOperatore getTipoAzioneOperatore() {
		return tipoAzioneOperatore;
	}

	public void setTipoAzioneOperatore(TipoAzioneOperatore tipoAzioneOperatore) {
		this.tipoAzioneOperatore = tipoAzioneOperatore;
	}

	public Date getDataAzione() {
		return dataAzione;
	}

	public void setDataAzione(Date dataAzione) {
		this.dataAzione = dataAzione;
	}

	public Intervento getIntervento() {
		return intervento;
	}

	public void setIntervento(Intervento intervento) {
		this.intervento = intervento;
	}

	public DPI getDpi() {
		return dpi;
	}

	public void setDpi(DPI dpi) {
		this.dpi = dpi;
	}

	public TipoAllarme getTipoAllarme() {
		return tipoAllarme;
	}

	public void setTipoAllarme(TipoAllarme tipoAllarme) {
		this.tipoAllarme = tipoAllarme;
	}
	
}
