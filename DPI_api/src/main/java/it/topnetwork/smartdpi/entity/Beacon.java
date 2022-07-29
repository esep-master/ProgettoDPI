package it.topnetwork.smartdpi.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "beacon")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Beacon extends BaseEntity {
	
	private String seriale;
	
	@Column(name = "livello_batteria", insertable=false)
	private int livelloBatteria;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_beacon", nullable = false)
	@JsonIgnoreProperties("beacon")
	private TipoBeacon tipoBeacon;
	
//	@OneToOne(mappedBy = "beacon")
	@JsonIgnore
	@OneToMany(mappedBy = "beacon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("beacon")
	private Set<DPI> dpi;
	
	public Beacon() {
		super();
	}

	public String getSeriale() {
		return seriale;
	}

	public void setSeriale(String seriale) {
		this.seriale = seriale;
	}

	public int getLivelloBatteria() {
		return livelloBatteria;
	}

	public void setLivelloBatteria(int livelloBatteria) {
		this.livelloBatteria = livelloBatteria;
	}

	public TipoBeacon getTipoBeacon() {
		return tipoBeacon;
	}

	public void setTipoBeacon(TipoBeacon tipoBeacon) {
		this.tipoBeacon = tipoBeacon;
	}

	public Set<DPI> getDpi() {
		return dpi;
	}

	public void setDpi(Set<DPI> dpi) {
		this.dpi = dpi;
	}

}
