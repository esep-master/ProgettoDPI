package it.topnetwork.smartdpi.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.BeaconRepository;
import it.topnetwork.smartdpi.dto.request.beacon.StatoBeaconRequest;
import it.topnetwork.smartdpi.entity.Beacon;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.Config;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class BeaconService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private BeaconRepository beaconRepository;
	
	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private AllarmeService allarmeService;
	
	@Autowired
	private Config config;
	
	/**
	 * aggiorna stato beacon
	 * @param beaconList
	 * @param idOperatore
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void save(List<StatoBeaconRequest> beaconList, Long idOperatore) throws ApplicationException {
		try {
			if(this.isDTOValid(beaconList)) {
				log.info("Update beacon started");
				// recupera soglia batteria per scadenza
				Configurazione configBatteria = this.configurazioneService.getConfig(this.config.getSogliaBatteriaScadenzaBeacon());
				int sogliaBatteria = Utility.getIntValue(configBatteria.getValore());
				// itera beacon e aggiorna livello batteria
				for(StatoBeaconRequest sbReq : beaconList) {
					try {
						// recupera beacon tramite id
						Beacon beacon = this.beaconRepository.findValidById(sbReq.getIdBeacon());
						if(beacon == null) {
							throw new ApplicationException(ErrorCode.FIND_RESULT, "beacon specificato [" + sbReq.getIdBeacon() + "] inesistente");
						}
						// imposta nuovo livello batteria
						beacon.setLivelloBatteria(sbReq.getBatteria());
						log.info("Update beacon {} battery [{}%]", sbReq.getIdBeacon(), sbReq.getBatteria());
						// aggiorna beacon
						this.beaconRepository.save(beacon);
						// controlla livello batteria
						if(sbReq.getBatteria() == 0) {
							// se batteria a 0 creo allarme batteria beacon scaduta
							this.allarmeService.insertBatteriaBeaconScarica(beacon);
						} else if(sbReq.getBatteria() <= sogliaBatteria) {
							// se batteria ha raggiunto la soglia creo allarme batteria beacon in scadenza
							this.allarmeService.insertBatteriaBeaconInScadenza(beacon);
						}
					} catch(Exception e) {
						log.error(e.getMessage());
					}
				}
				log.info("Update beacon finished");
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
	}
	
	/**
	 * verifica DTO valido
	 * @param dtobeaconList
	 * @return
	 */
	private boolean isDTOValid(List<StatoBeaconRequest> beaconList) {
		boolean isValid = false;
		if(beaconList != null && !beaconList.isEmpty()) {
			for(StatoBeaconRequest sbReq : beaconList) {
				isValid = sbReq.isValid();
				if(!isValid) {
					break;
				}
			}
		}
		return isValid;
	}
	
}
