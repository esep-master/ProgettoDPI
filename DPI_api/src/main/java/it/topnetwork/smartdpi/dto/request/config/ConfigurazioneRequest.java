package it.topnetwork.smartdpi.dto.request.config;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class ConfigurazioneRequest extends BaseRequest {
	
	private String nome;
	private String valore;
	private boolean loginApp;
	
	public ConfigurazioneRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = Utility.isValid(nome) &&
				Utility.isValid(valore, true);
		return isValid;
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
