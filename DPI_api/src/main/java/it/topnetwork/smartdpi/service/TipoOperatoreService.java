package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.TipoOperatoreRepository;
import it.topnetwork.smartdpi.entity.TipoOperatore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class TipoOperatoreService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TipoOperatoreRepository tipoOperatoreRepository;
	
	/**
	 * recupera lista tipo operatori
	 * @return
	 * @throws ApplicationException 
	 */
	public List<TipoOperatore> getTipiOperatore() throws ApplicationException {
		List<TipoOperatore> tipiOperatore = null;
		try {
			tipiOperatore = this.tipoOperatoreRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return tipiOperatore;
	}
	
}
