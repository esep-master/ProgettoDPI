package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.TipoAzioneOperatoreRepository;
import it.topnetwork.smartdpi.entity.TipoAzioneOperatore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class TipoAzioneOperatoreService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TipoAzioneOperatoreRepository tipoAzioneOperatoreRepository;
	
	/**
	 * recupera lista tipo beacon
	 * @return
	 * @throws ApplicationException 
	 */
	public List<TipoAzioneOperatore> getTipiAzioniOperatori() throws ApplicationException {
		List<TipoAzioneOperatore> tipiAzioniOperatori = null;
		try {
			tipiAzioniOperatori = this.tipoAzioneOperatoreRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return tipiAzioniOperatori;
	}

}
