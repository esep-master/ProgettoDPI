package it.topnetwork.smartdpi.dto.request.offline;

import java.util.List;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.dto.request.beacon.StatoBeaconRequest;
import it.topnetwork.smartdpi.utility.OfflineUtil;

public class OfflineSyncRequest extends BaseRequest {

	private List<InterventoSyncRequest> interventi;
	private List<AllarmeDPISyncRequest> allarmiDPI;
	private List<AllarmeUomoATerraSyncRequest> allarmiCadute;
	private List<StatoBeaconRequest> statiBeacon;
	private List<AzioneOperatoreSyncRequest> azioniOperatore;
	private List<ChiusuraAllarmeDPISyncRequest> allarmiDPIRisolti;
	
	public OfflineSyncRequest() {
		super();
	}
	
	@Override
	public boolean isValid() {
		boolean isValid = false;
		OfflineUtil offlineUtil = new OfflineUtil();
		isValid = offlineUtil.isOfflineSyncRequestValid(this);
		return isValid;
	}

	public List<InterventoSyncRequest> getInterventi() {
		return interventi;
	}

	public void setInterventi(List<InterventoSyncRequest> interventi) {
		this.interventi = interventi;
	}

	public List<AllarmeDPISyncRequest> getAllarmiDPI() {
		return allarmiDPI;
	}

	public void setAllarmiDPI(List<AllarmeDPISyncRequest> allarmiDPI) {
		this.allarmiDPI = allarmiDPI;
	}

	public List<AllarmeUomoATerraSyncRequest> getAllarmiCadute() {
		return allarmiCadute;
	}

	public void setAllarmiCadute(List<AllarmeUomoATerraSyncRequest> allarmiCadute) {
		this.allarmiCadute = allarmiCadute;
	}

	public List<StatoBeaconRequest> getStatiBeacon() {
		return statiBeacon;
	}

	public void setStatiBeacon(List<StatoBeaconRequest> statiBeacon) {
		this.statiBeacon = statiBeacon;
	}

	public List<AzioneOperatoreSyncRequest> getAzioniOperatore() {
		return azioniOperatore;
	}

	public void setAzioniOperatore(List<AzioneOperatoreSyncRequest> azioniOperatore) {
		this.azioniOperatore = azioniOperatore;
	}

	public List<ChiusuraAllarmeDPISyncRequest> getAllarmiDPIRisolti() {
		return allarmiDPIRisolti;
	}

	public void setAllarmiDPIRisolti(List<ChiusuraAllarmeDPISyncRequest> allarmiDPIRisolti) {
		this.allarmiDPIRisolti = allarmiDPIRisolti;
	}

	@Override
	public String toString() {
		return "OfflineSyncRequest [interventi=" + interventi + ", allarmiDPI=" + allarmiDPI + ", allarmiCadute="
				+ allarmiCadute + ", statiBeacon=" + statiBeacon + ", azioniOperatore=" + azioniOperatore + ", allarmiDPIRisolti=" + allarmiDPIRisolti + "]";
	}
	
}
