package it.topnetwork.smartdpi.dto.request.allarme;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class ChiusuraAllarmeDPIRequest extends BaseRequest {
	
	private long idDPI;
	private long idIntervento;
	private long idKit;

	public ChiusuraAllarmeDPIRequest() {}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idDPI != 0 && idIntervento != 0 && idKit != 0;
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

	public long getIdKit() {
		return idKit;
	}

	public void setIdKit(long idKit) {
		this.idKit = idKit;
	}

	@Override
	public String toString() {
		return "ChiusuraAllarmeDPIRequest [idDPI=" + idDPI + ", idIntervento=" + idIntervento + ", idKit=" + idKit
				+ "]";
	}

}
