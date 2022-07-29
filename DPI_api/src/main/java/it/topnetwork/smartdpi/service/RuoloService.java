package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.RuoloRepository;
import it.topnetwork.smartdpi.entity.Ruolo;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class RuoloService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private RuoloRepository ruoloRepository;

	/**
	 * recupera lista ruoli utente
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Ruolo> getRuoli() throws ApplicationException {
		List<Ruolo> ruoli = null;
		try {
			ruoli = this.ruoloRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return ruoli;
	}
	
}
