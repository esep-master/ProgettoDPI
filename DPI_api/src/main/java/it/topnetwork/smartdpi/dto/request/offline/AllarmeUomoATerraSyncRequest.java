package it.topnetwork.smartdpi.dto.request.offline;

import java.util.Date;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class AllarmeUomoATerraSyncRequest extends BaseRequest {

	private long idAppAllarme;
	private long idAllarme;
	private long idAppIntervento;
	private long idIntervento;
	private Date dataAllarme;
	private String latitudine;
	private String longitudine;
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idAppAllarme != 0 &&
				idAppIntervento != 0 &&
				dataAllarme != null &&
				Utility.isValid(latitudine) &&
				Utility.isValid(longitudine);
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
		return "AllarmeUomoATerraSyncRequest [idAppAllarme=" + idAppAllarme + ", idAllarme=" + idAllarme
				+ ", idAppIntervento=" + idAppIntervento + ", idIntervento=" + idIntervento + ", dataAllarme="
				+ dataAllarme + ", latitudine=" + latitudine + ", longitudine=" + longitudine + "]";
	}

}
