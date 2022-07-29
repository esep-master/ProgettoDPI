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
import it.topnetwork.smartdpi.dto.request.utente.ChangePasswordRequest;
import it.topnetwork.smartdpi.dto.request.utente.InsertUtenteRequest;
import it.topnetwork.smartdpi.dto.request.utente.UpdateUtenteRequest;
import it.topnetwork.smartdpi.dto.request.utente.UtenteSedeCommessaRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Utente;
import it.topnetwork.smartdpi.entity.UtenteSedeCommessa;
import it.topnetwork.smartdpi.service.UtenteService;

@RestController
@RequestMapping("utente")
public class UtenteRestController {
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private ResponseHandler handler;

	/**
	 * recupera lista utenti visibili
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Utente> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.utenteService.getListaUtenti(idUtente));
	}
	
	/**
	 * inserimento nuovo utente
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Utente> insert(@RequestHeader("id_utente") Long idUtente, @RequestBody InsertUtenteRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.utenteService.insert(dto, idUtente));
	}
	
	/**
	 * modifica utente
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PutMapping(value = "", produces = "application/json")
	public DetailResponse<Utente> update(@RequestHeader("id_utente") Long idUtente, @RequestBody UpdateUtenteRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.utenteService.update(dto, idUtente));
	}
	
	/**
	 * cancellazione utente
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.utenteService.delete(id, idUtente));
	}
	
	/**
	 * cambio password utente
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "cambia_password", produces = "application/json")
	public BaseResponse changePassword(@RequestHeader("id_utente") Long idUtente, @RequestBody ChangePasswordRequest dto) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.utenteService.changePassword(dto, idUtente));
	}
	
	/**
	 * associa utente a sede commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "associa_sede_commessa", produces = "application/json")
	public DetailResponse<Utente> associaSedeCommessa(@RequestHeader("id_utente") Long idUtente, @RequestBody UtenteSedeCommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.utenteService.associaSedeCommessa(dto, idUtente, true));
	}
	
	/**
	 * disassocia utente a sede commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "disassocia_sede_commessa", produces = "application/json")
	public DetailResponse<Utente> disassociaSedeCommessa(@RequestHeader("id_utente") Long idUtente, @RequestBody UtenteSedeCommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.utenteService.associaSedeCommessa(dto, idUtente, false));
	}
	
	/**
	 * recupera lista commesse visibili
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "commesse", produces = "application/json")
	public ListResponse<UtenteSedeCommessa> getListaCommesse(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.utenteService.getListaCommesse(idUtente));
	}
	
}
