package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tipi_dpi_settori")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoDPISettore extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_tipo_dpi", nullable = false)
	@JsonIgnoreProperties("tipiDPISettori")
	private TipoDPI tipoDPI;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_settore", nullable = false)
	@JsonIgnoreProperties("tipiDPISettori")
	private Settore settore;
	
	public TipoDPISettore() {
		super();
	}

	public TipoDPI getTipoDPI() {
		return tipoDPI;
	}

	public void setTipoDPI(TipoDPI tipoDPI) {
		this.tipoDPI = tipoDPI;
	}

	public Settore getSettore() {
		return settore;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

}
