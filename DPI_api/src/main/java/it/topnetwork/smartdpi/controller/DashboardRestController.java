package it.topnetwork.smartdpi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.topnetwork.smartdpi.controller.handler.ResponseHandler;
import it.topnetwork.smartdpi.dto.response.DetailResponse;
import it.topnetwork.smartdpi.dto.response.model.InfoDashboardResponse;
import it.topnetwork.smartdpi.service.DashboardService;

@RestController
@RequestMapping("dashboard")
public class DashboardRestController {

	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private ResponseHandler handler;
	
	/**
	 * recupera info dashboard per un particolare utente
	 * @param idUtente
	 * @return
	 */
	@GetMapping(value = "info", produces = "application/json")
	public DetailResponse<InfoDashboardResponse> insert(@RequestHeader("id_utente") Long idUtente) throws Exception {
		return this.handler.surroundWithCatch(() -> this.dashboardService.getInfoDashboard(idUtente));
	}

}
