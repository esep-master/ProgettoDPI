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
import it.topnetwork.smartdpi.dto.request.kit.KitRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.dto.response.model.InfoKitResponse;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.service.KitService;

@RestController()
@RequestMapping("kit")
public class KitRestController {
	
	@Autowired
	private KitService kitService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista kit validi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<Kit> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.kitService.getKit());
	}

	/**
	 * salvataggio dati kit con dpi associati
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<Kit> save(@RequestHeader("id_utente") Long idUtente, @RequestBody KitRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.kitService.save(dto, idUtente));
	}
	
	/**
	 * cancellazione kit
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("id") Long id) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.kitService.delete(id, idUtente));
	}
	
	@GetMapping(value = "info/{idSettore}/{idOperatore}", produces = "application/json")
	public DetailResponse<InfoKitResponse> getInfo(@RequestHeader("id_utente") Long idUtente, @PathVariable("idSettore") Long idSettore, @PathVariable("idOperatore") Long idOperatore) throws Exception {
		return this.handler.surroundWithCatch(() -> this.kitService.getInfoKit(idSettore, idOperatore, idUtente));
	}
	
}
