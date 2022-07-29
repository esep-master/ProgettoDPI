package it.topnetwork.smartdpi.dto.request.offline;

import java.util.Date;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class InterventoSyncRequest extends BaseRequest {
	
	private long idAppIntervento;
	private long idIntervento;
	private long idSedeCommessa;
	private long idKit;
	private Date dataInizio;
	private Date dataFine;
	private String latitudine;
	private String longitudine;

	public InterventoSyncRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idAppIntervento != 0 &&
				idSedeCommessa != 0 &&
				idKit != 0 && 
				dataInizio != null;
				//&& dataFine != null;
		return isValid;
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

	public Date getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}

	public Date getDataFine() {
		return dataFine;
	}

	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
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
		return "InterventoSyncRequest [idAppIntervento=" + idAppIntervento + ", idIntervento=" + idIntervento
				+ ", idSedeCommessa=" + idSedeCommessa + ", idKit=" + idKit + ", dataInizio=" + dataInizio
				+ ", dataFine=" + dataFine + ", latitudine=" + latitudine + ", longitudine=" + longitudine + "]";
	}

}
