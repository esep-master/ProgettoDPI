package it.topnetwork.smartdpi.dto.request.allarme;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class AllarmeDPIRequest extends BaseRequest {
	
	private long idDPI;
	private long idIntervento;
	private String latitudine;
	private String longitudine;
	
	public AllarmeDPIRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		// latitudine e longitudine sono necessari solo se app di lavoro non presente
		isValid = idDPI != 0 && 
				idIntervento != 0;
		return isValid;
	}

	public long getIdDPI() {
		return idDPI;
	}

	public void setIdDPI(long idDPI) {
		this.idDPI = idDPI;
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
