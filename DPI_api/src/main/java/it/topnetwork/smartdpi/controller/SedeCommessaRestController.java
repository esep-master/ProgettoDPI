package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.sede.SedeCommessaRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.SedeCommessa;
import it.topnetwork.smartdpi.service.SedeCommessaService;

@RestController
@RequestMapping("sede_commessa")
public class SedeCommessaRestController {

	@Autowired
	private SedeCommessaService sedeCommessaService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista sedi commesse valide
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<SedeCommessa> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.sedeCommessaService.getSediCommesse(idUtente));
	}
	
	/**
	 * salvataggio sede commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<SedeCommessa> save(@RequestHeader("id_utente") Long idUtente, @RequestBody SedeCommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.sedeCommessaService.save(dto, idUtente));
	}
	
	/**
	 * cancellazione sede commessa
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.sedeCommessaService.delete(id, idUtente));
	}
	
}
