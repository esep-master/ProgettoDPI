package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "operatori_sedi_commesse")
@Where(clause = "data_cancellazione > SYSDATE()")
public class OperatoreSedeCommessa extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_operatore", nullable = false)
	@JsonIgnoreProperties("operatoreSediCommesse")
	private Operatore operatore;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "id_sede_commessa", nullable = false)
	@JsonIgnoreProperties("operatoreSediCommesse")
	private SedeCommessa sedeCommessa;

	public OperatoreSedeCommessa() {
		super();
	}

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
	
}
