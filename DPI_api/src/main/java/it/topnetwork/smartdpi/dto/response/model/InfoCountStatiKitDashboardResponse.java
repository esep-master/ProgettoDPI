package it.topnetwork.smartdpi.dto.response.model;

public class InfoCountStatiKitDashboardResponse {
	
	private int singleDPI;
	private int multiDPI;
	private int disattivati;
	private int ok;
	
	public InfoCountStatiKitDashboardResponse()  {}

	public int getSingleDPI() {
		return singleDPI;
	}

	public void setSingleDPI(int singleDPI) {
		this.singleDPI = singleDPI;
	}

	public int getMultiDPI() {
		return multiDPI;
	}

	public void setMultiDPI(int multiDPI) {
		this.multiDPI = multiDPI;
	}

	public int getDisattivati() {
		return disattivati;
	}

	public void setDisattivati(int disattivati) {
		this.disattivati = disattivati;
	}

	public int getOk() {
		return ok;
	}

	public void setOk(int ok) {
		this.ok = ok;
	}
	
}
