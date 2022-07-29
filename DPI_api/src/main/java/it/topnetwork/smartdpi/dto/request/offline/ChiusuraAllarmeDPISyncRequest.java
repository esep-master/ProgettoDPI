package it.topnetwork.smartdpi.dto.request.offline;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class ChiusuraAllarmeDPISyncRequest extends BaseRequest {

	private long idDPI;
	private long idAppIntervento;
	private long idIntervento;
	private long idKit;
	
	public ChiusuraAllarmeDPISyncRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idDPI != 0 && 
				idAppIntervento != 0 &&
				idKit != 0;
		return isValid;
	}

	public long getIdDPI() {
		return idDPI;
	}

	public void setIdDPI(long idDPI) {
		this.idDPI = idDPI;
	}

	public long getIdAppIntervento() {
		return idAppIntervento;
	}

	public void setIdAppIntervento(long idAppIntervento) {
		this.idAppIntervento = idAppIntervento;
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
		return "ChiusuraAllarmeDPISyncRequest [idDPI=" + idDPI + ", idAppIntervento=" + idAppIntervento
				+ ", idIntervento=" + idIntervento + ", idKit=" + idKit + "]";
	}
	
}
