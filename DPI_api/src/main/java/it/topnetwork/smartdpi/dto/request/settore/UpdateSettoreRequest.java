package it.topnetwork.smartdpi.dto.request.settore;

public class UpdateSettoreRequest extends InsertSettoreRequest {
	
	private long idSettore;
	
	public UpdateSettoreRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		
		isValid = idSettore != 0;// && super.isValid();
		
		return isValid;
	}

	public long getIdSettore() {
		return idSettore;
	}

	public void setIdSettore(long idSettore) {
		this.idSettore = idSettore;
	}

}
