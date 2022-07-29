package it.topnetwork.smartdpi.dto.response.model;

import it.topnetwork.smartdpi.entity.Settore;

public class InfoKitSettoreDashboardResponse {

	private Settore settore;
	private int numeroKit;
	
	public InfoKitSettoreDashboardResponse() {}

	public Settore getSettore() {
		return settore;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public int getNumeroKit() {
		return numeroKit;
	}

	public void setNumeroKit(int numeroKit) {
		this.numeroKit = numeroKit;
	}
	
}
