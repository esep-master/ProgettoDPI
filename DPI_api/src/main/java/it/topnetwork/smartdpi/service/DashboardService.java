package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.CommessaRepository;
import it.topnetwork.smartdpi.dao.DPIRepository;
import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.SettoreRepository;
import it.topnetwork.smartdpi.dto.response.model.InfoCountStatiKitDashboardResponse;
import it.topnetwork.smartdpi.dto.response.model.InfoDashboardResponse;
import it.topnetwork.smartdpi.dto.response.model.InfoKitSettoreDashboardResponse;
import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class DashboardService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommessaRepository commessaRepository;
	
	@Autowired
	private SettoreRepository settoreRepository;
	
	@Autowired
	private KitRepository kitRepository;
	
	@Autowired
	private DPIRepository dpiRepository;
	
	/**
	 * recupera info dashboard
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	public InfoDashboardResponse getInfoDashboard(Long idUtente) throws ApplicationException {
		InfoDashboardResponse infoDashboard = new InfoDashboardResponse();
		try {
			// recupero numero commesse attive per l'utente
			int numeroCommesseAttive = this.commessaRepository.countAllValidi(idUtente);
			// recupero numero kit non associati
			int numeroKitNonAssociati = this.kitRepository.countNonAssociati();
			// recupero numero DPI non associati
			int numeroDPItNonAssociati = this.dpiRepository.countNonAssociati();
			// recupero numero kit per settore
			List<InfoKitSettoreDashboardResponse> kitSettore = this.getInfoKitSettore(idUtente);
			// recupero count per stati kit
			InfoCountStatiKitDashboardResponse statiKit = this.getCountStatiKit(idUtente);
			// set campi
			infoDashboard.setNumeroCommesseAttive(numeroCommesseAttive);
			infoDashboard.setNumeroKitNonAssociati(numeroKitNonAssociati);
			infoDashboard.setNumeroDPINonAssociati(numeroDPItNonAssociati);
			infoDashboard.setKitSettore(kitSettore);
			infoDashboard.setStatiKit(statiKit);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return infoDashboard;
	}

	/**
	 * recupera numero kit per ogni settore
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	private List<InfoKitSettoreDashboardResponse> getInfoKitSettore(Long idUtente) throws ApplicationException {
		List<InfoKitSettoreDashboardResponse> infoKit = new ArrayList<>();
		try {
			// recupera lista settori
			List<Settore> settori = this.settoreRepository.findAllValidi();
			if(settori != null && !settori.isEmpty()) {
				for(Settore settore : settori) {
					// recupera numero kit per ogni settore
					int numeroKitSettore = this.kitRepository.countBySettore(settore.getId());
					// set campi
					InfoKitSettoreDashboardResponse infoKitSettore = new InfoKitSettoreDashboardResponse();
					infoKitSettore.setSettore(settore);
					infoKitSettore.setNumeroKit(numeroKitSettore);
					// aggiungi a lista
					infoKit.add(infoKitSettore);
				}
			}
 		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return infoKit;
	}
	
	/**
	 * count stati kit
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private InfoCountStatiKitDashboardResponse getCountStatiKit(Long idUtente) throws ApplicationException {
		InfoCountStatiKitDashboardResponse countStatiKit = new InfoCountStatiKitDashboardResponse();
		// numero kit con un DPI in allarme
		int singleDPI = this.kitRepository.countSingoloDPIAllarme(idUtente);
		// numero kit con piu di un DPI in allarme
		int multiDPI = this.kitRepository.countMultiDPIAllarme(idUtente);
		// numero kit disattivati
		int disattivati = this.kitRepository.countDisattivati(idUtente);
		// numero kit OK
		int ok = this.kitRepository.countOk(idUtente);
		// set campi
		countStatiKit.setSingleDPI(singleDPI);
		countStatiKit.setMultiDPI(multiDPI);
		countStatiKit.setDisattivati(disattivati);
		countStatiKit.setOk(ok);
		
		return countStatiKit;
	}
}
