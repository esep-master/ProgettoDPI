package it.topnetwork.smartdpi.dto.request.offline;

import java.util.Date;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class AllarmeDPISyncRequest extends BaseRequest {
	
	private long idAppAllarme;
	private long idAllarme;
	private long idDPI;
	private long idAppIntervento;
	private long idIntervento;
	private Date dataAllarme;
	private String latitudine;
	private String longitudine;

	public AllarmeDPISyncRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idAppAllarme != 0 &&
				idDPI != 0 && 
				idAppIntervento != 0 &&
				dataAllarme != null;
		return isValid;
	}

	public long getIdAppAllarme() {
		return idAppAllarme;
	}

	public void setIdAppAllarme(long idAppAllarme) {
		this.idAppAllarme = idAppAllarme;
	}

	public long getIdAllarme() {
		return idAllarme;
	}

	public void setIdAllarme(long idAllarme) {
		this.idAllarme = idAllarme;
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

	public Date getDataAllarme() {
		return dataAllarme;
	}

	public void setDataAllarme(Date dataAllarme) {
		this.dataAllarme = dataAllarme;
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

	@Override
	public String toString() {
		return "AllarmeDPISyncRequest [idAppAllarme=" + idAppAllarme + ", idAllarme=" + idAllarme + ", idDPI=" + idDPI
				+ ", idAppIntervento=" + idAppIntervento + ", idIntervento=" + idIntervento + ", dataAllarme="
				+ dataAllarme + ", latitudine=" + latitudine + ", longitudine=" + longitudine + "]";
	}

}
