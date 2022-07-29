package it.topnetwork.smartdpi.dto.request.intervento;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class InizioInterventoRequest extends BaseRequest {
	
	private long idSedeCommessa;
	private long idKit;
	private String latitudine;
	private String longitudine;
	
	public InizioInterventoRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idSedeCommessa != 0 &&
				idKit != 0;
		return isValid;
	}

	public long getIdSedeCommessa() {
		return idSedeCommessa;
	}

	public void setIdSedeCommessa(long idSedeCommessa) {
		this.idSedeCommessa = idSedeCommessa;
	}

	public long getIdKit() {
		return idKit;
	}

	public void setIdKit(long idKit) {
		this.idKit = idKit;
	}

	public String getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
	}

	public String getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}

}
