package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.TipoBeaconRepository;
import it.topnetwork.smartdpi.entity.TipoBeacon;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class TipoBeaconService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TipoBeaconRepository tipoBeaconRepository;
	
	/**
	 * recupera lista tipo beacon
	 * @return
	 * @throws ApplicationException 
	 */
	public List<TipoBeacon> getTipiBeacon() throws ApplicationException {
		List<TipoBeacon> tipiBeacon = null;
		try {
			tipiBeacon = this.tipoBeaconRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return tipiBeacon;
	}

}
