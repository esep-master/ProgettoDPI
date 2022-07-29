package it.topnetwork.smartdpi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.SedeCommessaRepository;
import it.topnetwork.smartdpi.dto.request.sede.SedeCommessaRequest;
import it.topnetwork.smartdpi.entity.SedeCommessa;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class SedeCommessaService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SedeCommessaRepository sedeCommessaRepository;
	
	/**
	 * recupera lista sedi commesse valide per l'utente
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	public List<SedeCommessa> getSediCommesse(Long idUtente) throws ApplicationException {
		List<SedeCommessa> sediCommesse = null;
		try {
			sediCommesse = this.sedeCommessaRepository.findAllValidi(idUtente);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return sediCommesse;
	}

	/**
	 * salvataggio sede commessa
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public SedeCommessa save(SedeCommessaRequest dto, Long idUtente) throws ApplicationException {
		SedeCommessa sedeCommessa = null;
		try {
			// verifica dto
			if(dto != null && dto.isValid()) {
				// salva dati
				Map<String, Object> output = this.sedeCommessaRepository.salvaSedeCommessa(dto.getIdSedeCommessa(), dto.getNome(), dto.getIdCommessa(), idUtente);
				// valida output
				Long idSedeCommessa = Utility.validateStoredProcedureOutput(output, true);
				// recupera commessa appena salvata
				sedeCommessa = this.sedeCommessaRepository.findValidById(idSedeCommessa);
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
		return sedeCommessa;
	}
	
	/**
	 * cancellazione logica sede commessa
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// richiama procedure per cancellazione logica sede commessa e entita associate
			Map<String, Object> output = this.sedeCommessaRepository.eliminaSedeCommessa(id, idUtente);
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

}
