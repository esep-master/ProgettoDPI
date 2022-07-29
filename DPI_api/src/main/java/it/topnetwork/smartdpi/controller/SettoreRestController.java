package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.settore.InsertSettoreRequest;
import it.topnetwork.smartdpi.dto.request.settore.UpdateSettoreRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.service.SettoreService;

@RestController
@RequestMapping("settore")
public class SettoreRestController {
	
	@Autowired
	private SettoreService settoreService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista settori validi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Settore> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.settoreService.getSettori());
	}
	
	/**
	 * inserimento nuovo settore
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Settore> insert(@RequestHeader("id_utente") Long idUtente, @RequestBody InsertSettoreRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.settoreService.insert(dto, idUtente));
	}
	
	/**
	 * modifica settore
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PutMapping(value = "", produces = "application/json")
	public DetailResponse<Settore> update(@RequestHeader("id_utente") Long idUtente, @RequestBody UpdateSettoreRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.settoreService.update(dto, idUtente));
	}
	
	/**
	 * cancellazione settore
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.settoreService.delete(id, idUtente));
	}

}
