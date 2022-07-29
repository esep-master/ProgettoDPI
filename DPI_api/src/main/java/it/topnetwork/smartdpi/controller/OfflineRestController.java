package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.offline.OfflineSyncRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.service.OfflineService;

@RestController
@RequestMapping("offline")
public class OfflineRestController {
	
	@Autowired
	private OfflineService offlineService;
	
	@Autowired
	private ResponseHandler handler;

	/**
	 * salva nuovo allarme per DPI non indossato
	 * @param idOperatore
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "sync", produces = "application/json")
	public BaseResponse sync(@RequestHeader("id_operatore") Long idOperatore, @RequestBody OfflineSyncRequest dto) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.offlineService.sync(dto, idOperatore));
	}
	
}
