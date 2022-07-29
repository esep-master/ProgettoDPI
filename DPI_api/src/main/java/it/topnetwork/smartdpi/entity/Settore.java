package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "settori")
@Where(clause = "data_cancellazione > SYSDATE()")
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "Settore.eliminaSettore",
        procedureName = "ELIMINA_SETTORE",
        parameters = {
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_SETTORE", type = Long.class),
        	@StoredProcedureParameter(mode = ParameterMode.IN, name = "ID_UTENTE_OPERAZIONE", type = Long.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO", type = Integer.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "ESITO_MSG", type = String.class),
        }
    )
})
public class Settore extends BaseEntity {
	
	private String nome;
	
	@Column(name = "nome_icona")
	private String nomeIcona;
	
//	@OneToMany(mappedBy = "settore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("settore")
//	private Set<Commessa> commesse;
	
	@OneToMany(mappedBy = "settore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("settore")
	@JsonIgnore
	private Set<TipoDPISettore> tipiDPISettori;
	
//	@OneToMany(mappedBy = "settore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("settore")
//	private Set<Kit> kit;
	
//	@OneToMany(mappedBy = "settore", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("settore")
//	private Set<SettoreDPI> settoriDPI;
	
	public Settore() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeIcona() {
		return nomeIcona;
	}

	public void setNomeIcona(String nomeIcona) {
		this.nomeIcona = nomeIcona;
	}

//	public Set<Commessa> getCommesse() {
//		return commesse;
//	}
//
//	public void setCommesse(Set<Commessa> commesse) {
//		this.commesse = commesse;
//	}

	public Set<TipoDPISettore> getTipiDPISettori() {
		return tipiDPISettori;
	}

	public void setTipiDPISettori(Set<TipoDPISettore> tipiDPISettori) {
		this.tipiDPISettori = tipiDPISettori;
	}

//	public Set<Kit> getKit() {
//		return kit;
//	}
//
//	public void setKit(Set<Kit> kit) {
//		this.kit = kit;
//	}

//	public Set<SettoreDPI> getSettoriDPI() {
//		return settoriDPI;
//	}
//
//	public void setSettoriDPI(Set<SettoreDPI> settoriDPI) {
//		this.settoriDPI = settoriDPI;
//	}
	
}
