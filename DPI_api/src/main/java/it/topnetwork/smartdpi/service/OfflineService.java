package it.topnetwork.smartdpi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.OperatoreRepository;
import it.topnetwork.smartdpi.dto.request.allarme.ChiusuraAllarmeDPIRequest;
import it.topnetwork.smartdpi.dto.request.beacon.StatoBeaconRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeDPISyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeUomoATerraSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.AzioneOperatoreSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.ChiusuraAllarmeDPISyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.InterventoSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.OfflineSyncRequest;
import it.topnetwork.smartdpi.entity.Allarme;
import it.topnetwork.smartdpi.entity.Intervento;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.service.util.OfflineIdAppMapper;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;
import it.topnetwork.smartdpi.utility.constants.TipoAzioneOperatoreConstants;

@Service
//@Transactional(readOnly = true)
public class OfflineService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OperatoreRepository operatoreRepository;

	@Autowired
	private AllarmeService allarmeService;

	@Autowired
	private InterventoService interventoService;

	@Autowired
	private BeaconService beaconService;

	@Autowired
	private AzioneOperatoreService azioneOperatoreService;

	/**
	 * sincronizzazione dati app 
	 * @param dto
	 * @param idOperatore
	 * @throws ApplicationException
	 */
	//	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void sync(OfflineSyncRequest dto, Long idOperatore) throws ApplicationException {
		try {
			if(dto != null) {
				log.info(dto.toString());
				// recupera operatore
				Operatore operatore = this.operatoreRepository.findValidById(idOperatore);
				if(operatore == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore inesistente");
				}
				OfflineIdAppMapper idMapper =  new OfflineIdAppMapper();
				this.syncInterventi(dto.getInterventi(), operatore, idMapper);
				this.syncAllarmiDPI(dto.getAllarmiDPI(), operatore, idMapper);
				this.syncAllarmiCadute(dto.getAllarmiCadute(), operatore, idMapper);
				this.syncStatiBeacon(dto.getStatiBeacon(), operatore, idMapper);
				this.syncAzioniOperatore(dto.getAzioniOperatore(), operatore, idMapper);
				this.syncAllarmiDPIRisolti(dto.getAllarmiDPIRisolti(), operatore, idMapper);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}

	/**
	 * sync interventi
	 * @param interventi
	 * @param operatore
	 * @param idMapper
	 * @return
	 */
	private void syncInterventi(List<InterventoSyncRequest> interventi, Operatore operatore, OfflineIdAppMapper idMapper) {
		if(interventi != null && !interventi.isEmpty()) {
			// mapping id app - intervento DB
			Map<Long, Intervento> appId2interventi = idMapper.getAppId2interventi();
			// itera interventi
			for(InterventoSyncRequest interventoSR : interventi) {
				try {
					if(interventoSR.isValid()) {
						// salva intervento
						Intervento intervento = this.interventoService.sync(interventoSR, operatore);
						// put intervento in mappa
						appId2interventi.put(interventoSR.getIdAppIntervento(), intervento);
					} else {
						throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
					}
				} catch(ApplicationException ae) {
					log.error("{} for [{}]", ae.getMessage(), interventoSR.toString());
				}
				catch(Exception e) {
					log.error("{} for [{}]", e.getMessage(), interventoSR.toString());
				}
			}
		}
	}

	/**
	 * sync allarmi DPI non indossati
	 * @param allarmiDPI
	 * @param operatore
	 * @param idMapper
	 * @return
	 */
	private void syncAllarmiDPI(List<AllarmeDPISyncRequest> allarmiDPI, Operatore operatore, OfflineIdAppMapper idMapper) {
		if(allarmiDPI != null && !allarmiDPI.isEmpty()) {
			// mapping id app - allarme DB
			Map<Long, Allarme> appId2allarmi = idMapper.getAppId2allarmi();
			// itera allarmiDPI
			for(AllarmeDPISyncRequest allarmeDPISyncRequest : allarmiDPI) {
				try {
					if(allarmeDPISyncRequest.isValid()) {
						// salva allarme
						Allarme allarmeDPI = this.allarmeService.sync(allarmeDPISyncRequest, operatore, idMapper);
						// put allarme in mappa
						appId2allarmi.put(allarmeDPISyncRequest.getIdAppAllarme(), allarmeDPI);
					} else {
						throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
					}
				} catch(ApplicationException ae) {
					log.error("{} for [{}]", ae.getMessage(), allarmeDPISyncRequest.toString());
				}
				catch(Exception e) {
					log.error("{} for [{}]", e.getMessage(), allarmeDPISyncRequest.toString());
				}
			}
		}
	}

	/**
	 * sync allarmi uomo a terra
	 * @param allarmiCadute
	 * @param operatore
	 * @param idMapper
	 * @return
	 */
	private void syncAllarmiCadute(List<AllarmeUomoATerraSyncRequest> allarmiCadute, Operatore operatore, OfflineIdAppMapper idMapper) {
		if(allarmiCadute != null && !allarmiCadute.isEmpty()) {
			// mapping id app - allarme DB
			Map<Long, Allarme> appId2allarmi = idMapper.getAppId2allarmi();
			// itera allarmiCadute
			for(AllarmeUomoATerraSyncRequest allarmeCaduta : allarmiCadute) {
				try {
					if(allarmeCaduta.isValid()) {
						// salva allarme
						Allarme allarmeDPI = this.allarmeService.sync(allarmeCaduta, operatore, idMapper);
						// put allarme in mappa
						appId2allarmi.put(allarmeCaduta.getIdAppAllarme(), allarmeDPI);
					} else {
						throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
					}
				} catch(ApplicationException ae) {
					log.error("{} for [{}]", ae.getMessage(), allarmeCaduta.toString());
				}
				catch(Exception e) {
					log.error("{} for [{}]", e.getMessage(), allarmeCaduta.toString());
				}
			}
		}
	}

	/**
	 * sync stati beacon
	 * @param statiBeacon
	 * @param operatore
	 * @param idMapper
	 * @return
	 */
	private void syncStatiBeacon(List<StatoBeaconRequest> statiBeacon, Operatore operatore, OfflineIdAppMapper idMapper) {
		if(statiBeacon != null && !statiBeacon.isEmpty()) {
			try {
//				// costruisco array
//				StatoBeaconRequest[] statiBeaconArr = new StatoBeaconRequest[statiBeacon.size()];
//				statiBeacon.toArray(statiBeaconArr);
				// salvataggio stati beacon
				this.beaconService.save(statiBeacon, operatore.getId());
			} catch(ApplicationException ae) {
				log.error(ae.getMessage());
			}
			catch(Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * sync azioni operatore
	 * @param azioniOperatore
	 * @param operatore
	 * @param idMapper
	 * @return
	 */
	private void syncAzioniOperatore(List<AzioneOperatoreSyncRequest> azioniOperatore, Operatore operatore, OfflineIdAppMapper idMapper) {
		if(azioniOperatore != null && !azioniOperatore.isEmpty()) {
			// itera azioniOperatore
			for(AzioneOperatoreSyncRequest azioneOperatore : azioniOperatore) {
				try {
					if(azioneOperatore.isValid()) {
						syncAzioneOperatore(operatore, idMapper, azioneOperatore);
					} else {
						throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
					}
				} catch(ApplicationException ae) {
					log.error("{} for [{}]", ae.getMessage(), azioneOperatore.toString());
				}
				catch(Exception e) {
					log.error("{} for [{}]", e.getMessage(), azioneOperatore.toString());
				}
			}
		}
	}

	/**
	 * sync azione operatore
	 * @param operatore
	 * @param idMapper
	 * @param azioneOperatore
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	private void syncAzioneOperatore(Operatore operatore, OfflineIdAppMapper idMapper, AzioneOperatoreSyncRequest azioneOperatore) throws ApplicationException {
		// switch azioni operatore
		// il login è sempre richiamato online, quindi non serve sync
		// nuovo allarme e sblocco allarme invece gestiti da allarme service
		if(azioneOperatore.getIdTipoAzione() == TipoAzioneOperatoreConstants.INIZIO_ATTIVITA) {
			Intervento interventoAzione = getIntervento(idMapper, azioneOperatore);
			// log azione inizio attivita
			this.azioneOperatoreService.inizioAttivita(operatore.getId(), interventoAzione.getId(), azioneOperatore.getDataAzione());
		} else if( azioneOperatore.getIdTipoAzione() == TipoAzioneOperatoreConstants.FINE_ATTIVITA) {
			Intervento interventoAzione = getIntervento(idMapper, azioneOperatore);
			// log azione fine attivita
			this.azioneOperatoreService.fineAttivita(operatore.getId(), interventoAzione.getId(), azioneOperatore.getDataAzione());
		} else if (azioneOperatore.getIdTipoAzione() == TipoAzioneOperatoreConstants.LOGOUT) {
			// log azione logout
			this.azioneOperatoreService.logout(operatore.getId(), azioneOperatore.getDataAzione());
		}
	}
	
	/**
	 * sync allarmi DPI risolti
	 * @param allarmiDPIRisolti
	 * @param operatore
	 * @param idMapper
	 * @throws ApplicationException
	 */
//	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	private void syncAllarmiDPIRisolti(List<ChiusuraAllarmeDPISyncRequest> allarmiDPIRisolti, Operatore operatore, OfflineIdAppMapper idMapper) throws ApplicationException {
		if(allarmiDPIRisolti != null && !allarmiDPIRisolti.isEmpty()) {
			// itera allarmiDPIRisolti
			for(ChiusuraAllarmeDPISyncRequest allarmeDPIRisoltiSyncRequest : allarmiDPIRisolti) {
				try {
					// verifica intervento
					long idIntervento = 0;
					// verifica id intervento da app
					if(allarmeDPIRisoltiSyncRequest.getIdIntervento() == 0) {
						// recupero id intervento da mapper
						idIntervento = idMapper.getIntervento(allarmeDPIRisoltiSyncRequest.getIdAppIntervento()).getId();
					} else {
						// intervento da DB
						idIntervento = allarmeDPIRisoltiSyncRequest.getIdIntervento();
					}

					// creo oggetto ChiusuraAllarmeDPIRequest da passare al service per chiusura allarme
					ChiusuraAllarmeDPIRequest chiusuraAllarmeDPIRequest = new ChiusuraAllarmeDPIRequest();
					chiusuraAllarmeDPIRequest.setIdDPI(allarmeDPIRisoltiSyncRequest.getIdDPI());
					chiusuraAllarmeDPIRequest.setIdIntervento(idIntervento);
					chiusuraAllarmeDPIRequest.setIdKit(allarmeDPIRisoltiSyncRequest.getIdKit());
					
					// chiusura allarme DPI non indossato
					this.allarmeService.chiusuraAllarmeDPINonIndossato(chiusuraAllarmeDPIRequest, operatore.getId());
				} catch(ApplicationException ae) {
					log.error("{} for [{}]", ae.getMessage(), allarmeDPIRisoltiSyncRequest.toString());
				}
				catch(Exception e) {
					log.error("{} for [{}]", e.getMessage(), allarmeDPIRisoltiSyncRequest.toString());
				}
			}
		}
	}

	/**
	 * get intervento da DB o da mapper
	 * @param idMapper
	 * @param azioneOperatore
	 * @return
	 * @throws ApplicationException
	 */
	private Intervento getIntervento(OfflineIdAppMapper idMapper, AzioneOperatoreSyncRequest azioneOperatore)
			throws ApplicationException {
		Intervento interventoAzione = null;
		// verifica id intervento da app
		if(azioneOperatore.getIdIntervento() == 0) {
			// recupero intervento da mapper
			interventoAzione = idMapper.getIntervento(azioneOperatore.getIdAppIntervento());
		} else {
			// intervento da DB
			interventoAzione = this.interventoService.getIntervento(azioneOperatore.getIdIntervento());
		}
		return interventoAzione;
	}

}
