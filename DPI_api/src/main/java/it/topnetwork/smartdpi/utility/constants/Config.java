package it.topnetwork.smartdpi.utility.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
	
	@Value("${api.version}")
	private String apiVersion;	
	
	@Value("${config.crm.use}")
	private String useCRM;
	
	@Value("${config.crm.rest.info.uri}")
	private String crmRestInfoURI;
	
	@Value("${config.allarme.preavviso_giorni_dpi}")
	private String giorniPreavvisoScadenzaDPI;
	
	@Value("${config.allarme.soglia_batteria_beacon}")
	private String sogliaBatteriaScadenzaBeacon;

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getUseCRM() {
		return useCRM;
	}

	public void setUseCRM(String useCRM) {
		this.useCRM = useCRM;
	}

	public String getCrmRestInfoURI() {
		return crmRestInfoURI;
	}

	public void setCrmRestInfoURI(String crmRestInfoURI) {
		this.crmRestInfoURI = crmRestInfoURI;
	}

	public String getGiorniPreavvisoScadenzaDPI() {
		return giorniPreavvisoScadenzaDPI;
	}

	public void setGiorniPreavvisoScadenzaDPI(String giorniPreavvisoScadenzaDPI) {
		this.giorniPreavvisoScadenzaDPI = giorniPreavvisoScadenzaDPI;
	}

	public String getSogliaBatteriaScadenzaBeacon() {
		return sogliaBatteriaScadenzaBeacon;
	}

	public void setSogliaBatteriaScadenzaBeacon(String sogliaBatteriaScadenzaBeacon) {
		this.sogliaBatteriaScadenzaBeacon = sogliaBatteriaScadenzaBeacon;
	}

}
