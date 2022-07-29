package it.topnetwork.smartdpi.dto.request.offline;

import java.util.Date;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class AzioneOperatoreSyncRequest extends BaseRequest {

	private long idTipoAzione;
	private long idAppIntervento;
	private long idIntervento;
	private long idDpi;
	private long idTipoAllarme;
	private Date dataAzione;
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idTipoAzione != 0 &&
				dataAzione != null;				
		return isValid;
	}

	public long getIdTipoAzione() {
		return idTipoAzione;
	}

	public void setIdTipoAzione(long idTipoAzione) {
		this.idTipoAzione = idTipoAzione;
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

	public long getIdDpi() {
		return idDpi;
	}

	public void setIdDpi(long idDpi) {
		this.idDpi = idDpi;
	}

	public long getIdTipoAllarme() {
		return idTipoAllarme;
	}

	public void setIdTipoAllarme(long idTipoAllarme) {
		this.idTipoAllarme = idTipoAllarme;
	}

	public Date getDataAzione() {
		return dataAzione;
	}

	public void setDataAzione(Date dataAzione) {
		this.dataAzione = dataAzione;
	}

	@Override
	public String toString() {
		return "AzioneOperatoreSyncRequest [idTipoAzione=" + idTipoAzione + ", idAppIntervento=" + idAppIntervento
				+ ", idIntervento=" + idIntervento + ", idDpi=" + idDpi + ", idTipoAllarme=" + idTipoAllarme
				+ ", dataAzione=" + dataAzione + "]";
	}
	
}
