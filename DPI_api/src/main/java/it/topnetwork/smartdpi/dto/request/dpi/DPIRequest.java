package it.topnetwork.smartdpi.dto.request.dpi;

import java.util.Date;
import java.util.List;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.dto.request.dpi.beacon.BeaconRequest;

public class DPIRequest extends BaseRequest {
	
	private long idDPI;
	private String codice;
	private String marca;
	private String modello;
	private Date dataScadenza;
	private String note;
	private long idTipoDPI;
	private List<Long> settoriDPI;
	private BeaconRequest beacon;
	
	public DPIRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = (idDPI == 0 &&
//				Utility.isValid(codice) && 
//				Utility.isValid(marca) &&
//				Utility.isValid(modello) &&
				dataScadenza != null &&
				idTipoDPI != 0 &&
				settoriDPI != null && !settoriDPI.isEmpty() &&
				beacon != null && beacon.isValid()) ||
				idDPI != 0;
		
		return isValid;
	}

	public long getIdDPI() {
		return idDPI;
	}

	public void setIdDPI(long idDPI) {
		this.idDPI = idDPI;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getIdTipoDPI() {
		return idTipoDPI;
	}

	public void setIdTipoDPI(long idTipoDPI) {
		this.idTipoDPI = idTipoDPI;
	}

	public List<Long> getSettoriDPI() {
		return settoriDPI;
	}

	public void setSettoriDPI(List<Long> settoriDPI) {
		this.settoriDPI = settoriDPI;
	}

	public BeaconRequest getBeacon() {
		return beacon;
	}

	public void setBeacon(BeaconRequest beacon) {
		this.beacon = beacon;
	}

}
