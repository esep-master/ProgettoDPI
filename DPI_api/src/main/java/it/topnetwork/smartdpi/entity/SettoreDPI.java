package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "settori_dpi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class SettoreDPI extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_dpi", nullable = false)
	@JsonIgnoreProperties("settoriDPI")
	private DPI dpi;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_settore", nullable = false)
	@JsonIgnoreProperties("settoriDPI")
	private Settore settore;

	public SettoreDPI() {
		super();
	}

	public DPI getDpi() {
		return dpi;
	}

	public void setDpi(DPI dpi) {
		this.dpi = dpi;
	}

	public Settore getSettore() {
		return settore;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}
	
}
