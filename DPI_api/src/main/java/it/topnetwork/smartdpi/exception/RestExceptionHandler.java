package it.topnetwork.smartdpi.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.utility.constants.Config;

@ControllerAdvice
@RestController
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	Logger logger = Logger.getLogger(RestExceptionHandler.class.getName());

	
	@Autowired
	private Config config;
	
	@ExceptionHandler(ApplicationException.class)
	public final BaseResponse applicationExceptionHandler(Exception ex) {
		ApplicationException ae =  (ApplicationException) ex;
		
		logger.log(Level.SEVERE, ae.getMessage());
		
		BaseResponse response = new BaseResponse(config.getApiVersion());
		
		response.setCode(ae.getCode());
		response.setMessage(ae.getMessage());

		logger.log(Level.INFO, String.format("Response: %s", response.toString()));
		
		return response;
	}
    
	@ExceptionHandler(Exception.class)
	public BaseResponse exceptionHandler(Exception ex) {
		BaseResponse response = new BaseResponse(config.getApiVersion());
		
		logger.log(Level.SEVERE, ex.getMessage());
		
		response.setCode(HttpStatus.BAD_REQUEST.value());
		response.setMessage(ex.getMessage());

		logger.log(Level.INFO, String.format("Response: %s", response.toString()));
		
		return response;
	}
	
}