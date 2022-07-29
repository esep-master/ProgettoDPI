package it.topnetwork.smartdpi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "stati_allarmi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class StatoAllarme extends BaseEntity {
	
	private String nome;
	
	@Column(name = "stato_iniziale")
	private boolean statoIniziale;
	
	@Column(name = "stato_finale")
	private boolean statoFinale;
	
	@Column(name = "sblocco_automatico_operatore")
	private boolean sbloccoAutomaticoOperatore;
	
//	@OneToMany(mappedBy = "statoAllarme", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("statoAllarme")
//	private Set<Allarme> allarmi;
	
	public StatoAllarme() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isStatoIniziale() {
		return statoIniziale;
	}

	public void setStatoIniziale(boolean statoIniziale) {
		this.statoIniziale = statoIniziale;
	}

	public boolean isStatoFinale() {
		return statoFinale;
	}

	public void setStatoFinale(boolean statoFinale) {
		this.statoFinale = statoFinale;
	}

	public boolean isSbloccoAutomaticoOperatore() {
		return sbloccoAutomaticoOperatore;
	}

	public void setSbloccoAutomaticoOperatore(boolean sbloccoAutomaticoOperatore) {
		this.sbloccoAutomaticoOperatore = sbloccoAutomaticoOperatore;
	}

}
