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
@Table(name = "dpi")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries(
{
    @NamedStoredProcedureQuery(
        name = "DPI.salvaDPI",
        procedureName = "SALVA_DPI",
        parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_DPI", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "CODICE_DPI", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "MARCA_DPI", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "MODELLO_DPI", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "DATA_SCADENZA_DPI", type = Date.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "NOTE_DPI", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_TIPO_DPI", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_BEACON", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "SERIALE_BEACON", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_TIPO_BEACON", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ID_ENTITA_CREATA", type = Long.class)
        }
    ),
    @NamedStoredProcedureQuery(
            name = "DPI.eliminaDPI",
            procedureName = "ELIMINA_DPI",
            parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_DPI", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_BEACON", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class)
            }
        )
})
public class DPI extends BaseEntity {
	
	private String codice;
	
	private String marca;

	private String modello;
	
	@Column(name = "data_scadenza")
	private Date dataScadenza;
	
	private String note;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_dpi", nullable = false)
	@JsonIgnoreProperties("dpi")
	private TipoDPI tipoDPI;
	
//	@OneToOne(fetch = FetchType.EAGER, optional = true)
//	@JoinColumn(name = "id_beacon", nullable = true)
//	@JsonIgnoreProperties("dpi")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_beacon", nullable = false)
	@JsonIgnoreProperties("dpi")
	private Beacon beacon;
		
	@OneToMany(mappedBy = "dpi", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("dpi")
//	@JsonIgnore
	private Set<DPIKit> dpiKit;
	
	@OneToMany(mappedBy = "dpi", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("dpi")
	private Set<SettoreDPI> settoriDPI;
	
	@OneToMany(mappedBy = "dpi", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("dpi")
	@JsonIgnore
	private Set<Allarme> allarmi;
	
	public DPI() {
		super();
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public TipoDPI getTipoDPI() {
		return tipoDPI;
	}

	public void setTipoDPI(TipoDPI tipoDPI) {
		this.tipoDPI = tipoDPI;
	}

	public Beacon getBeacon() {
		return beacon;
	}

	public void setBeacon(Beacon beacon) {
		this.beacon = beacon;
	}

	public Set<DPIKit> getDpiKit() {
		return dpiKit;
	}

	public void setDpiKit(Set<DPIKit> dpiKit) {
		this.dpiKit = dpiKit;
	}

	public Set<SettoreDPI> getSettoriDPI() {
		return settoriDPI;
	}

	public void setSettoriDPI(Set<SettoreDPI> settoriDPI) {
		this.settoriDPI = settoriDPI;
	}

	public Set<Allarme> getAllarmi() {
		return allarmi;
	}

	public void setAllarmi(Set<Allarme> allarmi) {
		this.allarmi = allarmi;
	}
	
}
