package it.topnetwork.smartdpi.dto.request.dpi.beacon;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class BeaconRequest extends BaseRequest {
	
	private long idBeacon;
	private String seriale;
	private long idTipoBeacon;

	public BeaconRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		// se in creazione devono essere validi tutti i campi tranne id, se in modifica necessario solo id
		isValid = (idBeacon == 0 && Utility.isValid(seriale) && idTipoBeacon != 0) || idBeacon != 0;
		
		return isValid;
	}

	public long getIdBeacon() {
		return idBeacon;
	}

	public void setIdBeacon(long idBeacon) {
		this.idBeacon = idBeacon;
	}

	public String getSeriale() {
		return seriale;
	}

	public void setSeriale(String seriale) {
		this.seriale = seriale;
	}

	public long getIdTipoBeacon() {
		return idTipoBeacon;
	}

	public void setIdTipoBeacon(long idTipoBeacon) {
		this.idTipoBeacon = idTipoBeacon;
	}
	
}
