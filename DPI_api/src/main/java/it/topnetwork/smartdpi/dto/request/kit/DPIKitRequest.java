package it.topnetwork.smartdpi.dto.request.kit;

import java.util.Date;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class DPIKitRequest extends BaseRequest {
	
	public long idDPI;
	private Date sbloccoAllarmeDa;
	private Date sbloccoAllarmeA;
	
	public DPIKitRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idDPI != 0;
		return isValid;
	}

	public long getIdDPI() {
		return idDPI;
	}

	public void setIdDPI(long idDPI) {
		this.idDPI = idDPI;
	}

	public Date getSbloccoAllarmeDa() {
		return sbloccoAllarmeDa;
	}

	public void setSbloccoAllarmeDa(Date sbloccoAllarmeDa) {
		this.sbloccoAllarmeDa = sbloccoAllarmeDa;
	}

	public Date getSbloccoAllarmeA() {
		return sbloccoAllarmeA;
	}

	public void setSbloccoAllarmeA(Date sbloccoAllarmeA) {
		this.sbloccoAllarmeA = sbloccoAllarmeA;
	}

}
