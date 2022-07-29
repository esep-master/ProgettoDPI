package it.topnetwork.smartdpi.dto.request.beacon;

import java.util.List;

public class StatoBeaconWrapperRequest {

	private List<StatoBeaconRequest> beacon;
	
	public StatoBeaconWrapperRequest() {
		super();
	}

	public List<StatoBeaconRequest> getBeacon() {
		return beacon;
	}

	public void setBeacon(List<StatoBeaconRequest> beacon) {
		this.beacon = beacon;
	}
	
}
