package it.topnetwork.smartdpi.utility;

import java.util.List;

import it.topnetwork.smartdpi.dto.request.beacon.StatoBeaconRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeDPISyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.AllarmeUomoATerraSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.AzioneOperatoreSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.InterventoSyncRequest;
import it.topnetwork.smartdpi.dto.request.offline.OfflineSyncRequest;

public class OfflineUtil {

	/**
	 * controlla request per sync dati app
	 * @param dto
	 * @return
	 */
	public boolean isOfflineSyncRequestValid(OfflineSyncRequest dto) {
		boolean isValid = false;
		if(dto != null) {
			isValid = this.isInterventiValid(dto.getInterventi()) &&
				this.isAllarmiDPIValid(dto.getAllarmiDPI()) &&
				this.isAllarmiCaduteValid(dto.getAllarmiCadute()) &&
				this.isStatiBeaconValid(dto.getStatiBeacon()) &&
				this.isAzioniOperatoreValid(dto.getAzioniOperatore());
		}
		return isValid;
	}
	
	/**
	 * controlla validita interventi
	 * @param interventi
	 * @return
	 */
	private boolean isInterventiValid(List<InterventoSyncRequest> interventi) {
		boolean isValid = Utility.isValid(interventi);
		return isValid;
	}
	
	/**
	 * controlla validita allarmi DPI non indossati
	 * @param allarmiDPI
	 * @return
	 */
	private boolean isAllarmiDPIValid(List<AllarmeDPISyncRequest> allarmiDPI) {
		boolean isValid = Utility.isValid(allarmiDPI);
		return isValid;
	}
	
	/**
	 * controlla validita allarmi uomo a terra
	 * @param allarmiCadute
	 * @return
	 */
	private boolean isAllarmiCaduteValid(List<AllarmeUomoATerraSyncRequest> allarmiCadute) {
		boolean isValid = Utility.isValid(allarmiCadute);
		return isValid;
	}
	
	/**
	 * controlla validita stati beacon
	 * @param statiBeacon
	 * @return
	 */
	private boolean isStatiBeaconValid(List<StatoBeaconRequest> statiBeacon) {
		boolean isValid = Utility.isValid(statiBeacon);
		return isValid;
	}
	
	/**
	 * controlla validita azioni operatore
	 * @param azioniOperatore
	 * @return
	 */
	private boolean isAzioniOperatoreValid(List<AzioneOperatoreSyncRequest> azioniOperatore) {
		boolean isValid = Utility.isValid(azioniOperatore);
		return isValid;
	}
	
}
