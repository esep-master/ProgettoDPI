package it.topnetwork.smartdpi.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "tipi_allarmi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoAllarme extends BaseEntity {
	
	private String nome;
	
	private boolean bloccante;
	
//	@OneToMany(mappedBy = "tipoAllarme", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnore
//	private Set<Allarme> allarmi;
	
	public TipoAllarme() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isBloccante() {
		return bloccante;
	}

	public void setBloccante(boolean bloccante) {
		this.bloccante = bloccante;
	}

//	public Set<Allarme> getAllarmi() {
//		return allarmi;
//	}

//	public void setAllarmi(Set<Allarme> allarmi) {
//		this.allarmi = allarmi;
//	}

}
