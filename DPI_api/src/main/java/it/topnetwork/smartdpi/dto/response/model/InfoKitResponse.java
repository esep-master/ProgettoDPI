package it.topnetwork.smartdpi.dto.response.model;

import java.util.List;

import it.topnetwork.smartdpi.entity.DPI;
import it.topnetwork.smartdpi.entity.TipoDPI;

public class InfoKitResponse {

	private List<TipoDPI> tipiDPISettore;
	private List<DPI> dpiDisponibili;
	
	public InfoKitResponse() {}

	public List<TipoDPI> getTipiDPISettore() {
		return tipiDPISettore;
	}

	public void setTipiDPISettore(List<TipoDPI> tipiDPISettore) {
		this.tipiDPISettore = tipiDPISettore;
	}

	public List<DPI> getDpiDisponibili() {
		return dpiDisponibili;
	}

	public void setDpiDisponibili(List<DPI> dpiDisponibili) {
		this.dpiDisponibili = dpiDisponibili;
	}
	
}
