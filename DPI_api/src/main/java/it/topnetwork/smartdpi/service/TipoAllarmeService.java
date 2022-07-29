package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.TipoAllarmeRepository;
import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class TipoAllarmeService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TipoAllarmeRepository tipoAllarmeRepository;
	
	/**
	 * recupera lista tipo allarmi
	 * @return
	 * @throws ApplicationException 
	 */
	public List<TipoAllarme> getTipiAllarmi() throws ApplicationException {
		List<TipoAllarme> tipiAllarmi = null;
		try {
			tipiAllarmi = this.tipoAllarmeRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return tipiAllarmi;
	}

}