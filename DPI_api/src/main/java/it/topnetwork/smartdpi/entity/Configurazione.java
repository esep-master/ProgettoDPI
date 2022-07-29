package it.topnetwork.smartdpi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "configurazioni")
@Where(clause = "data_cancellazione > SYSDATE()")
public class Configurazione extends BaseEntity {

	private String nome;
	
	private String valore;
	
	@Column(name = "login_app")
	private boolean loginApp;
	
	public Configurazione() {
		super();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public boolean isLoginApp() {
		return loginApp;
	}

	public void setLoginApp(boolean loginApp) {
		this.loginApp = loginApp;
	}
	
}
