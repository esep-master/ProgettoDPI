package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.request.allarme.AllarmeDPIRequest;
import it.topnetwork.smartdpi.dto.request.allarme.AllarmeUomoATerraRequest;
import it.topnetwork.smartdpi.dto.request.allarme.ChiusuraAllarmeDPIRequest;
import it.topnetwork.smartdpi.dto.request.allarme.LavorazioneAllarmeRequest;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Allarme;
import it.topnetwork.smartdpi.service.AllarmeService;

@RestController
@RequestMapping("allarme")
public class AllarmeRestController {
	
	@Autowired
	private AllarmeService allarmeService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista allarmi non chiusi visibile ad un utente
	 * @param dataAllarmeDaMS
	 * @param idUtente
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = {"list", "list/{dataAllarmeDa}"}, produces = "application/json")
	public ListResponse<Allarme> getLista(@RequestHeader("id_utente") Long idUtente, @PathVariable(name = "dataAllarmeDa", required = false) Long dataAllarmeDaMS) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.allarmeService.getAllarmi(dataAllarmeDaMS, idUtente, false));
	}
	
	/**
	 * recupera storico allarmi visibile ad un utente
	 * @param dataAllarmeDaMS
	 * @param idUtente
	 * @return
	 * @throws Exception 
	 */
	@GetMapping(value = {"storico/list", "storico/list/{dataAllarmeDa}"}, produces = "application/json")
	public ListResponse<Allarme> getStorico(@RequestHeader("id_utente") Long idUtente, @PathVariable(name = "dataAllarmeDa", required = false) Long dataAllarmeDaMS) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.allarmeService.getAllarmi(dataAllarmeDaMS, idUtente, true));
	}

	/**
	 * salva nuovo allarme per DPI non indossato
	 * @param idOperatore
	 * @param dto
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "dpi_non_indossato", produces = "application/json")
	public DetailResponse<Allarme> insertDPINonIndossato(@RequestHeader("id_operatore") Long idOperatore, @RequestBody AllarmeDPIRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.allarmeService.insertDPINonIndossato(dto, idOperatore));
	}
	
	/**
	 * salva nuovo allarme per uomo a terra
	 * @param idOperatore
	 * @param dto
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "uomo_a_terra", produces = "application/json")
	public DetailResponse<Allarme> insertUomoATerra(@RequestHeader("id_operatore") Long idOperatore, @RequestBody AllarmeUomoATerraRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.allarmeService.insertUomoATerra(dto, idOperatore));
	}
	
	/**
	 * presa in carico allarme
	 * @param idUtente
	 * @param dto
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(value = "lavorazione", produces = "application/json")
	public DetailResponse<Allarme> lavorazione(@RequestHeader("id_utente") Long idUtente, @RequestBody LavorazioneAllarmeRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.allarmeService.lavorazioneAllarme(dto, idUtente));
	}
	
	/**
	 * salva nuovo allarme
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "chiusura", produces = "application/json")
	public DetailResponse<Allarme> chiusura(@RequestHeader("id_utente") Long idUtente, @RequestBody LavorazioneAllarmeRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.allarmeService.chiusuraAllarme(dto, idUtente));
	}
	
	/**
	 * recupera lista allarmi operatore per la data odierna
	 * @param idOperatore
	 * @return
	 */
	@GetMapping(value = "operatore", produces = "application/json")
	public ListResponse<Allarme> getListaOperatore(@RequestHeader("id_operatore") Long idOperatore) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.allarmeService.getAllarmiOperatore(idOperatore));
	}
	
	/**
	 * chiude un allarme che è stato risolto dall'app 
	 * @param idOperatore
	 * @return
	 */
	@PostMapping(value = "chiusura_operatore", produces = "application/json")
	public DetailResponse<Allarme> chiusuraAllarmeDPINonIndossato(@RequestHeader("id_operatore") Long idOperatore, @RequestBody ChiusuraAllarmeDPIRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.allarmeService.chiusuraAllarmeDPINonIndossato(dto, idOperatore));
	}
	
}
