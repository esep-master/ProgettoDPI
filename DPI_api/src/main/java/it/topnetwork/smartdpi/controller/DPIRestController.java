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
import it.topnetwork.smartdpi.dto.request.dpi.DPIRequest;
import it.topnetwork.smartdpi.dto.response.BaseResponse;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.ListResponse;
import it.topnetwork.smartdpi.entity.Beacon;
import it.topnetwork.smartdpi.entity.DPI;
import it.topnetwork.smartdpi.service.DPIService;

@RestController()
@RequestMapping("dpi")
public class DPIRestController {
	
	@Autowired
	private DPIService dpiService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera lista dpi validi
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "list", produces = "application/json")
	public ListResponse<DPI> getLista(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.dpiService.getDPI());
	}
	
	/**
	 * salvataggio dati dpi e beacon
	 * @param idUtente
	 * @param dto
	 * @return
	 */
	@PostMapping(value = "", produces = "application/json")
	public DetailResponse<DPI> save(@RequestHeader("id_utente") Long idUtente, @RequestBody DPIRequest dto) throws Exception {
		return this.handler.surroundWithCatch(() -> this.dpiService.save(dto, idUtente));
	}
	
	/**
	 * cancellazione dpi
	 * @param idUtente
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "{idDPI}/{idBeacon}", produces = "application/json")
	public BaseResponse delete(@RequestHeader("id_utente") Long idUtente, @PathVariable("idDPI") Long idDPI, @PathVariable("idBeacon") Long idBeacon) throws Exception {
		return this.handler.surroundWithCatchBase(() -> this.dpiService.delete(idDPI, idBeacon, idUtente));
	}
	
	/**
	 * recupera lista beacon dispoonibili
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "beacon_disponibili", produces = "application/json")
	public ListResponse<Beacon> getListaBeaconDisponibili(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatchList(() -> this.dpiService.getBeaconDisponibili());
	}
	
}
