package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.topnetwork.smartdpi.dao.CommessaRepository;
import it.topnetwork.smartdpi.dao.OperatoreSedeCommessaRepository;
import it.topnetwork.smartdpi.dto.request.commessa.CommessaRequest;
import it.topnetwork.smartdpi.entity.Commessa;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.OperatoreSedeCommessa;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.service.model.CRMData;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.Config;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class CommessaService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommessaRepository commessaRepository;
	
	@Autowired
	private OperatoreSedeCommessaRepository operatoreSedeCommessaRepository;
	
	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private Config config;

	/**
	 * sincronizza dati con CRM
	 * @param idUtenteOperazione
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void synchronizeCRMData(Long idUtenteOperazione) throws ApplicationException {
		try {
			Configurazione uriConfig = this.configurazioneService.getConfig(this.config.getCrmRestInfoURI());
			final String uri = uriConfig != null ? uriConfig.getValore() : "";
			// call rest api
			log.info("call rest api '{}' at [{}]", uri, Utility.dateFormat.format(new Date()));
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(uri, String.class);
			log.info("rest api response '{}'", result);
			ObjectMapper mapper = new ObjectMapper();
			// convert JSON array to List of objects
			List<CRMData> crmData = Arrays.asList(mapper.readValue(result, CRMData[].class));
			
			this.synchronizeData(crmData, idUtenteOperazione);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * recupera lista commesse
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Commessa> getCommesse(Long idUtente) throws ApplicationException {
		List<Commessa> commesse = null;
		try {
			commesse = this.commessaRepository.findAllValidi(idUtente);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return commesse;
	}
	
	
	/**
	 * recupera operatori commessa
	 * @param idCommessa
	 * @return
	 * @throws ApplicationException
	 */
	public List<Operatore> getOperatori(Long idCommessa) throws ApplicationException {
		List<Operatore> operatoriCommessa = new ArrayList<>();
		try {
			// recupero lista operatori sedi commessa
			List<OperatoreSedeCommessa> operatoriSediCommessa = this.operatoreSedeCommessaRepository.findByCommessa(idCommessa);
			if(operatoriSediCommessa != null && !operatoriSediCommessa.isEmpty()) {
				// itero operatoriSediCommessa e verifico validità (FIX query ritorna oggetta cancellati)
				for(OperatoreSedeCommessa osc : operatoriSediCommessa) {
					if(osc.getDataCancellazione().after(new Date())) {
						operatoriCommessa.add(osc.getOperatore());
					}
				}
			}
			if(operatoriCommessa != null && !operatoriCommessa.isEmpty()) {
				// rimozione entity associate agli operatori
				for(Operatore operatore : operatoriCommessa) {
					operatore.setOperatoreSediCommesse(null);
					operatore.setKit(null);
				}
			} 
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return operatoriCommessa;
	}
	
	/**
	 * inserimento/ modifica commessa
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Commessa save(CommessaRequest dto, Long idUtente) throws ApplicationException {
		Commessa commessa = null;
		try {
			// verifica dto
			if(dto != null && dto.isValid()) {
				// salva dati
				Map<String, Object> output = this.commessaRepository.salvaCommessa(dto.getIdCommessa(), dto.getNome(), dto.getIdSettore(), idUtente);
				// valida output
				Long idCommessa = Utility.validateStoredProcedureOutput(output, true);
				// recupera commessa appena salvata
				commessa = this.commessaRepository.findValidById(idCommessa);
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
		return commessa;
	}
	
	/**
	 * cancellazione logica commessa
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// richiama procedure per cancellazione logica commessa e entita associate
			Map<String, Object> output = this.commessaRepository.eliminaCommessa(id, idUtente);
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
	 * synchronize crm data
	 * @param crmData
	 * @param idUtenteOperazione
	 */
	private void synchronizeData(List<CRMData> crmData, Long idUtenteOperazione) {
		if(crmData != null && crmData.size() > 0) {
			log.info("SYNCHRONIZE_CRM_RECORD start records sync...");
			for(CRMData entry : crmData) {
				try {
					log.info("SYNCHRONIZE_CRM_RECORD sync [{}] started", entry);
					Map<String, Object> output = this.commessaRepository.synchronizeCRMData(entry.getMatricola(), entry.getImei(), entry.getCommessa(), entry.getSettore(), entry.getOperatore(), entry.getNumeroTelefono(), entry.getEmail(), idUtenteOperazione);
					// valida output
					Utility.validateStoredProcedureOutput(output);
					log.info("SYNCHRONIZE_CRM_RECORD sync [{}] finished", entry);
				} catch(Exception e) {
					log.error("SYNCHRONIZE_CRM_RECORD error: \n{}\n{}", e.getMessage(), entry);
				}
			}
			log.info("SYNCHRONIZE_CRM_RECORD stop records sync");
		}
	}
	
}
