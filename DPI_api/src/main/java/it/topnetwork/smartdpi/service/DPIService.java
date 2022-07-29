package it.topnetwork.smartdpi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.BeaconRepository;
import it.topnetwork.smartdpi.dao.DPIRepository;
import it.topnetwork.smartdpi.dao.SettoreDPIRepository;
import it.topnetwork.smartdpi.dao.SettoreRepository;
import it.topnetwork.smartdpi.dto.request.dpi.DPIRequest;
import it.topnetwork.smartdpi.entity.Beacon;
import it.topnetwork.smartdpi.entity.DPI;
import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.entity.SettoreDPI;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class DPIService {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DPIRepository dpiRepository;
	
	@Autowired
	private BeaconRepository beaconRepository;
	
	@Autowired
	private SettoreRepository settoreRepository;
	
	@Autowired
	private SettoreDPIRepository settoreDPIRepository;
	
	/**
	 * recupera lista dpi
	 * @return
	 * @throws ApplicationException 
	 */
	public List<DPI> getDPI() throws ApplicationException {
		List<DPI> dpi = null;
		try {
			dpi = this.dpiRepository.findAllValidi();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return dpi;
	}

	/**
	 * salvataggio dati dpi e beacon
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public DPI save(DPIRequest dto, Long idUtente) throws ApplicationException {
		DPI dpi = null;
		try {
			// verifica dpi e beacon
			if(dto != null && dto.isValid()) {
				// salva dati
				Map<String, Object> output = this.dpiRepository.saveDPI(dto.getIdDPI(), dto.getCodice(), dto.getMarca(), dto.getModello(), dto.getDataScadenza(), dto.getNote(), dto.getIdTipoDPI(), 
						dto.getBeacon().getIdBeacon(), dto.getBeacon().getSeriale(), dto.getBeacon().getIdTipoBeacon(), idUtente);
				// valida output
				Long idDPI = Utility.validateStoredProcedureOutput(output, true);
				// recupera dpi appena salvato
				dpi = this.dpiRepository.findValidById(idDPI);
				// inserisce associazioni dpi-settore
				Set<SettoreDPI> nuoveAssociazioniSettori = this.insertSettoriDPI(dpi, dto.getSettoriDPI(), idUtente);
				dpi.setSettoriDPI(nuoveAssociazioniSettori);
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
		return dpi;
	}
	
	/**
	 * cancellazione logica dpi e/o beacon
	 * @param idDpi
	 * @param idBeacon
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long idDpi, Long idBeacon, Long idUtente) throws ApplicationException {
		try {
			// verifica se almeno uno dei due campi è valorizzato
			if(idDpi != 0 || idBeacon != 0) {
				// richiama procedure per cancellazione logica dpi e/o beacon
				Map<String, Object> output = this.dpiRepository.eliminaDPI(idDpi, idBeacon, idUtente);
				// valida output
				Utility.validateStoredProcedureOutput(output);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}

		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}

	/**
	 * recupera beacon disponibili
	 * @return
	 * @throws ApplicationException
	 */
	public List<Beacon> getBeaconDisponibili() throws ApplicationException {
		List<Beacon> beaconDisponibli = null;
		try {
			beaconDisponibli = this.beaconRepository.findDisponibili();
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return beaconDisponibli;
	}
	
	/**
	 * inserisce associazioni dpi - settore
	 * @param idDPI
	 * @param idSettori
	 * @param idUtente
	 * @throws ApplicationException
	 */
	private Set<SettoreDPI> insertSettoriDPI(DPI dpi, List<Long> idSettori, Long idUtente) throws ApplicationException {
		Set<SettoreDPI> nuoveAssociazioni = new HashSet<>();
		// cancellazione vecchie associazioni dpi - setttori
		this.settoreDPIRepository.deleteSettoriDPI(dpi.getId(), idUtente);
		// salvo nuove associazioni
		for(Long idSettore : idSettori) {
			Settore settore = this.settoreRepository.findValidById(idSettore);
			if(settore == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "settore specificato inesistente");
			}
			SettoreDPI settoreDPI = new SettoreDPI();
			settoreDPI.setDpi(dpi);
			settoreDPI.setSettore(settore);
			settoreDPI.setDatiInserimento(idUtente);
			// salva nuova associazine
			settoreDPI = this.settoreDPIRepository.save(settoreDPI);
			nuoveAssociazioni.add(settoreDPI);
		}
		return nuoveAssociazioni;
	}
	
}
