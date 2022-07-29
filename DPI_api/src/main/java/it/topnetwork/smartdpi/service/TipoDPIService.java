package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.TipoDPIRepository;
import it.topnetwork.smartdpi.entity.TipoDPI;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class TipoDPIService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TipoDPIRepository tipoDPIRepository;
	
	/**
	 * recupera lista tipo dpi
	 * @return
	 * @throws ApplicationException 
	 */
	public List<TipoDPI> getTipiDPI() throws ApplicationException {
		List<TipoDPI> tipiDPI = null;
		try {
			tipiDPI = this.tipoDPIRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return tipiDPI;
	}

}
