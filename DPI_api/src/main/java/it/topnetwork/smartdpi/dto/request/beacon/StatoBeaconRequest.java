package it.topnetwork.smartdpi.dto.request.beacon;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class StatoBeaconRequest extends BaseRequest {
	
	private Long idBeacon;
	private int batteria;
	
	public StatoBeaconRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idBeacon != 0 && batteria >= 0 && batteria <= 100;
		return isValid;
	}

	public Long getIdBeacon() {
		return idBeacon;
	}

	public void setIdBeacon(Long idBeacon) {
		this.idBeacon = idBeacon;
	}

	public int getBatteria() {
		return batteria;
	}

	public void setBatteria(int batteria) {
		this.batteria = batteria;
	}

}
