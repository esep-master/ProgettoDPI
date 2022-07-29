package it.topnetwork.smartdpi.dto.request.kit;

import java.util.Date;
import java.util.List;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.utility.Utility;

public class KitRequest extends BaseRequest {

	private long idKit;
	private long idOperatore;
	private long idSettore;
	private String modello;
	private Date dataAssegnazione;
	private String note;
	private String noteSbloccoTotale;
	private List<DPIKitRequest> listaDPI;
	
	public KitRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idOperatore != 0 &&
				idSettore != 0 &&
				Utility.isValid(modello) &&
				Utility.isValid(listaDPI);
		return isValid;
	}

	public long getIdKit() {
		return idKit;
	}

	public void setIdKit(long idKit) {
		this.idKit = idKit;
	}

	public long getIdOperatore() {
		return idOperatore;
	}

	public void setIdOperatore(long idOperatore) {
		this.idOperatore = idOperatore;
	}

	public long getIdSettore() {
		return idSettore;
	}

	public void setIdSettore(long idSettore) {
		this.idSettore = idSettore;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public Date getDataAssegnazione() {
		return dataAssegnazione;
	}

	public void setDataAssegnazione(Date dataAssegnazione) {
		this.dataAssegnazione = dataAssegnazione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNoteSbloccoTotale() {
		return noteSbloccoTotale;
	}

	public void setNoteSbloccoTotale(String noteSbloccoTotale) {
		this.noteSbloccoTotale = noteSbloccoTotale;
	}

	public List<DPIKitRequest> getListaDPI() {
		return listaDPI;
	}

	public void setListaDPI(List<DPIKitRequest> listaDPI) {
		this.listaDPI = listaDPI;
	}

}
