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
import it.topnetwork.smartdpi.dto.request.operatore.InsertOperatoreRequest;
import it.topnetwork.smartdpi.dto.request.operatore.OperatoreKitRequest;
import it.topnetwork.smartdpi.dto.request.operatore.OperatoreSedeCommessaRequest;
import it.topnetwork.smartdpi.dto.request.operatore.UpdateOperatoreRequest;
import it.topnetwork.smartdpi.dto.request.utente.ChangePasswordRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.AzioneOperatore;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.service.AzioneOperatoreService;
import it.topnetwork.smartdpi.service.OperatoreService;

@RestController
@RequestMapping("operatore")
public class OperatoreRestController {
	
	@Autowired
	private OperatoreService operatoreService;
	
	@Autowired
	private AzioneOperatoreService azioneOperatoreService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista operatori visibili
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Operatore> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.operatoreService.getListaOperatori(idUtente));
	}
	
	/**
	 * inserimento nuovo operatore
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Operatore> insert(@RequestHeader("id_utente") Long idUtente, @RequestBody InsertOperatoreRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.insert(dto, idUtente));
	}
	
	/**
	 * modifica operatore
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PutMapping(value = "", produces = "application/json")
	public DetailResponse<Operatore> update(@RequestHeader("id_utente") Long idUtente, @RequestBody UpdateOperatoreRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.update(dto, idUtente));
	}
	
	/**
	 * cancellazione operatore
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.operatoreService.delete(id, idUtente));
	}
	
	/**
	 * associa operatore a sede commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "associa_sede_commessa", produces = "application/json")
	public DetailResponse<Operatore> associaSedeCommessa(@RequestHeader("id_utente") Long idUtente, @RequestBody OperatoreSedeCommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.associaSedeCommessa(dto, idUtente, true));
	}
	
	/**
	 * disassocia operatore a sede commessa
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "disassocia_sede_commessa", produces = "application/json")
	public DetailResponse<Operatore> disassociaSedeCommessa(@RequestHeader("id_utente") Long idUtente, @RequestBody OperatoreSedeCommessaRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.associaSedeCommessa(dto, idUtente, false));
	}
	
	/**
	 * associa operatore a kit
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "associa_kit", produces = "application/json")
	public DetailResponse<Operatore> associaKit(@RequestHeader("id_utente") Long idUtente, @RequestBody OperatoreKitRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.associaKit(dto, idUtente, true));
	}
	
	/**
	 * disassocia operatore kit
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "disassocia_kit", produces = "application/json")
	public DetailResponse<Operatore> disassociaKit(@RequestHeader("id_utente") Long idUtente, @RequestBody OperatoreKitRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.operatoreService.associaKit(dto, idUtente, false));
	}
	
	/**
	 * cambio password operatore
	 * @param idOperatore
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "cambia_password", produces = "application/json")
	public BaseResponse changePassword(@RequestHeader("id_operatore") Long idOperatore, @RequestBody ChangePasswordRequest dto) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.operatoreService.changePassword(dto, idOperatore));
	}

	/**
	 * logout da app
	 * @param idOperatore
	 * @return
	 */
	@PostMapping(value = "logout", produces = "application/json")
	public BaseResponse logoutApp(@RequestHeader("id_operatore") Long idOperatore) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.operatoreService.logoutApp(idOperatore));
	}	
	
	/**
	 * resetta password impostando quella iniziale
	 * @param idUtente
	 * @param idOperatore
	 * @return
	 */
	@PostMapping(value = "reset_password/{idOperatore}", produces = "application/json")
	public BaseResponse resetPassword(@RequestHeader("id_utente") Long idUtente, @PathVariable("idOperatore") Long idOperatore) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.operatoreService.resetPassword(idOperatore, idUtente));
	}
	
	/**
	 * recupera azioni operatore
	 * @param idUtente
	 * @param idOperatore
	 * @return
	 */
	@GetMapping(value = "azioni/{idOperatore}", produces = "application/json")
	public ListResponse<AzioneOperatore> getAzioniOperatore(@RequestHeader("id_utente") Long idUtente, @PathVariable("idOperatore") Long idOperatore) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.azioneOperatoreService.getAzioniOperatore(idOperatore));
	}
	
}
