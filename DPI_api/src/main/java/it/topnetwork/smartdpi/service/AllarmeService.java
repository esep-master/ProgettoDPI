package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.AllarmeRepository;
import it.topnetwork.smartdpi.dao.DPIKitRepository;
import it.topnetwork.smartdpi.dao.DPIRepository;
import it.topnetwork.smartdpi.dao.InterventoRepository;
import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.StatoAllarmeRepository;
import it.topnetwork.smartdpi.dao.TipoAllarmeRepository;
import it.topnetwork.smartdpi.dao.UtenteRepository;
import it.topnetwork.smartdpi.dto.request.allarme.AllarmeDPIRequest;
import it.topnetwork.smartdpi.dto.request.allarme.AllarmeUomoATerraRequest;
import it.topnetwork.smartdpi.dto.request.allarme.ChiusuraAllarmeDPIRequest;
import it.topnetwork.smartdpi.dto.request.allarme.LavorazioneAllarmeRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeDPISyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeUomoATerraSyncRequest;
import it.topnetwork.smartdpi.entity.Allarme;
import it.topnetwork.smartdpi.entity.Beacon;
import it.topnetwork.smartdpi.entity.DPI;
import it.topnetwork.smartdpi.entity.DPIKit;
import it.topnetwork.smartdpi.entity.Intervento;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.StatoAllarme;
import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.entity.Utente;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.service.util.OfflineIdAppMapper;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;
import it.topnetwork.smartdpi.utility.constants.TipoAllarmeConstants;

