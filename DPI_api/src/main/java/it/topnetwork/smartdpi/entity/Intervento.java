package it.topnetwork.smartdpi.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "interventi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Intervento extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_operatore", nullable = false)
	@JsonIgnoreProperties({"interventi", "operatoreSediCommesse", "kit"})
	private Operatore operatore;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_sede_commessa", nullable = false)
	@JsonIgnoreProperties({"interventi", "utenteSediCommesse"})
	private SedeCommessa sedeCommessa;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_kit", nullable = false)
	@JsonIgnoreProperties({"interventi", "operatore"})
	private Kit kit;

	@Column(name = "data_inizio")
	private Date dataInizio;
	
	@Column(name = "data_fine")
	private Date dataFine;
	
	private String latitudine;
	
	private String longitudine;
	
	@OneToMany(mappedBy = "intervento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties("intervento")
	private Set<Allarme> allarmi;
	
	public Intervento() {}

	public Operatore getOperatore() {
		return operatore;
	}

	public void setOperatore(Operatore operatore) {
		this.operatore = operatore;
	}

	public SedeCommessa getSedeCommessa() {
		return sedeCommessa;
	}

	public void setSedeCommessa(SedeCommessa sedeCommessa) {
		this.sedeCommessa = sedeCommessa;
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
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

	public Set<Allarme> getAllarmi() {
		return allarmi;
	}

	public void setAllarmi(Set<Allarme> allarmi) {
		this.allarmi = allarmi;
	}
	
}
