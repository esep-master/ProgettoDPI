package it.topnetwork.smartdpi.dto.response;

public class DetailResponse<T> extends BaseResponse {
	
	private T data;

	public DetailResponse(String apiVersion) {
		super(apiVersion);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
