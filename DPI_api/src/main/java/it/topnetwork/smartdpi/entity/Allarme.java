package it.topnetwork.smartdpi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "allarmi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Allarme extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_dpi", nullable = true)
	@JsonIgnoreProperties({"allarmi", "dpiKit", "settoriDPI"})
	private DPI dpi;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_intervento", nullable = true)
	@JsonIgnoreProperties("allarmi")
	private Intervento intervento;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_kit", nullable = true)
	@JsonIgnoreProperties("allarmi")
	private Kit kit;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_allarme", nullable = false)
	@JsonIgnoreProperties("allarmi")
	private TipoAllarme tipoAllarme;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_stato_allarme", nullable = false)
	@JsonIgnoreProperties("allarmi")
	private StatoAllarme statoAllarme;
	
	@Column(name = "data_allarme")
	private Date dataAllarme;
	
	private String latitudine;
	
	private String longitudine;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_utente_presa_in_carico", nullable = true)
	@JsonIgnoreProperties({"allarmi", "utenteSediCommesse", "ruolo"})
	private Utente utentePresaInCarico;
	
	@Column(name = "data_presa_in_carico")
	private Date dataPresaInCarico;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_utente_risoluzione", nullable = true)
	@JsonIgnoreProperties({"allarmi", "utenteSediCommesse", "ruolo"})
	private Utente utenteRisoluzione;
	
	@Column(name = "data_risoluzione")
	private Date dataRisoluzione;
	
	@Column(name = "falso_allarme")
	private boolean falsoAllarme;
	
	private String note;
	
	public Allarme() {
		super();
	}

	public DPI getDpi() {
		return dpi;
	}

	public void setDpi(DPI dpi) {
		this.dpi = dpi;
	}

	public Intervento getIntervento() {
		return intervento;
	}

	public void setIntervento(Intervento intervento) {
		this.intervento = intervento;
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public TipoAllarme getTipoAllarme() {
		return tipoAllarme;
	}

	public void setTipoAllarme(TipoAllarme tipoAllarme) {
		this.tipoAllarme = tipoAllarme;
	}

	public StatoAllarme getStatoAllarme() {
		return statoAllarme;
	}

	public void setStatoAllarme(StatoAllarme statoAllarme) {
		this.statoAllarme = statoAllarme;
	}

	public Date getDataAllarme() {
		return dataAllarme;
	}

	public void setDataAllarme(Date dataAllarme) {
		this.dataAllarme = dataAllarme;
	}

	public String getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
	}

	public String getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}

	public Utente getUtentePresaInCarico() {
		return utentePresaInCarico;
	}

	public void setUtentePresaInCarico(Utente utentePresaInCarico) {
		this.utentePresaInCarico = utentePresaInCarico;
	}

	public Date getDataPresaInCarico() {
		return dataPresaInCarico;
	}

	public void setDataPresaInCarico(Date dataPresaInCarico) {
		this.dataPresaInCarico = dataPresaInCarico;
	}

	public Utente getUtenteRisoluzione() {
		return utenteRisoluzione;
	}

	public void setUtenteRisoluzione(Utente utenteRisoluzione) {
		this.utenteRisoluzione = utenteRisoluzione;
	}

	public Date getDataRisoluzione() {
		return dataRisoluzione;
	}

	public void setDataRisoluzione(Date dataRisoluzione) {
		this.dataRisoluzione = dataRisoluzione;
	}

	public boolean isFalsoAllarme() {
		return falsoAllarme;
	}

	public void setFalsoAllarme(boolean falsoAllarme) {
		this.falsoAllarme = falsoAllarme;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
