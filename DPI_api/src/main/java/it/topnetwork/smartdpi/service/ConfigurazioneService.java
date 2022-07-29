package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.ConfigurazioneRepository;
import it.topnetwork.smartdpi.dto.request.config.ConfigurazioneRequest;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class ConfigurazioneService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigurazioneRepository configurazioneRepository;
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * recupera configurazioni
	 * @return
	 * @throws ApplicationException
	 */
	public List<Configurazione> getConfigs() throws ApplicationException {
		List<Configurazione> configs = null;
		try {
			configs = this.configurazioneRepository.findAll();
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return configs;
	}
	
	/**
	 * recupera configurazione
	 * @param nome
	 * @return
	 * @throws ApplicationException
	 */
	public Configurazione getConfig(String nome) throws ApplicationException {
		Configurazione config = null;
		try {
			List<Configurazione> configs = this.configurazioneRepository.findAllLogin();
			if(configs != null && !configs.isEmpty()) {
				for(Configurazione item : configs) {
					if(item.getNome().equalsIgnoreCase(nome)) {
						config = item;
						break;
					}
				}
			}
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return config;
	}
	
	/**
	 * recupera configs login
	 * @return
	 */
	public List<Configurazione> getLoginConfigs() {
		List<Configurazione> loginConfigs = new ArrayList<>();
		List<Configurazione> configs = this.configurazioneRepository.findAllLogin();
		if(configs != null && !configs.isEmpty()) {
			for(Configurazione item : configs) {
				if(item.isLoginApp()) {
					loginConfigs.add(item);
				}
			}
		}
		return loginConfigs;
	}
	
	/**
	 * inserimento/ modifica configurazione
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Configurazione save(ConfigurazioneRequest dto, Long idUtente) throws ApplicationException {
		Configurazione configurazione = null;
		try {
			// verifica dto
			if(dto != null && dto.isValid()) {
				// controllo configurazione già presente
				configurazione = this.configurazioneRepository.findByNome(dto.getNome());
				if(configurazione == null) {
					// nuova configurazione. Creo entita
					configurazione = new Configurazione();
					configurazione.setDatiInserimento(idUtente);
				} else {
					// configurazione esistente. Aggiorno entita
					configurazione.setDatiUltimaModifica(idUtente);
				}
				// aggiorno campi
				configurazione.setNome(dto.getNome());
				configurazione.setValore(dto.getValore());
				configurazione.setLoginApp(dto.isLoginApp());
				// salvataggio
				configurazione = this.configurazioneRepository.save(configurazione);
				// clear cache configurazioni
				this.cacheService.clearCache(CacheEntry.LISTA_CONFIG);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException ae) {
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return configurazione;
	}
	
}
