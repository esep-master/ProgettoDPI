package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.config.ConfigurazioneRequest;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.service.ConfigurazioneService;

@RestController
@RequestMapping("configurazione")
public class ConfigurazioneRestController {

	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista configurazioni valide
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Configurazione> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.configurazioneService.getConfigs());
	}
	
	/**
	 * salvataggio configurazione
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Configurazione> save(@RequestHeader("id_utente") Long idUtente, @RequestBody ConfigurazioneRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.configurazioneService.save(dto, idUtente));
	}
	
}
