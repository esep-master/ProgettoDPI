package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Ruolo;
import it.topnetwork.smartdpi.entity.StatoAllarme;
import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.entity.TipoAzioneOperatore;
import it.topnetwork.smartdpi.entity.TipoBeacon;
import it.topnetwork.smartdpi.entity.TipoDPI;
import it.topnetwork.smartdpi.entity.TipoOperatore;
import it.topnetwork.smartdpi.service.RuoloService;
import it.topnetwork.smartdpi.service.StatoAllarmeService;
import it.topnetwork.smartdpi.service.TipoAllarmeService;
import it.topnetwork.smartdpi.service.TipoAzioneOperatoreService;
import it.topnetwork.smartdpi.service.TipoBeaconService;
import it.topnetwork.smartdpi.service.TipoDPIService;
import it.topnetwork.smartdpi.service.TipoOperatoreService;

@RestController
@RequestMapping("lookup")
public class LookUpRestController {
	
	@Autowired
	private RuoloService ruoloService;

	@Autowired
	private TipoOperatoreService tipoOperatoreService;
	
	@Autowired
	private TipoDPIService tipoDPIService;
	
	@Autowired
	private TipoBeaconService tipoBeaconService;
	
	@Autowired
	private TipoAzioneOperatoreService tipoAzioneOperatoreService;
	
	@Autowired
	private TipoAllarmeService tipoAllarmeService;
	
	@Autowired
	private StatoAllarmeService statoAllarmeService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista ruoli
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "ruoli", produces = "application/json")
	public ListResponse<Ruolo> getRuoli(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.ruoloService.getRuoli());
	}
	
	/**
	 * recupera lista tipi operatore
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "tipi_operatore", produces = "application/json")
	public ListResponse<TipoOperatore> getTipiOperatore(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.tipoOperatoreService.getTipiOperatore());
	}
	
	/**
	 * recupera lista tipi dpi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "tipi_dpi", produces = "application/json")
	public ListResponse<TipoDPI> getTipiDPI(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.tipoDPIService.getTipiDPI());
	}
	
	/**
	 * recupera lista tipi beacon
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "tipi_beacon", produces = "application/json")
	public ListResponse<TipoBeacon> getTipiBeacon(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.tipoBeaconService.getTipiBeacon());
	}
	
	/**
	 * recupera lista tipi azioni operatori
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "tipi_azioni_operatori", produces = "application/json")
	public ListResponse<TipoAzioneOperatore> getTipiAzioniOperatori(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.tipoAzioneOperatoreService.getTipiAzioniOperatori());
	}
	
	/**
	 * recupera lista tipi allarmi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "tipi_allarmi", produces = "application/json")
	public ListResponse<TipoAllarme> getTipiAllarmi(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.tipoAllarmeService.getTipiAllarmi());
	}
	
	/**
	 * recupera lista stati allarmi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "stati_allarmi", produces = "application/json")
	public ListResponse<StatoAllarme> getStatiAllarmi(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.statoAllarmeService.getStatiAllarmi());
	}
	
}
