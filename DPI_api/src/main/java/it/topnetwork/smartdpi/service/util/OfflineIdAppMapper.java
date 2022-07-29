package it.topnetwork.smartdpi.service.util;

import java.util.HashMap;
import java.util.Map;

import it.topnetwork.smartdpi.entity.Allarme;
import it.topnetwork.smartdpi.entity.Intervento;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

public class OfflineIdAppMapper {

	private Map<Long, Intervento> appId2interventi;
	private Map<Long, Allarme> appId2allarmi;
	
	public OfflineIdAppMapper() {
		this.appId2interventi = new HashMap<>();
		this.appId2allarmi = new HashMap<>();
	}

	public Map<Long, Intervento> getAppId2interventi() {
		return appId2interventi;
	}

	public Map<Long, Allarme> getAppId2allarmi() {
		return appId2allarmi;
	}

	/**
	 * recupera nuovo intervento inserito da id app
	 * @param idAppIntervento
	 * @return
	 * @throws ApplicationException
	 */
	public Intervento getIntervento(long idAppIntervento) throws ApplicationException {
		Intervento intervento = null;
		if(this.appId2interventi != null && this.appId2interventi.containsKey(idAppIntervento)) {
			intervento = this.appId2interventi.get(idAppIntervento);
		} else {
			throw new ApplicationException(ErrorCode.FIND_RESULT, "id intervento da app inesistente");
		}
		return intervento;
	}
	
}
