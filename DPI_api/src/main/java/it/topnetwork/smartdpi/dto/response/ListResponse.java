package it.topnetwork.smartdpi.dto.response;

import java.util.List;

public class ListResponse<T> extends BaseResponse {

	private List<T> data;
	private long count;
	private int pageNumber;
	
	public ListResponse(String apiVersion) {
		super(apiVersion);
	}
	
	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
}
