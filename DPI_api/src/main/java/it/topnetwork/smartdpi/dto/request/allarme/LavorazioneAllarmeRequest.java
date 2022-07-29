package it.topnetwork.smartdpi.dto.request.allarme;

import it.topnetwork.smartdpi.dto.request.BaseRequest;

public class LavorazioneAllarmeRequest extends BaseRequest {
	
	private long idAllarme;
	private boolean falsoAllarme;
	private String note;
	
	public LavorazioneAllarmeRequest() {
		super();
	}

	@Override
	public boolean isValid() {
		boolean isValid = false;
		isValid = idAllarme != 0;
		return isValid;
	}

	public long getIdAllarme() {
		return idAllarme;
	}

	public void setIdAllarme(long idAllarme) {
		this.idAllarme = idAllarme;
	}

	public boolean isFalsoAllarme() {
		return falsoAllarme;
	}

	public void setFalsoAllarme(boolean falsoAllarme) {
		this.falsoAllarme = falsoAllarme;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
