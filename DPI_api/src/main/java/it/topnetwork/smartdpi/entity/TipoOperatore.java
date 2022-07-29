package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "tipi_operatori")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoOperatore extends BaseEntity {

	private String nome;
	
	private boolean esterno;
	
//	@OneToMany(mappedBy = "tipoOperatore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnore
//	private Set<Operatore> operatori;
	
	public TipoOperatore() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isEsterno() {
		return esterno;
	}

	public void setEsterno(boolean esterno) {
		this.esterno = esterno;
	}

//	public Set<Operatore> getOperatori() {
//		return operatori;
//	}

//	public void setOperatori(Set<Operatore> operatori) {
//		this.operatori = operatori;
//	}
	
}
