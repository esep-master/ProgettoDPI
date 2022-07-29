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
import it.topnetwork.smartdpi.dto.request.commessa.CommessaRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Commessa;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.service.CommessaService;

@RestController
@RequestMapping("commessa")
public class CommessaRestController {

	@Autowired
	private CommessaService commessaService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista commesse valide
	 * @param idUtente
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Commessa> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.commessaService.getCommesse(idUtente));
	}
	
	/**
	 * salvataggio commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Commessa> save(@RequestHeader("id_utente") Long idUtente, @RequestBody CommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.commessaService.save(dto, idUtente));
	}
	
	/**
	 * cancellazione commessa
	 * @param idUtente
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.commessaService.delete(id, idUtente));
	}
	
	/**
	 * recupera lista operatori per commessa
	 * @param idUtente
	 * @param idCommessa
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = "operatori/{idCommessa}", produces = "application/json")
	public ListResponse<Operatore> getOperatori(@RequestHeader("id_utente") Long idUtente, @PathVariable("idCommessa") Long idCommessa) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.commessaService.getOperatori(idCommessa));
	}
	
}