@Service
@Transactional(readOnly = true)
public class AllarmeService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AllarmeRepository allarmeRepository;

	@Autowired
	private DPIRepository dpiRepository;

	@Autowired
	private InterventoRepository interventoRepository;

	@Autowired
	private TipoAllarmeRepository tipoAllarmeRepository;

	@Autowired
	private StatoAllarmeRepository statoAllarmeRepository;

	@Autowired
	private KitRepository kitRepository;

	@Autowired
	private DPIKitRepository dpiKitRepository;

	@Autowired
	private UtenteRepository utenteRepository;

	@Autowired
	private AzioneOperatoreService azioneOperatoreService;


	/**
	 * recupera allarmi visibili all'utente
	 * @param dataAllarmeDaMS
	 * @param idUtente
	 * @param isStorico
	 * @return
	 * @throws ApplicationException
	 */
	public List<Allarme> getAllarmi(Long dataAllarmeDaMS, Long idUtente, boolean isStorico) throws ApplicationException {
		List<Allarme> allarmi = null;
		try {
			if(dataAllarmeDaMS == null || dataAllarmeDaMS == 0) {
				if(isStorico) {
					// recupera tutti allarmi (storico)
					allarmi = this.allarmeRepository.findAllByUtente(idUtente);
				} else {
					// recupera allarmi non chiusi
					allarmi = this.allarmeRepository.findByUtente(idUtente);
				}
			} else {
				Date dataAllarmeDa = new Date(dataAllarmeDaMS);
				if(isStorico) {
					// recupera allarmi partendo da dataAllarmeDa (storico)
					allarmi = this.allarmeRepository.findAllByUtente(idUtente, dataAllarmeDa);
				} else {
					// recupera allarmi non chiusi da dataAllarmeDa
					allarmi = this.allarmeRepository.findByUtente(idUtente, dataAllarmeDa);
				}
			}

		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return allarmi;
	}

	/**
	 * recupera allarmi operatore per la giornata odierna
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	public List<Allarme> getAllarmiOperatore(Long idOperatore) throws ApplicationException {
		List<Allarme> allarmi = null;
		try {
			// costruisco data oggi (alle 00:00:00)
			Date oggi = Utility.getOggi();

			allarmi = this.allarmeRepository.findAllByOperatore(idOperatore, oggi);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return allarmi;
	}

	/**
	 * salva nuovo allarme per DPI non indossato
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme insertDPINonIndossato(AllarmeDPIRequest dto, Long idOperatore) throws ApplicationException {
		Allarme allarme = null;
		try {
			// verifa se esiste già un allarme aperto per questo dpi in questo intervento. in questo caso non generare allarmi ma salvo log
			Allarme checkAllarme = this.allarmeRepository.find(dto.getIdDPI(), dto.getIdIntervento());
			if(checkAllarme == null) {
				// verifica e crea allarme
				allarme = this.creaAllarmeDPINonIndossato(dto, idOperatore);
				// logga azione e salva allarme 
				allarme = salvaAllarme(idOperatore, allarme, allarme.getDataAllarme(), true, true);
			} else {
				allarme = salvaAllarme(idOperatore, checkAllarme, new Date(), true, false);
			}
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * salva nuovo allarme per DPI non indossato
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme insertUomoATerra(AllarmeUomoATerraRequest dto, Long idOperatore) throws ApplicationException {
		Allarme allarme = null;
		try {
			// verifica e crea allarme
			allarme = this.creaAllarmeUomoATerra(dto, idOperatore);
			// salva allarme
			allarme = salvaAllarme(idOperatore, allarme, new Date(), true, true);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * crea nuovi allarmi per batteria beacon in scadenza (uno per ogni kit in cui è presente il dpi con questo beacon)
	 * @param beacon
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public List<Allarme> insertBatteriaBeaconInScadenza(Beacon beacon) throws ApplicationException {
		List<Allarme> allarmiGenerati = null;
		try {
			// recupera tipo allarme "batteria beacon in scadenza"
			TipoAllarme tipoAllarme = this.tipoAllarmeRepository.findValidById(TipoAllarmeConstants.BATTERIA_BEACON_IN_SCADENZA);
			if(tipoAllarme == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo allarme specificato inesistente");
			}
			// inserisci allarmi
			allarmiGenerati = this.creaAllarmeBeacon(beacon, tipoAllarme);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarmiGenerati;
	}

	/**
	 * crea nuovi allarmi per batteria beacon scarica (uno per ogni kit in cui è presente il dpi con questo beacon)
	 * @param beacon
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public List<Allarme> insertBatteriaBeaconScarica(Beacon beacon) throws ApplicationException {
		List<Allarme> allarmiGenerati = null;
		try {
			// recupera tipo allarme "batteria beacon in scadenza"
			TipoAllarme tipoAllarme = this.tipoAllarmeRepository.findValidById(TipoAllarmeConstants.BATTERIA_BEACON_SCARICA);
			if(tipoAllarme == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo allarme specificato inesistente");
			}
			// inserisci allarmi
			allarmiGenerati = this.creaAllarmeBeacon(beacon, tipoAllarme);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarmiGenerati;
	}

	/**
	 * crea allarmi per DPI in scadenza
	 * @param giorniScadenza
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public List<Allarme> insertDPIInScadenza(int giorniScadenza) throws ApplicationException {
		List<Allarme> allarmiGenerati = new ArrayList<>();
		try {
			// costruisco data scadenza
			Date dataScadenza = Utility.getDataScadenza(giorniScadenza);
			// recupero DPI in scadenza per cui non è stato generato o risolto ancora un alert per DPI in scadenza
			List<DPI> dpiInScadenza = this.dpiRepository.findInScadenza(dataScadenza, TipoAllarmeConstants.DPI_IN_SCADENZA);
			if(dpiInScadenza != null && !dpiInScadenza.isEmpty()) {
				for(DPI dpi : dpiInScadenza) {
					// creo allarme per ogni DPI in scadenza
					this.creaAllarmeDPIInScadenza(dpi);
				}
			}
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarmiGenerati;
	}

	/**
	 * presa in carico allarme da un utente 
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme lavorazioneAllarme(LavorazioneAllarmeRequest dto, Long idUtente) throws ApplicationException {
		Allarme allarme = null;
		try {
			if(dto != null && dto.isValid()) {
				// verifica allarme
				allarme = this.allarmeRepository.findValidById(dto.getIdAllarme());
				if(allarme == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "allarme specificato inesistente");
				}
				// verifico se allarme sbloccato da operatore
				if(allarme.getStatoAllarme().isSbloccoAutomaticoOperatore()) {
					throw new ApplicationException(ErrorCode.ALLARME_OPERATORE, "questo allarme non necessita di lavorazione. sbloccato da operatore");
				}
				// recupero utente presa in carico
				Utente utente = this.utenteRepository.findValidById(idUtente);
				if(utente == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "utente specificato inesistente");
				}
				// verifico se non è gia stato chiuso
				if(allarme.getDataRisoluzione() != null) {
					throw new ApplicationException(ErrorCode.ALLARME_CHIUSO, "allarme già chiuso");
				}
				// verifico se non è gia stato preso in carico
				if(allarme.getDataPresaInCarico() != null) {
					throw new ApplicationException(ErrorCode.ALLARME_LAVORATO, "allarme già in lavorazione");
				}
				// recupero stato allarme 'In lavorazione'
				StatoAllarme statoAllarmeLavorazione = this.statoAllarmeRepository.findStatoLavorazione();
				if(statoAllarmeLavorazione == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "stato allarme 'In lavorazione' inesistente");
				}

				// imposto stat,  utente e data presa in carico ed eventuali note
				allarme.setStatoAllarme(statoAllarmeLavorazione);
				allarme.setUtentePresaInCarico(utente);
				allarme.setDataPresaInCarico(new Date());
				if(Utility.isValid(dto.getNote(), true)) {
					allarme.setNote(dto.getNote());
				}
				allarme.setDatiUltimaModifica(idUtente);

				// salva allarme
				allarme = this.allarmeRepository.save(allarme);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * chiusura allarme da un utente
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme chiusuraAllarme(LavorazioneAllarmeRequest dto, Long idUtente) throws ApplicationException {
		Allarme allarme = null;
		try {
			if(dto != null && dto.isValid()) {
				// verifica allarme
				allarme = this.allarmeRepository.findValidById(dto.getIdAllarme());
				if(allarme == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "allarme specificato inesistente");
				}
				// verifico se allarme sbloccato da operatore
				if(allarme.getStatoAllarme().isSbloccoAutomaticoOperatore()) {
					throw new ApplicationException(ErrorCode.ALLARME_OPERATORE, "questo allarme non necessita di lavorazione. sbloccato da operatore");	
				}
				// recupero utente presa in carico
				Utente utente = this.utenteRepository.findValidById(idUtente);
				if(utente == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "utente specificato inesistente");
				}
				// verifico se non è stato ancora preso in carico
				if(allarme.getDataPresaInCarico() == null) {
					throw new ApplicationException(ErrorCode.ALLARME_DA_LAVORARE, "allarme ancora da lavorare");
				}
				// verifico se non è gia stato chiuso
				if(allarme.getDataRisoluzione() != null) {
					throw new ApplicationException(ErrorCode.ALLARME_CHIUSO, "allarme già chiuso");
				}
				// recupero stato allarme 'Chiuso'
				StatoAllarme statoAllarmeChiuso = this.statoAllarmeRepository.findStatoChiusura();
				if(statoAllarmeChiuso == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "stato allarme 'Chiuso' inesistente");
				}

				// imposto utente e data risoluzione ed eventuali note
				allarme.setStatoAllarme(statoAllarmeChiuso);
				allarme.setUtenteRisoluzione(utente);
				allarme.setDataRisoluzione(new Date());
				allarme.setFalsoAllarme(dto.isFalsoAllarme());
				if(Utility.isValid(dto.getNote(), true)) {
					allarme.setNote(dto.getNote());
				}
				allarme.setDatiUltimaModifica(idUtente);

				// salva allarme
				allarme = this.allarmeRepository.save(allarme);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * chiusura allarme risolto automaticamente dall'app
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme chiusuraAllarmeDPINonIndossato(ChiusuraAllarmeDPIRequest dto, Long idOperatore) throws ApplicationException {
		Allarme allarme = null;
		try {
			if(dto != null && dto.isValid()) {
				// recupera allarme tramite id intervento e id_dpi
				allarme = this.allarmeRepository.findAperto(dto.getIdDPI(), dto.getIdIntervento(), dto.getIdKit());

				if(allarme == null) {
					throw new ApplicationException(ErrorCode.ALLARME_CHIUSO, "nesssun allarme da chiudere per il kit, intervento e DPI");
				}

				// recupero stato allarme 'Chiuso'
				StatoAllarme statoAllarmeChiuso = this.statoAllarmeRepository.findStatoChiusura();
				if(statoAllarmeChiuso == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "stato allarme 'Chiuso' inesistente");
				}

				// imposta campi
				allarme.setStatoAllarme(statoAllarmeChiuso);
				allarme.setUtenteRisoluzione(null);
				allarme.setDataRisoluzione(new Date());
				allarme.setFalsoAllarme(false);
				allarme.setDatiUltimaModifica(null);

				// salva allarme
				allarme = this.allarmeRepository.save(allarme);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * sync allarme DPI non indossato
	 * @param dto
	 * @param operatore
	 * @param idMapper
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme sync(AllarmeDPISyncRequest dto, Operatore operatore, OfflineIdAppMapper idMapper) throws ApplicationException {
		Allarme allarme = null;
		try {
			// verifica DPI
			DPI dpiAllarme = verificaDPI(dto.getIdDPI());
			// verifica intervento
			Intervento interventoAllarme = null;
			// verifica id intervento da app
			if(dto.getIdIntervento() == 0) {
				// recupero intervento da mapper
				interventoAllarme = idMapper.getIntervento(dto.getIdAppIntervento());
			} else {
				// intervento da DB
				interventoAllarme = verificaIntervento(dto.getIdIntervento());
			}
			// verifica tipo allarme
			TipoAllarme tipoAllarme = verificaTipoAllarme(TipoAllarmeConstants.DPI_NON_INDOSSATO);		

			// verifa se esiste già un allarme aperto per questo dpi in questo intervento. in questo caso non generare allarmi ma salvo log
			Allarme checkAllarme = this.allarmeRepository.find(dto.getIdDPI(), dto.getIdIntervento());
			if(checkAllarme == null) {
				// verifica e crea allarme
				allarme = creaAllarme(tipoAllarme, dpiAllarme, interventoAllarme, interventoAllarme.getKit(), dto.getDataAllarme(), dto.getLatitudine(), dto.getLongitudine());
				// logga azione e salva allarme 
				allarme = salvaAllarme(operatore.getId(), allarme, allarme.getDataAllarme(), true, true);
			} else {
				allarme = salvaAllarme(operatore.getId(), checkAllarme, new Date(), true, false);
			}
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * sync allarme Uomo a terra
	 * @param dto
	 * @param operatore
	 * @param idMapper
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Allarme sync(AllarmeUomoATerraSyncRequest dto, Operatore operatore, OfflineIdAppMapper idMapper) throws ApplicationException {
		Allarme allarme = null;
		try {
			// verifica intervento
			Intervento interventoAllarme = null;
			// verifica id intervento da app
			if(dto.getIdIntervento() == 0) {
				// recupero intervento da mapper
				interventoAllarme = idMapper.getIntervento(dto.getIdAppIntervento());
			} else {
				// intervento da DB
				interventoAllarme = verificaIntervento(dto.getIdIntervento());
			}
			// verifica tipo allarme
			TipoAllarme tipoAllarme = verificaTipoAllarme(TipoAllarmeConstants.UOMO_A_TERRA);
			// crea allarme
			allarme = creaAllarme(tipoAllarme, null, interventoAllarme, interventoAllarme.getKit(), dto.getDataAllarme(), dto.getLatitudine(), dto.getLongitudine());
			// salva allarme
			allarme = salvaAllarme(operatore.getId(), allarme, allarme.getDataAllarme(), true, true);
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarme;
	}

	/**
	 * crea nuovo allarme per DPI non indossato
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	private Allarme creaAllarmeDPINonIndossato(AllarmeDPIRequest dto, Long idOperatore) throws ApplicationException {
		Allarme allarme = null;
		if(dto != null && dto.isValid()) {
			// verifica DPI
			DPI dpiAllarme = verificaDPI(dto.getIdDPI());
			// verifica intervento
			Intervento interventoAllarme = verificaIntervento(dto.getIdIntervento());
			// verifica tipo allarme
			TipoAllarme tipoAllarme = verificaTipoAllarme(TipoAllarmeConstants.DPI_NON_INDOSSATO);

			// crea allarme
			allarme = creaAllarme(tipoAllarme, dpiAllarme, interventoAllarme, interventoAllarme.getKit(), new Date(), dto.getLatitudine(), dto.getLongitudine());
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return allarme;
	}

	/**
	 * verifica DPI
	 * @param idDPI
	 * @return
	 * @throws ApplicationException
	 */
	private DPI verificaDPI(long idDPI) throws ApplicationException {
		DPI dpiAllarme = this.dpiRepository.findValidById(idDPI);
		if(dpiAllarme == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "DPI specificato inesistente");
		}
		return dpiAllarme;
	}

	/**
	 * verifica intervento
	 * @param idIntervento
	 * @return
	 * @throws ApplicationException
	 */
	private Intervento verificaIntervento(long idIntervento) throws ApplicationException {
		Intervento interventoAllarme = this.interventoRepository.findValidById(idIntervento);
		if(interventoAllarme == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "intervento specificato inesistente");
		}
		return interventoAllarme;
	}

	/**
	 * verifica tipo allarme
	 * @param idTipoAllarme
	 * @return
	 * @throws ApplicationException
	 */
	private TipoAllarme verificaTipoAllarme(long idTipoAllarme) throws ApplicationException {
		TipoAllarme tipoAllarme = this.tipoAllarmeRepository.findValidById(idTipoAllarme);
		if(tipoAllarme == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo allarme specificato inesistente");
		}
		return tipoAllarme;
	}

	/**
	 * crea nuovo allarme per uomo a terra
	 * @param dto
	 * @param idOperatore
	 * @return
	 * @throws ApplicationException
	 */
	private Allarme creaAllarmeUomoATerra(AllarmeUomoATerraRequest dto, Long idOperatore) throws ApplicationException {
		Allarme allarme = null;
		if(dto != null && dto.isValid()) {
			// verifica intervento
			Intervento interventoAllarme = this.interventoRepository.findValidById(dto.getIdIntervento());
			if(interventoAllarme == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "intervento specificato inesistente");
			}
			// verifica tipo allarme
			TipoAllarme tipoAllarme = this.tipoAllarmeRepository.findValidById(TipoAllarmeConstants.UOMO_A_TERRA);
			if(tipoAllarme == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo allarme specificato inesistente");
			}
			// crea allarme
			allarme = this.creaAllarme(tipoAllarme, null, interventoAllarme, interventoAllarme.getKit(), new Date(), dto.getLatitudine(), dto.getLongitudine());
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return allarme;
	}

	/**
	 * crea allarmi per beacon dpi (uno per ogni kit in cui è presente il dpi con questo beacon)
	 * @param beacon
	 * @param tipoAllarme
	 * @return
	 * @throws ApplicationException
	 */
	private List<Allarme> creaAllarmeBeacon(Beacon beacon, TipoAllarme tipoAllarme) throws ApplicationException {
		List<Allarme> allarmiGenerati = new ArrayList<>();
		// recupero il DPI con il particolare beacon
		DPI dpiBeacon = this.dpiRepository.findByBeacon(beacon.getId());
		if(dpiBeacon == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "beacon " + beacon.getId() + " non associato a nessun DPI") ;
		}
		// recupero tutti i kit in cui è presente il DPI
		List<Kit> kitDPI = this.kitRepository.findByDPI(dpiBeacon.getId());
		if(kitDPI != null && !kitDPI.isEmpty() ) {
			// genero un allarme per ogni kit
			for(Kit kit : kitDPI) {
				// creo e salvo allarme
				Allarme allarmeKit = this.creaAllarme(tipoAllarme, dpiBeacon, null, kit, new Date(), null, null);
				// salva allarme
				allarmeKit = salvaAllarme(0l, allarmeKit, allarmeKit.getDataAllarme(), false, true);
				// lista allarmi generati
				allarmiGenerati.add(allarmeKit);
			}
		}
		return allarmiGenerati;
	}

	/**
	 * crea nuovo allarme per DPI in scadenza
	 * @param dpi
	 * @return
	 * @throws ApplicationException
	 */
	private List<Allarme> creaAllarmeDPIInScadenza(DPI dpi) throws ApplicationException {
		List<Allarme> allarmiGenerati = new ArrayList<>();
		try {
			// recupero tutti i kit in cui è presente il DPI
			List<Kit> kitDPI = this.kitRepository.findByDPI(dpi.getId());
			if(kitDPI != null && !kitDPI.isEmpty() ) {
				// recupera tipo allarme "DPI in scadenza"
				TipoAllarme tipoAllarme = this.tipoAllarmeRepository.findValidById(TipoAllarmeConstants.DPI_IN_SCADENZA);
				if(tipoAllarme == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo allarme specificato inesistente");
				}
				// genero un allarme per ogni kit
				for(Kit kit : kitDPI) {
					// creo e salvo allarme
					Allarme allarmeKit = this.creaAllarme(tipoAllarme, dpi, null, kit, new Date(), null, null);
					// salva allarme
					allarmeKit = salvaAllarme(0l, allarmeKit, allarmeKit.getDataAllarme(), false, true);
					// lista allarmi generati
					allarmiGenerati.add(allarmeKit);
				}
			}
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return allarmiGenerati;
	}

	private Allarme creaAllarme(TipoAllarme tipoAllarme, DPI dpiAllarme, Intervento interventoAllarme, Kit kitAllarme,
			Date dataAllarme, String latitudine, String longitudine) throws ApplicationException {
		Allarme allarme;
		// data allarme
		//		Date dataAllarme = new Date();
		// recupero stato allarme iniziale
		StatoAllarme statoAllarme = this.findStatoIniziale(tipoAllarme, kitAllarme, dpiAllarme, dataAllarme);
		// set campi
		allarme = new Allarme();
		allarme.setDpi(dpiAllarme);
		allarme.setIntervento(interventoAllarme);
		allarme.setKit(kitAllarme);
		allarme.setTipoAllarme(tipoAllarme);
		allarme.setStatoAllarme(statoAllarme);
		allarme.setDataAllarme(dataAllarme);
		allarme.setLatitudine(latitudine);
		allarme.setLongitudine(longitudine);
		allarme.setDataInserimento(dataAllarme);
		allarme.setDataUltimaModifica(dataAllarme);
		// imposta data risoluzione se stato allarme è sblocco automatico
		if(statoAllarme.isSbloccoAutomaticoOperatore()) {
			allarme.setDataRisoluzione(dataAllarme);
		}
		return allarme;
	}

	/**
	 * salva allarme
	 * @param idOperatore
	 * @param allarme
	 * @param dataAzione
	 * @param logAzione
	 * @param saveAllarme
	 * @return
	 */
	private Allarme salvaAllarme(Long idOperatore, Allarme allarme, Date dataAzione, boolean logAzione, boolean saveAllarme) {
		if(logAzione) {
			// salva azione operatore (nuovo allarme)
			long idDPIAllarme = allarme.getDpi() != null ? allarme.getDpi().getId() : 0;
			this.azioneOperatoreService.nuovoAllarme(idOperatore, allarme.getIntervento().getId(), idDPIAllarme, allarme.getTipoAllarme().getId(), dataAzione);
			// se sbloccoo automatico salva azione operatore (sblocco allarme)
			if(allarme.getStatoAllarme().isSbloccoAutomaticoOperatore()) {
				this.azioneOperatoreService.sbloccoAllarme(idOperatore, allarme.getIntervento().getId(), allarme.getDpi().getId(), allarme.getTipoAllarme().getId(), dataAzione);
			}
		}
		// salva allarme
		if(saveAllarme) {
			allarme = this.allarmeRepository.save(allarme);
		}
		return allarme;
	}

	/**
	 * recupera stato iniziale per tipo allarme.
	 * se il dpi per il kit dell'operatore ha sblocco automatico impostato 'Sblocco Automatico', altrimenti imposto stato iniziale 'Aperto'
	 * @param tipoAllarme
	 * @param kit
	 * @param dpi
	 * @param dataAllarme
	 * @return
	 * @throws ApplicationException
	 */
	private StatoAllarme findStatoIniziale(TipoAllarme tipoAllarme, Kit kit, DPI dpi, Date dataAllarme) throws ApplicationException {
		StatoAllarme statoAllarme = null;
		boolean isRisoluzioneAutomatica = false;
		// verifica se puo esserci risoluzione automatica
		if(tipoAllarme.isBloccante() && dpi != null) {
			// recupero associazione DPI-Kit
			DPIKit dpiKit = this.dpiKitRepository.findByKitAndDPI(kit.getId(), dpi.getId());
			if(dpiKit == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "DPI non associato al kit per l'intervento");
			}
			// verifico se associazine DPI-Kit ha sblocco automatico e controllo se l'inizio sblocco automatico non è posteriore alla dataAllarme
			if(dpiKit.getSbloccoAllarmeDa() != null && !dpiKit.getSbloccoAllarmeDa().after(dataAllarme)) {
				// verifico se è stato impostata anche la data di fine sblocco e controllo se la fine sblocco automatico non è anteriore alla dataAllarme
				if(dpiKit.getSbloccoAllarmeA() == null || !dpiKit.getSbloccoAllarmeA().before(dataAllarme)) {
					// data fine validità non impostata oppure all'interno dell'intervallo di validità. sblocco allarme automatico
					isRisoluzioneAutomatica = true;
				}			
			}
		}
		// controllo quale stato iniziale impostare 
		if(isRisoluzioneAutomatica) {
			// blocco automaticoo per il DPI nel Kit, recupero stato sblocco automatico
			statoAllarme = this.statoAllarmeRepository.findStatoSbloccoAutomatico();
		} else {
			// nessuno sblocco automatico per il DPI nel kit, recupero stato allarme iniziale
			statoAllarme = this.statoAllarmeRepository.findStatoIniziale();
		}

		if(statoAllarme == null) {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "impossibile recuperare stato allarme");
		}
		return statoAllarme;
	}

}