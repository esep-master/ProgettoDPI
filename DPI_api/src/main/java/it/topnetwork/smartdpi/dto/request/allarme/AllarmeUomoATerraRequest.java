package it.topnetwork.smartdpi.dto.request.allarme;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class AllarmeUomoATerraRequest extends BaseRequest {
	
	private long idIntervento;
	private String latitudine;
	private String longitudine;

	public AllarmeUomoATerraRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idIntervento != 0 &&
				Utility.isValid(latitudine) &&
				Utility.isValid(longitudine);
		return isValid;
	}

	public long getIdIntervento() {
		return idIntervento;
	}

	public void setIdIntervento(long idIntervento) {
		this.idIntervento = idIntervento;
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
