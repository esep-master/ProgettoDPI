package it.topnetwork.smartdpi.dto.response.model;

import java.util.List;

public class InfoDashboardResponse {

	private int numeroCommesseAttive;
	private int numeroKitNonAssociati;
	private int numeroDPINonAssociati;
	private List<InfoKitSettoreDashboardResponse> kitSettore;
	private InfoCountStatiKitDashboardResponse statiKit;
	
	public InfoDashboardResponse()	{}

	public int getNumeroCommesseAttive() {
		return numeroCommesseAttive;
	}

	public void setNumeroCommesseAttive(int numeroCommesseAttive) {
		this.numeroCommesseAttive = numeroCommesseAttive;
	}

	public int getNumeroKitNonAssociati() {
		return numeroKitNonAssociati;
	}

	public void setNumeroKitNonAssociati(int numeroKitNonAssociati) {
		this.numeroKitNonAssociati = numeroKitNonAssociati;
	}

	public int getNumeroDPINonAssociati() {
		return numeroDPINonAssociati;
	}

	public void setNumeroDPINonAssociati(int numeroDPINonAssociati) {
		this.numeroDPINonAssociati = numeroDPINonAssociati;
	}

	public List<InfoKitSettoreDashboardResponse> getKitSettore() {
		return kitSettore;
	}

	public void setKitSettore(List<InfoKitSettoreDashboardResponse> kitSettore) {
		this.kitSettore = kitSettore;
	}

	public InfoCountStatiKitDashboardResponse getStatiKit() {
		return statiKit;
	}

	public void setStatiKit(InfoCountStatiKitDashboardResponse statiKit) {
		this.statiKit = statiKit;
	}
	
}
