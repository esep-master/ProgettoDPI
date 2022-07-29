package it.topnetwork.smartdpi.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tipi_dpi")
@Where(clause = "data_cancellazione > SYSDATE()")
public class TipoDPI extends BaseEntity {
	
	private String nome;
	
	@Column(name = "nome_modello_tf")
	private String nomeModelloTF;
	
	@Column(name = "nome_icona")
	private String nomeIcona;
	
	@OneToMany(mappedBy = "tipoDPI", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("tipoDPI")
	@JsonIgnore
	private Set<TipoDPISettore> tipiDPISettori;
	
//	@OneToMany(mappedBy = "tipoDPI", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JsonIgnoreProperties("tipoDPI")
//	private Set<DPI> dpi;
	
	public TipoDPI() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeModelloTF() {
		return nomeModelloTF;
	}

	public void setNomeModelloTF(String nomeModelloTF) {
		this.nomeModelloTF = nomeModelloTF;
	}

	public String getNomeIcona() {
		return nomeIcona;
	}

	public void setNomeIcona(String nomeIcona) {
		this.nomeIcona = nomeIcona;
	}

	public Set<TipoDPISettore> getTipiDPISettori() {
		return tipiDPISettori;
	}

	public void setTipiDPISettori(Set<TipoDPISettore> tipiDPISettori) {
		this.tipiDPISettori = tipiDPISettori;
	}

//	public Set<DPI> getDpi() {
//		return dpi;
//	}

//	public void setDpi(Set<DPI> dpi) {
//		this.dpi = dpi;
//	}

}
