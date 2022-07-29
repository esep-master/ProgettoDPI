package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.intervento.InizioInterventoRequest;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.entity.Intervento;
import it.topnetwork.smartdpi.service.InterventoService;

@RestController
@RequestMapping("intervento")
public class InterventoRestController {
	
	@Autowired
	private InterventoService interventoService;
	
	@Autowired
	private ResponseHandler handler;

	/**
	 * inizio intervento
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "inizio", produces = "application/json")
	public DetailResponse<Intervento> inizioIntervento(@RequestHeader("id_operatore") Long idOperatore, @RequestBody InizioInterventoRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.interventoService.inizio(dto, idOperatore));
	}
	
	/**
	 * inizio intervento
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@PostMapping(value = "fine/{id}", produces = "application/json")
	public DetailResponse<Intervento> fineIntervento(@RequestHeader("id_operatore") Long idOperatore, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatch(() -> this.interventoService.fine(id, idOperatore));
	}
	
}
