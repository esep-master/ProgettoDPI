package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.DPIKitRepository;
import it.topnetwork.smartdpi.dao.DPIRepository;
import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.OperatoreRepository;
import it.topnetwork.smartdpi.dao.SettoreRepository;
import it.topnetwork.smartdpi.dao.TipoDPISettoreRepository;
import it.topnetwork.smartdpi.dto.request.kit.DPIKitRequest;
import it.topnetwork.smartdpi.dto.request.kit.KitRequest;
import it.topnetwork.smartdpi.dto.response.model.InfoKitResponse;
import it.topnetwork.smartdpi.entity.DPI;
import it.topnetwork.smartdpi.entity.DPIKit;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.entity.SettoreDPI;
import it.topnetwork.smartdpi.entity.TipoDPI;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class KitService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KitRepository kitRepository;
	
	@Autowired
	private SettoreRepository settoreRepository;
	
	@Autowired
	private DPIRepository dpiRepository;
	
	@Autowired
	private DPIKitRepository dpiKitRepository;
	
	@Autowired
	private TipoDPISettoreRepository tipoDPISettoreRepository;
	
	@Autowired
	private OperatoreRepository operatoreRepository;
	
	/**
	 * recupera lista kit
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Kit> getKit() throws ApplicationException {
		List<Kit> kitList = null;
		try {
			kitList = this.kitRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return kitList;
	}
	
	/**
	 * salvataggio dati kit e dpi associati
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Kit save(KitRequest dto, Long idUtente) throws ApplicationException {
		Kit kit = null;
		try {
			// verifica kit
			kit = this.verificaKit(dto, idUtente);
			// salvataggio kit
			kit = this.kitRepository.save(kit);
			// rimuovo associazioni kit - dpi gia esistenti
			this.dpiKitRepository.deleteDPIKit(kit.getId(), idUtente);
			// creo nuove associazioni kit - dpi
			Set<DPIKit> nuoveAssociazioniKit = this.inserisciDpiKit(kit, dto.getListaDPI(), idUtente);
			kit.setDpiKit(nuoveAssociazioniKit);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return kit;
	}
	
	/**
	 * cancellazione logica kit
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// richiama procedure per cancellazione logica kit e entita associate
			Map<String, Object> output = this.kitRepository.eliminaKit(id, idUtente);
			// valida output
			Utility.validateStoredProcedureOutput(output);
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}
	
	/**
	 * recupera info kit dato settore e operatore
	 * @param idSettore
	 * @param idOperatore
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	public InfoKitResponse getInfoKit(Long idSettore, Long idOperatore, Long idUtente) throws ApplicationException {
		InfoKitResponse response = new InfoKitResponse();
		try {
			// recupera lista tipi dpi per il settore
			List<TipoDPI> tipiDPISettore = this.tipoDPISettoreRepository.findBySettore(idSettore);
			if(tipiDPISettore == null || tipiDPISettore.isEmpty()) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "non ci sono tipologie DPI associate con questo settore");
			}
			// recupera lista dpi disponibili
			List<DPI> dpiDisponibili = getDPIDisponibili(idSettore, idOperatore);
			// set campi
			response.setTipiDPISettore(tipiDPISettore);
			response.setDpiDisponibili(dpiDisponibili);
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return response;
	}
	
	/**
	 * verifica kit e dpi
	 * @param dto
	 * @param idUtenteOperazione
	 * @return
	 * @throws ApplicationException
	 */
	private Kit verificaKit(KitRequest dto, Long idUtenteOperazione) throws ApplicationException {
		Kit kit = null;
		if(dto != null && dto.isValid()) {
			// controllo inserimento o modifica kit
			boolean isInserimento = dto.getIdKit() == 0;
			if(isInserimento) {
				// inserimento - crea entity e set dati inserimento
				kit = new Kit();
				kit.setDatiInserimento(idUtenteOperazione);
			} else {
				// modifica - recupera entity e set dati ultima modifica
				kit = this.kitRepository.findValidById(dto.getIdKit());
				if(kit == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "kit specificato inesistente");
				}
				kit.setDatiUltimaModifica(idUtenteOperazione);
			}
			// controllo settore
			Settore settoreKit = this.settoreRepository.findValidById(dto.getIdSettore());
			if(settoreKit == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "settore specificato inesistente");
			}
			// controllo operatore
			Operatore operatoreKit = this.operatoreRepository.findValidById(dto.getIdOperatore());
			if(operatoreKit == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore inesistente");
			}
			// verifica kit operatore già presente per quel settore
			Kit checkKit = this.kitRepository.findByOperatoreAndSettore(dto.getIdOperatore(), dto.getIdSettore());
			// errore se esiste un kit 
			if(checkKit != null && (isInserimento || !checkKit.getId().equals(dto.getIdKit()))) {
				throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "kit per il settore già esistente");
			}
			// set campi
			kit.setOperatore(operatoreKit);
			kit.setSettore(settoreKit);
			kit.setModello(dto.getModello());
			kit.setDataAssegnazione(dto.getDataAssegnazione());
			kit.setNote(dto.getNote());
			kit.setNoteSbloccoTotale(dto.getNoteSbloccoTotale());
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return kit;
	}
	
	/**
	 * inserisce nuove associazioni dpi - kit
	 * @param kit
	 * @param listaIdPI
	 * @param idUtente
	 * @throws ApplicationException
	 */
	private Set<DPIKit> inserisciDpiKit(Kit kit, List<DPIKitRequest> listaIdPI, Long idUtente) throws ApplicationException {
		Set<DPIKit> nuoveAssociazioni = new HashSet<>();
		for(DPIKitRequest dpiReq : listaIdPI) {
			// verifica esistenza dpi
			DPI dpi = this.dpiRepository.findValidById(dpiReq.getIdDPI());
			if(dpi == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "dpi specificato inesistente");
			}
			// verifica se DPI già associato ad un altro operatore
			if(dpi.getDpiKit() != null) {
				for(DPIKit dpiKit : dpi.getDpiKit()) {
					if(dpiKit.getKit().getOperatore() != null && !dpiKit.getKit().getOperatore().getId().equals(kit.getOperatore().getId())) {
						String errorMsg = dpi.getTipoDPI().getNome() + " già assoociata ad un kit per un altro operatore";
						throw new ApplicationException(ErrorCode.DPI_KIT_ASSOCIATED_OTHER, errorMsg);
					}
				}
			}
			// se sono valide tutte e due le date, controllo che A > DA
			// posso inserire anche solo sblocco_allarme_da, vale da quel giorno in poi
			if(dpiReq.getSbloccoAllarmeDa() != null && dpiReq.getSbloccoAllarmeA() != null) {
				if(dpiReq.getSbloccoAllarmeA().before(dpiReq.getSbloccoAllarmeDa())) {
					throw new ApplicationException(ErrorCode.DATE_PERIOD_INVALID, "intervallo di date non valido");
				}
			}
			// creo nuova associazione
			DPIKit dpiKit = new DPIKit();
			dpiKit.setKit(kit);
			dpiKit.setDpi(dpi);
			dpiKit.setSbloccoAllarmeDa(dpiReq.getSbloccoAllarmeDa());
			dpiKit.setSbloccoAllarmeA(dpiReq.getSbloccoAllarmeA());
			dpi.setDatiInserimento(idUtente);
			// salvataggio nuova entity
			dpiKit = this.dpiKitRepository.save(dpiKit);
			nuoveAssociazioni.add(dpiKit);
		}
		return nuoveAssociazioni;
	}

	/**
	 * recupera DPI disponibili per operatore e settore
	 * @param idSettore
	 * @param idOperatore
	 * @return
	 */
	private List<DPI> getDPIDisponibili(Long idSettore, Long idOperatore) {
		List<DPI> dpiDisponibili = new ArrayList<>();
		List<DPI> dpiDisponibiliOperatore = this.dpiRepository.findDisponibili(idOperatore, idSettore);
		if(dpiDisponibiliOperatore != null && !dpiDisponibiliOperatore.isEmpty()) {
			for(DPI dpi : dpiDisponibiliOperatore) {
				if(dpi.getSettoriDPI() != null && !dpi.getSettoriDPI().isEmpty()) {
					for(SettoreDPI settoreDPI : dpi.getSettoriDPI()) {
						if(settoreDPI.getSettore().getId().equals(idSettore)) {
							dpiDisponibili.add(dpi);
							break;
						}
					}
				}
			}
		}
		return dpiDisponibili;
	}
	
}
