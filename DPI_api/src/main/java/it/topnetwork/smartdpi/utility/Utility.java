package it.topnetwork.smartdpi.utility;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.topnetwork.smartdpi.dto.request.BaseRequest;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

public class Utility {
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	
	/**
	 * encode clear string to BASE54 string
	 * @param stringToEncode
	 * @return
	 */
	public static String encodeBASE64(String stringToEncode) {
		return Base64.getEncoder().encodeToString(stringToEncode.getBytes());
	}

	/**
	 * decode BASE64 string to clear string
	 * @param stringToDecode
	 * @return
	 */
	public static String decodeBASE64(String stringToDecode) {
		return new String(Base64.getDecoder().decode(stringToDecode));
	}
	
	/**
	 * verifica stringa non nulla e non vuota 
	 * @param value
	 * @return
	 */
	public static boolean isValid(String value) {
		return Utility.isValid(value, false);
	}
	
	/**
	 * verifica stringa non nulla e non vuota  in base al flag excludeEmptyCheck
	 * @param value
	 * @return
	 */
	public static boolean isValid(String value, boolean excludeEmptyCheck) {
		boolean isValid = value != null;
		if(!excludeEmptyCheck) {
			isValid = isValid && !value.isEmpty();
		}
		return isValid;
	}
	
	/**
	 * valida lista dto
	 * @param dtoList
	 * @return
	 */
	public static boolean isValid(List<? extends BaseRequest> dtoList) {
		boolean isValid = true;
		if(dtoList != null && !dtoList.isEmpty()) {
			for(BaseRequest dto : dtoList) {
				isValid = dto.isValid();
				if(!isValid) {
					break;
				}
			}
		}
		return isValid;
	}
	
	/**
	 * valida output da stored procedure
	 * @param output
	 * @throws ApplicationException
	 */
	public static void validateStoredProcedureOutput(Map<String, Object> output) throws ApplicationException {
		Utility.validateStoredProcedureOutput(output, false);
	}
	
	/**
	 * valida output da stored procedure e recupera Id se richiesto
	 * @param output
	 * @param returnId
	 * @return
	 * @throws ApplicationException
	 */
	public static Long validateStoredProcedureOutput(Map<String, Object> output, boolean returnId) throws ApplicationException {
		Long idEntitaCreata = 0l;
		if(output != null && !output.isEmpty()) {
			int code = output.containsKey("ESITO") ? (int) output.get("ESITO") : ErrorCode.GENERIC_DB_ERROR;
			String message = (String) output.get("ESITO_MSG");
			if(code != 0) {
				throw new ApplicationException(code, message);
			}
			if(returnId) {
				idEntitaCreata = output.containsKey("ID_ENTITA_CREATA") ? (Long) output.get("ID_ENTITA_CREATA") : 0l;
			}
		} else {
			throw new ApplicationException(ErrorCode.GENERIC_DB_ERROR, "errore interno stored procedure");
		}
		return idEntitaCreata;
	}
	
	/**
	 * parse string to int value
	 * @param stringValue
	 * @return
	 */
	public static Integer getIntValue(String stringValue) {
		Integer intValue = 0;
		intValue = Integer.parseInt(stringValue);
		return intValue;
	}
	
	/**
	 * parse string to boolean value
	 * @param stringValue
	 * @return
	 */
	public static boolean getBooleanValue(String stringValue) {
		boolean booleanValue = false;
		booleanValue = Boolean.parseBoolean(stringValue);
		return booleanValue;
	}
	
	/**
	 * costruisce data di oggi azzerando ore, minuti e secondi 'YYYY-MM-DD 00:00:00'
	 * @return
	 */
	public static Date getOggi() {
		Date oggi = null;
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        oggi = now.getTime();
        return oggi;
	}
	
	/**
	 * costruisce data scadenza da oggi a giorniScadenza
	 * @param giorniScadenza
	 * @return
	 */
	public static Date getDataScadenza(int giorniScadenza) {
		Date dataScadenza = null;
		Calendar cal = Calendar.getInstance();
        cal.setTime(Utility.getOggi());
        cal.add(Calendar.DATE, giorniScadenza);
        dataScadenza = cal.getTime();
        return dataScadenza;
	}
	
	/**
	 * get default data_cancellazione '9999-12-31 23:59:59'
	 * @return
	 */
	public static Date getDataCancellazione() {
		Date dataCancellezione = null;
		Calendar dataCancellezioneCalendar = Calendar.getInstance();
		dataCancellezioneCalendar.set(Calendar.YEAR, 9999);
		dataCancellezioneCalendar.set(Calendar.MONTH, 12);
		dataCancellezioneCalendar.set(Calendar.DAY_OF_MONTH, 31);
		dataCancellezioneCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dataCancellezioneCalendar.set(Calendar.MINUTE, 59);
		dataCancellezioneCalendar.set(Calendar.SECOND, 59);
        dataCancellezione = dataCancellezioneCalendar.getTime();
        return dataCancellezione;
	}
	
}
