package it.topnetwork.smartdpi.controller.handler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.utility.constants.Config;

@Component
public class ResponseHandler {
	
	Logger logger = Logger.getLogger(ResponseHandler.class.getName());
	
	@Autowired
	private Config config;
	
	@SuppressWarnings("unused")
	public BaseResponse surroundWithCatchBase(ThrowingSupplierVoid task) throws Exception {
		
		BaseResponse response = new BaseResponse(config.getApiVersion());
		
		task.get();
		
		logger.log(Level.INFO, String.format("Response: %s", response.toString()));
		
		return response;
	}

	public <T> DetailResponse<T> surroundWithCatch(ThrowingSupplier<T> task) throws Exception {
		
		DetailResponse<T> response = new DetailResponse<T>(config.getApiVersion());
		
		T supplied = task.get();
		response.setData(supplied);
		
		logger.log(Level.INFO, String.format("Response: %s", response.toString()));
		
		return response;
	}

	public <T> ListResponse<T> surroundWithCatchList(ThrowingSupplier<List<T>> task) throws Exception {
		
		ListResponse<T> response = new ListResponse<T>(config.getApiVersion());
		
		List<T> supplied = (List<T>) task.get();
		response.setData(supplied);
		
		logger.log(Level.INFO, String.format("Response: %s", response.toString()));
		
		return response;
	}

}
