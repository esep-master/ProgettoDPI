package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.beacon.StatoBeaconWrapperRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.service.BeaconService;

@RestController
@RequestMapping("beacon")
public class BeaconRestController {

	@Autowired
	private BeaconService beaconService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * salva stati beacon
	 * @param idOperatore
	 * @param dto
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "aggiorna_stato", produces = "application/json")
	public BaseResponse insert(@RequestHeader("id_operatore") Long idOperatore, @RequestBody StatoBeaconWrapperRequest dto) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.beaconService.save(dto.getBeacon(), idOperatore));
	}
	
}
