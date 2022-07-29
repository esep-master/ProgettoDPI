package it.topnetwork.smartdpi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.AzioneOperatoreRepository;
import it.topnetwork.smartdpi.entity.AzioneOperatore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;
import it.topnetwork.smartdpi.utility.constants.TipoAzioneOperatoreConstants;

@Service
@Transactional(readOnly = true)
public class AzioneOperatoreService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AzioneOperatoreRepository azioneOperatoreRepository;

	/**
	 * recupera azioni operatore
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	public List<AzioneOperatore> getAzioniOperatore(Long idOperatore) throws ApplicationException {
		List<AzioneOperatore> azioniOperatore = null;
		try {
			azioniOperatore = this.azioneOperatoreRepository.findPerOperatore(idOperatore);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return azioniOperatore;
	}
	
	/**
	 * azione operatore: login
	 * @param idOperatore
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void login(Long idOperatore, Date dataAzione) {
		try {
			log.info("LOGIN operatore [{}]", idOperatore);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.LOGIN, 0, 0, 0, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * azione operatore: inizio attivita
	 * @param idOperatore
	 * @param idIntervento
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void inizioAttivita(Long idOperatore, Long idIntervento, Date dataAzione) {
		try {
			log.info("INIZIO_ATTIVITA operatore [{}], intervento [{}]", idOperatore, idIntervento);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.INIZIO_ATTIVITA, idIntervento, 0, 0, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * azione operatore: fine attivita
	 * @param idOperatore
	 * @param idIntervento
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void fineAttivita(Long idOperatore, Long idIntervento, Date dataAzione) {
		try {
			log.info("FINE_ATTIVITA operatore [{}], intervento [{}]", idOperatore, idIntervento);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.FINE_ATTIVITA, idIntervento, 0, 0, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * azione operatore: nuovo allarme
	 * @param idOperatore
	 * @param idIntervento
	 * @param idDPI
	 * @param idTipoAllarme
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void nuovoAllarme(Long idOperatore, Long idIntervento, Long idDPI, Long idTipoAllarme, Date dataAzione) {
		try {
			log.info("NUOVO_ALLARME operatore [{}], intervento [{}], DPI [{}]", idOperatore, idIntervento, idDPI);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.NUOVO_ALLARME, idIntervento, idDPI, idTipoAllarme, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * azione operatore: sblocco allarme
	 * @param idOperatore
	 * @param idIntervento
	 * @param idDPI
	 * @param idTipoAllarme
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void sbloccoAllarme(Long idOperatore, Long idIntervento, Long idDPI, Long idTipoAllarme, Date dataAzione) {
		try {
			log.info("SBLOCCO_ALLARME operatore [{}], intervento [{}], DPI [{}]", idOperatore, idIntervento, idDPI);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.SBLOCCO_ALLARME, idIntervento, idDPI, idTipoAllarme, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * azione operatore: logout
	 * @param idOperatore
	 * @param dataAzione
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void logout(Long idOperatore, Date dataAzione) {
		try {
			log.info("LOGOUT operatore [{}]", idOperatore);
			Map<String, Object> output = this.azioneOperatoreRepository.logAzioneOperatore(idOperatore, TipoAzioneOperatoreConstants.LOGOUT, 0, 0, 0, dataAzione);
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
}
