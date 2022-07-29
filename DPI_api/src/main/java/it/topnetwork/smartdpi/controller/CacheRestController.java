package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.service.CacheService;

@RestController
@RequestMapping("cache")
public class CacheRestController {
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private ResponseHandler handler;

	
	@PostMapping(value = { "clear/{entryName}", "clear" }, produces = "application/json")
	public BaseResponse clearCache(@RequestHeader("id_utente") Long idUtente, @PathVariable(name = "entryName", required = false) String entryName) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.cacheService.clearCache(entryName));
	}
	
}
