package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.auth.LoginAppRequest;
import it.topnetwork.smartdpi.dto.request.auth.LoginRequest;
import it.topnetwork.smartdpi.dto.request.auth.RecoveryPasswordRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.model.LoginAppResponse;
import it.topnetwork.smartdpi.dto.response.model.LoginResponse;
import it.topnetwork.smartdpi.service.AuthService;

@RestController
@RequestMapping("auth")
public class AuthRestController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * login utente portale
	 * @param loginReq
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<LoginResponse> auth(@RequestBody LoginRequest loginReq) throws Exception {		
		return this.handler.surroundWithCatch(() -> this.authService.auth(loginReq.getUsername(), loginReq.getPassword()));
	}
	
	/**
	 * recupero password operatore
	 * @param recoveryPasswordReq
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/recovery", produces = "application/json")
	public BaseResponse recoveryPassword(@RequestBody RecoveryPasswordRequest recoveryPasswordReq) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.authService.recoveryPassword(recoveryPasswordReq.getEmail()));
	}

	/**
	 * login operatore app
	 * @param loginAppReq
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "app", produces = "application/json")
	public DetailResponse<LoginAppResponse> authApp(@RequestBody LoginAppRequest loginAppReq) throws Exception {
		return this.handler.surroundWithCatch(() -> this.authService.authApp(loginAppReq.getMatricola(), loginAppReq.getPassword()));
	}
	
	/**
	 * recupero password operatore
	 * @param recoveryPasswordReq
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "/recovery/operatore", produces = "application/json")
	public BaseResponse recoveryPasswordOperatore(@RequestBody RecoveryPasswordRequest recoveryPasswordReq) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.authService.recoveryPasswordOperatore(recoveryPasswordReq.getEmail()));
	}
	
}
