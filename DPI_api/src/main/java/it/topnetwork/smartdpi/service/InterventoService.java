package it.topnetwork.smartdpi.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.InterventoRepository;
import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.OperatoreRepository;
import it.topnetwork.smartdpi.dao.SedeCommessaRepository;
import it.topnetwork.smartdpi.dto.request.intervento.InizioInterventoRequest;
import it.topnetwork.smartdpi.dto.request.offline.InterventoSyncRequest;
import it.topnetwork.smartdpi.entity.Intervento;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.SedeCommessa;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class InterventoService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private InterventoRepository interventoRepository;
	
	@Autowired
	private OperatoreRepository operatoreRepository;
	
	@Autowired
	private SedeCommessaRepository sedeCommessaRepository;
	
	@Autowired
	private KitRepository kitRepository;
	
	@Autowired
	private AzioneOperatoreService azioneOperatoreService;

	/**
	 * inizio intervento
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Intervento inizio(InizioInterventoRequest dto, Long idOperatore) throws ApplicationException {
		Intervento intervento = null;
		try {
			// verifica intervento
			intervento = this.verificaIntervento(dto, idOperatore);
			// salva intervento
			intervento = this.interventoRepository.save(intervento);
			// salva azione operatore (inizio intervento)
			this.azioneOperatoreService.inizioAttivita(idOperatore, intervento.getId(), new Date());
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return intervento;
	}

	/**
	 * fine intervento
	 * @param id
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Intervento fine(Long id, Long idOperatore) throws ApplicationException {
		Intervento intervento = null;
		try {
			// recupera intervento tramite id
			intervento = getIntervento(id);
			// imposta fine intervento
			Date dataFineIntervento = new Date();
			intervento.setDataFine(dataFineIntervento);
			intervento.setDataUltimaModifica(dataFineIntervento);
			// salva intervento
			intervento = this.interventoRepository.save(intervento);
			// salva azione operatore (fine intervento)
			this.azioneOperatoreService.fineAttivita(idOperatore, intervento.getId(), new Date());
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return intervento;
	}

	/**
	 * get intervento
	 * @param id
	 * @return
	 * @throws ApplicationException
	 */
	public Intervento getIntervento(Long id) throws ApplicationException {
		Intervento intervento;
		intervento = this.interventoRepository.findValidById(id);
		if(intervento == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "intervento specificato inesistente");
		}
		return intervento;
	}
	
	/**
	 * sync intervento
	 * @param dto
	 * @param operatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Intervento sync(InterventoSyncRequest dto, Operatore operatore) throws ApplicationException {
		Intervento intervento = null;
		try {
			// verifica intervento gia esistente (idIntervento != 0)
			if(dto.getIdIntervento() != 0) {
				// recupera intervento tramite id
				intervento = this.interventoRepository.findValidById(dto.getIdIntervento());
				if(intervento == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "intervento specificato inesistente");
				}
			} else {
				// nuovo intervento
				intervento = new Intervento();
			}
			// verifica sede commessa
			SedeCommessa sedeCommessaIntervento = verificaSedeCommessa(dto.getIdSedeCommessa());
			// verifica kit
			Kit kitIntervento = verificaKit(dto.getIdKit(), operatore);
			// data inserimento/ultima modifica
			Date dataInserimento = new Date();
			// set dati intervento
			setDatiIntervento(intervento, operatore, sedeCommessaIntervento, kitIntervento, dto.getDataInizio(), dto.getDataFine(), 
					dto.getLatitudine(), dto.getLongitudine(), dataInserimento); 
			
			// save intervento
			intervento = this.interventoRepository.save(intervento);
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return intervento;
	}

	/**
	 * crea entity intervento
	 * @param operatore
	 * @param intervento
	 * @param sedeCommessaIntervento
	 * @param kitIntervento
	 * @param dataInizio
	 * @param dataFine
	 * @param latitudine
	 * @param longitudine
	 * @param dataInserimento
	 */
	private void setDatiIntervento(Intervento intervento, Operatore operatore, SedeCommessa sedeCommessaIntervento, Kit kitIntervento, 
				Date dataInizio, Date dataFine, String latitudine, String longitudine, Date dataInserimento) {
		intervento.setOperatore(operatore);
		intervento.setSedeCommessa(sedeCommessaIntervento);
		intervento.setKit(kitIntervento);
		intervento.setDataInizio(dataInizio);
		intervento.setDataFine(dataFine);
		intervento.setLatitudine(latitudine);
		intervento.setLongitudine(longitudine);
		intervento.setDataInserimento(dataInserimento);
		intervento.setDataUltimaModifica(dataInserimento);
	}

	/**
	 * verifica intervento
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	private Intervento verificaIntervento(InizioInterventoRequest dto, Long idOperatore) throws ApplicationException {
		Intervento intervento = new Intervento();
		if(dto != null && dto.isValid()) {
			// verifica operatore
			Operatore operatoreIntervento = verificaOperatore(idOperatore);
			// verifica sede commessa
			SedeCommessa sedeCommessaIntervento = verificaSedeCommessa(dto.getIdSedeCommessa());
			// verifica kit
			Kit kitIntervento = verificaKit(dto.getIdKit(), operatoreIntervento);
			// data inizio intervento
			Date dataInizioIntervento = new Date();
			// set campi
			this.setDatiIntervento(intervento, operatoreIntervento, sedeCommessaIntervento, kitIntervento, dataInizioIntervento, null, 
					dto.getLatitudine(), dto.getLongitudine(), dataInizioIntervento);
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return intervento;
	}

	/**
	 * verifica operatore
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	private Operatore verificaOperatore(Long idOperatore) throws ApplicationException {
		Operatore operatoreIntervento = this.operatoreRepository.findValidById(idOperatore);
		if(operatoreIntervento == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore specificato inesistente");
		}
		return operatoreIntervento;
	}
	
	/**
	 * verifica sede commessa
	 * @param idSedeCommessa
	 * @return
	 * @throws ApplicationException
	 */
	private SedeCommessa verificaSedeCommessa(long idSedeCommessa) throws ApplicationException {
		SedeCommessa sedeCommessaIntervento = this.sedeCommessaRepository.findValidById(idSedeCommessa);
		if(sedeCommessaIntervento == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "sede commessa specificata inesistente");
		}
		return sedeCommessaIntervento;
	}
	
	/**
	 * verifica kit
	 * @param idKit
	 * @param operatoreIntervento
	 * @return
	 * @throws ApplicationException
	 */
	private Kit verificaKit(long idKit, Operatore operatoreIntervento) throws ApplicationException {
		Kit kitIntervento = this.kitRepository.findValidById(idKit);
		if(kitIntervento == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "kit specificato inesistente");
		}
		// verifica kit operatore
		if(kitIntervento.getOperatore() == null || !kitIntervento.getOperatore().getId().equals(operatoreIntervento.getId())) {
			throw new ApplicationException(ErrorCode.KIT_OPERATORE_MISMATCH, "kit non associato all'operatore");
		}
		return kitIntervento;
	}
	
}
