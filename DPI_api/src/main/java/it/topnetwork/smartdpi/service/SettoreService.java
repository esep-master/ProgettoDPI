package it.topnetwork.smartdpi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.CommessaRepository;
import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.SettoreRepository;
import it.topnetwork.smartdpi.dto.request.settore.InsertSettoreRequest;
import it.topnetwork.smartdpi.dto.request.settore.UpdateSettoreRequest;
import it.topnetwork.smartdpi.entity.Commessa;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class SettoreService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SettoreRepository settoreRepository;
	
	@Autowired
	private CommessaRepository commessaRepository;
	
	@Autowired
	private KitRepository kitRepository;
	
	@Autowired
	private CacheService cacheService;

	/**
	 * recupera lista settori
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Settore> getSettori() throws ApplicationException {
		List<Settore> settori = null;
		try {
			settori = this.settoreRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return settori;
	}

	/**
	 * inserimento nuovo settore
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Settore insert(InsertSettoreRequest dto, Long idUtente) throws ApplicationException {
		Settore settore = null;
		try {
			// crea entita settore
			settore = this.creaSettore(dto, idUtente);
			// salva entita
			settore = this.settoreRepository.save(settore);
			// crea associazioni tipi dpi - settore
			this.settoreRepository.insertTipiDPISettore(settore.getId(), idUtente);
			// clear cache settori
			this.cacheService.clearCache(CacheEntry.LISTA_SETTORI);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return settore;
	}

	/**
	 * aggiornamento settore
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Settore update(UpdateSettoreRequest dto, Long idUtente) throws ApplicationException {
		Settore settore = null;
		try {
			// prepare entita settore da modificare
			settore = this.prepareSettoreForUpdate(dto, idUtente);
			// salva entita
			settore = this.settoreRepository.save(settore);
			// clear cache settori
			this.cacheService.clearCache(CacheEntry.LISTA_SETTORI);
		}
		catch(ApplicationException ae) {
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return settore;
	}

	/**
	 * cancellazione logica settore
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// è possibile cancellare un settore se e solo se non ci sono commesse e kit associati
			List<Commessa> commesseSettore = this.commessaRepository.findBySettore(id);
			if(commesseSettore != null &&commesseSettore.size() > 0) {
				throw new ApplicationException(ErrorCode.SETTORE_COMMESSA_ASSOCIATED, "non è possibile eliminare il settore perché ci sono alcune commesse associate");
			}
			List<Kit> kitSettore = this.kitRepository.findBySettore(id);
			if(kitSettore != null && kitSettore.size() > 0) {
				throw new ApplicationException(ErrorCode.SETTORE_KIT_ASSOCIATED, "non è possibile eliminare il settore perché ci sono alcuni kit associati");
			}
			// richiama procedure per cancellazione logica settore e entita associate
			Map<String, Object> output = this.settoreRepository.eliminaSettore(id, idUtente);
			// valida output
			Utility.validateStoredProcedureOutput(output);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}

	/**
	 * crea entita settore
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private Settore creaSettore(InsertSettoreRequest dto, Long idUtente) throws ApplicationException {
		Settore settore = new Settore();
		if(dto != null && dto.isValid()) {
			// cerca settore già esistente con stesso nome
			Settore checkSettore = this.settoreRepository.findByNome(dto.getNome());
			if(checkSettore != null) {
				throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "nome settore gia esistente");
			}
			// set campi
			settore.setNome(dto.getNome());
			settore.setDatiInserimento(idUtente);
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return settore;
	}

	/**
	 * prepara settore per modifica
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private Settore prepareSettoreForUpdate(UpdateSettoreRequest dto, Long idUtente) throws ApplicationException {
		Settore settore = null;
		if(dto != null && dto.isValid()) {
			// cerca settore tramite id
			settore = this.settoreRepository.findValidById(dto.getIdSettore());
			if(settore == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "settore specificato inesistente");
			}
			// cerca username già esistente per un altro utente se username modificato
			if(Utility.isValid(dto.getNome()) && !dto.getNome().equalsIgnoreCase(settore.getNome())) {
				Settore checkSettore = this.settoreRepository.findByNome(dto.getNome());
				if(checkSettore != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "nome settore gia esistente");
				}
				settore.setNome(dto.getNome());
			}
			settore.setDatiUltimaModifica(idUtente);
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return settore;
	}

}
