package it.topnetwork.smartdpi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "tipi_beacon")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoBeacon extends BaseEntity {
	
	private String nome;
	
	@Column(name = "beacon_dpi", insertable=false)
	private boolean beaconDPI;
	
//	@OneToMany(mappedBy = "tipoBeacon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("tipoBeacon")
//	private Set<Beacon> beacon;
	
	public TipoBeacon() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isBeaconDPI() {
		return beaconDPI;
	}

	public void setBeaconDPI(boolean beaconDPI) {
		this.beaconDPI = beaconDPI;
	}

//	public Set<Beacon> getBeacon() {
//		return beacon;
//	}

//	public void setBeacon(Set<Beacon> beacon) {
//		this.beacon = beacon;
//	}

}
