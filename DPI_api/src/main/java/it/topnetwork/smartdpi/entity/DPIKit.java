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
@Table(name = "dpi_kit")
@Where(clause = "data_cancellazione > SYSDATE()")
public class DPIKit extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_kit", nullable = false)
	@JsonIgnoreProperties("dpiKit")
	private Kit kit;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_dpi", nullable = false)
	@JsonIgnoreProperties("dpiKit")
	private DPI dpi;
	
	@Column(name = "sblocco_allarme_da")
	private Date sbloccoAllarmeDa;
	
	@Column(name = "sblocco_allarme_a")
	private Date sbloccoAllarmeA;
	
	public DPIKit() {
		super();
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public DPI getDpi() {
		return dpi;
	}

	public void setDpi(DPI dpi) {
		this.dpi = dpi;
	}

	public Date getSbloccoAllarmeDa() {
		return sbloccoAllarmeDa;
	}

	public void setSbloccoAllarmeDa(Date sbloccoAllarmeDa) {
		this.sbloccoAllarmeDa = sbloccoAllarmeDa;
	}

	public Date getSbloccoAllarmeA() {
		return sbloccoAllarmeA;
	}

	public void setSbloccoAllarmeA(Date sbloccoAllarmeA) {
		this.sbloccoAllarmeA = sbloccoAllarmeA;
	}

}
