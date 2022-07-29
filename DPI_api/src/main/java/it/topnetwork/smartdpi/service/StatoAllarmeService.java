package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.StatoAllarmeRepository;
import it.topnetwork.smartdpi.entity.StatoAllarme;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class StatoAllarmeService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatoAllarmeRepository statoAllarmeRepository;
	
	/**
	 * recupera lista stati allarmi
	 * @return
	 * @throws ApplicationException 
	 */
	public List<StatoAllarme> getStatiAllarmi() throws ApplicationException {
		List<StatoAllarme> statiAllarmi = null;
		try {
			statiAllarmi = this.statoAllarmeRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return statiAllarmi;
	}

}