package it.topnetwork.smartdpi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.KitRepository;
import it.topnetwork.smartdpi.dao.OperatoreRepository;
import it.topnetwork.smartdpi.dao.OperatoreSedeCommessaRepository;
import it.topnetwork.smartdpi.dao.SedeCommessaRepository;
import it.topnetwork.smartdpi.dao.TipoOperatoreRepository;
import it.topnetwork.smartdpi.dto.request.operatore.InsertOperatoreRequest;
import it.topnetwork.smartdpi.dto.request.operatore.OperatoreKitRequest;
import it.topnetwork.smartdpi.dto.request.operatore.OperatoreSedeCommessaRequest;
import it.topnetwork.smartdpi.dto.request.operatore.UpdateOperatoreRequest;
import it.topnetwork.smartdpi.dto.request.utente.ChangePasswordRequest;
import it.topnetwork.smartdpi.entity.Kit;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.OperatoreSedeCommessa;
import it.topnetwork.smartdpi.entity.SedeCommessa;
import it.topnetwork.smartdpi.entity.TipoOperatore;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class OperatoreService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OperatoreRepository operatoreRepository;

	@Autowired
	private TipoOperatoreRepository tipoOperatoreRepository;

	@Autowired
	private SedeCommessaRepository sedeCommessaRepository;

	@Autowired
	private OperatoreSedeCommessaRepository operatoreSedeCommessaRepository;
	
	@Autowired
	private KitRepository kitRepository;
	
	@Autowired
	private AzioneOperatoreService azioneOperatoreService;

	/**
	 * recupera lista operatori visibili ad un particolare utente
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Operatore> getListaOperatori(Long idUtente) throws ApplicationException {
		List<Operatore> operatori = null;
		try {
			operatori = this.operatoreRepository.findPerUtente(idUtente);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return operatori;
	}

	/**
	 * inserimento nuovo operatore
	 * @param dto
	 * @param idUtente 
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Operatore insert(InsertOperatoreRequest dto, Long idUtente) throws ApplicationException {
		Operatore operatore = null;
		try {
			// crea entity utente
			operatore = this.createOperatore(dto, idUtente);
			// salva utente
			operatore = this.operatoreRepository.save(operatore);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}

	/**
	 * aggiornamento operatore
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Operatore update(UpdateOperatoreRequest dto, Long idUtente) throws ApplicationException {
		Operatore operatore = null;
		try {
			// prepara entity utente da modificare
			operatore = this.prepareOperatoreForUpdate(dto, idUtente);
			// salva utente
			operatore = this.operatoreRepository.save(operatore);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}
	
	/**
	 * cancellazione logica operatore
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// richiama procedure per cancellazione logica operatore e entita associate
			Map<String, Object> output = this.operatoreRepository.eliminaOperatore(id, idUtente);
			// valida output
			Utility.validateStoredProcedureOutput(output);
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}

	/**
	 * associa un operatore ad una sede commessa
	 * @param dto
	 * @param idUtente
	 * @param isAssociazione TRUE se associazione, FALSE se disassociazione
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Operatore associaSedeCommessa(OperatoreSedeCommessaRequest dto, Long idUtente, boolean isAssociazione) throws ApplicationException {
		Operatore operatore = null;
		try {
			if(dto != null && dto.isValid()) {
				// recupero operatore
				operatore = this.operatoreRepository.findValidById(dto.getIdOperatore());
				if(operatore == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore inesistente");
				}
				// recupero sede commessa
				SedeCommessa sedeCommessa = this.sedeCommessaRepository.findValidById(dto.getIdSedeCommessa());
				if(sedeCommessa == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "sede commessa inesistente");
				}
				// verifica esistenza associazione in base ad associazione/disassociazione
				OperatoreSedeCommessa operatoreSedeCommessa = this.operatoreSedeCommessaRepository.findByUtenteAndSedeCommessa(dto.getIdOperatore(), dto.getIdSedeCommessa());
				if(isAssociazione) {
					if(operatoreSedeCommessa != null) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "associazione operatore - sede commessa gia esistente");
					}
					// creo associazione
					operatoreSedeCommessa = new OperatoreSedeCommessa();
					operatoreSedeCommessa.setOperatore(operatore);
					operatoreSedeCommessa.setSedeCommessa(sedeCommessa);
					operatoreSedeCommessa.setDatiInserimento(idUtente);
				} else {
					if(operatoreSedeCommessa == null) {
						throw new ApplicationException(ErrorCode.FIND_RESULT, "associazione operatore - sede commessa inesistente");
					}
					// remove associazione (soft delete)
					operatoreSedeCommessa.setDatiCancellazione(idUtente);
				}
				// salvo associazione
				operatoreSedeCommessa = this.operatoreSedeCommessaRepository.save(operatoreSedeCommessa);
				// ricarico associazioni operatore
				Set<OperatoreSedeCommessa> sediCommesseOperatore = operatore.getOperatoreSediCommesse();
				if(isAssociazione) {
					sediCommesseOperatore.add(operatoreSedeCommessa);
				} else {
					sediCommesseOperatore.remove(operatoreSedeCommessa);
				}
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}
	
	/**
	 * associa un operatore ad un kit
	 * @param dto
	 * @param idUtente
	 * @param isAssociazione TRUE se associazione, FALSE se disassociazione
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Operatore associaKit(OperatoreKitRequest dto, Long idUtente, boolean isAssociazione) throws ApplicationException {
		Operatore operatore = null;
		try {
			if(dto != null && dto.isValid()) {
				// recupero operatore
				operatore = this.operatoreRepository.findValidById(dto.getIdOperatore());
				if(operatore == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore inesistente");
				}
				// recupero kit
				Kit kit = this.kitRepository.findValidById(dto.getIdKit());
				if(kit == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "kit inesistente");
				}
				// verifica se kit associato ad un altro operatore
				if(kit.getOperatore() != null && !kit.getOperatore().getId().equals(operatore.getId())) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "il kit è già associato ad un altro operatore");
				}
				// verifica kit operatore già presente per quel settore
				Kit checkKit = this.kitRepository.findByOperatoreAndSettore(dto.getIdOperatore(), kit.getSettore().getId());
				if(checkKit != null && (!checkKit.getId().equals(dto.getIdKit()))) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "kit per il settore già esistente");
				}

				if(isAssociazione) {
					// verifico se kit gia associato a questo operatore
					if(kit.getOperatore() != null && kit.getOperatore().getId().equals(operatore.getId())) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "associazione operatore - kit gia esistente");
					}
					// associazione operatore kit
					kit.setOperatore(operatore);
				} else {
					// verifico se kit non associato a questo operatore
					if(kit.getOperatore() == null || (kit.getOperatore() != null && !kit.getOperatore().getId().equals(operatore.getId()))) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "associazione operatore - kit inesistente");
					}
					// disassociazione operatore kit
					kit.setOperatore(null);
				}
				// set dati ultima modifica
				kit.setDatiUltimaModifica(idUtente);
				// salvataggio entity
				kit = this.kitRepository.save(kit);
				// ricarico associazioni operatore
				Set<Kit> kitOperatore = operatore.getKit();
				if(isAssociazione) {
					kitOperatore.add(kit);
				} else {
					kitOperatore.remove(kit);
				}
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		}
		catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}
	
	/**
	 * cambio password
	 * @param dto
	 * @param idOperatore
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void changePassword(ChangePasswordRequest dto, Long idOperatore) throws ApplicationException {
		try {
			// encode password BASE64
			String oldPasswordEncoded = Utility.encodeBASE64(dto.getOldPassword());
			String newPasswordEncoded = Utility.encodeBASE64(dto.getNewPassword());
			
			Operatore operatore = this.operatoreRepository.findByIdAndPassword(idOperatore, oldPasswordEncoded);

			if(operatore != null) {
				if(operatore.getTipoOperatore().isEsterno()) {
					throw new ApplicationException(ErrorCode.OPERATORE_CRM, "non puoi modificare la password per questo operatore");
				}
				
				if(dto.getOldPassword().equals(dto.getNewPassword())) {
					throw new ApplicationException(ErrorCode.SAME_PASSWORD, "la nuova password deve essere diversa dalla vecchia.");
				}

				// update password
				operatore.setPassword(newPasswordEncoded);

				this.operatoreRepository.save(operatore);
			} else {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "vecchia password errata");
			}
		}
		catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}
	
	/**
	 * logout operatore app
	 * @param idOperatore
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void logoutApp(Long idOperatore) throws ApplicationException {
		try {
			// salva azione operatore (logout)
			this.azioneOperatoreService.logout(idOperatore, new Date());
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}
	
	/**
	 * reset password operatore a quella iniziale
	 * @param idOperatore
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void resetPassword(Long idOperatore, Long idUtente) throws ApplicationException {
		try {
			// recupero operatore
			Operatore operatore = this.operatoreRepository.findValidById(idOperatore);
			if(operatore == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore specificato inesistente");
			}
			// reimposta password
			operatore.setPassword(operatore.getPasswordOriginale());
			operatore.setDatiUltimaModifica(idUtente);
		} catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}

	/**
	 * crea entity operatore
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private Operatore createOperatore(InsertOperatoreRequest dto, Long idUtente) throws ApplicationException {
		Operatore operatore = new Operatore();
		try {
			if(dto != null && dto.isValid()) {
				// cerca matricola già esistente
				Operatore checkOperatore = this.operatoreRepository.findByMatricola(dto.getMatricola());
				if(checkOperatore != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "matricola gia esistente");
				}
				// cerca email già esistente se presente
				if(Utility.isValid(dto.getEmail())) {
					checkOperatore = this.operatoreRepository.findByEmail(dto.getEmail());
					if(checkOperatore != null) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "email gia esistente");
					}
				}
				// cerca tipo ruolo
				TipoOperatore tipoOperatore = this.tipoOperatoreRepository.findValidById(dto.getIdTipoOperatore());
				if(tipoOperatore == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo operatore specificato inesistente");
				}
				// set campi
				operatore.setMatricola(dto.getMatricola());
				operatore.setPassword(Utility.encodeBASE64(dto.getPassword()));
				operatore.setNominativo(dto.getNominativo());
				operatore.setIdDispositivo(dto.getIdDispositivo());
				operatore.setNumeroTelefono(dto.getNumeroTelefono());
				operatore.setEmail(dto.getEmail());
				operatore.setTipoOperatore(tipoOperatore);
				operatore.setPasswordOriginale(Utility.encodeBASE64(dto.getPassword()));
				operatore.setDatiInserimento(idUtente);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		} catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}

	/**
	 * prepara operatore per modifica
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private Operatore prepareOperatoreForUpdate(UpdateOperatoreRequest dto, Long idUtente) throws ApplicationException {
		Operatore operatore = null;
		try {
			if(dto != null && dto.isValid()) {
				// cerca operatore tramite id
				operatore = this.operatoreRepository.findValidById(dto.getIdOperatore());
				if(operatore == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "operatore specificato inesistente");
				}
				// cerca matricola già esistente per un altro operatore se matricola modificata
				if(Utility.isValid(dto.getMatricola()) && !dto.getMatricola().equalsIgnoreCase(operatore.getMatricola())) {
					Operatore checkOperatore = this.operatoreRepository.findByMatricola(dto.getMatricola());
					if(checkOperatore != null) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "matricola gia esistente");
					}
					operatore.setMatricola(dto.getMatricola());
				}
				// cerca email già esistente per un altro operatore se email modificata
				if(Utility.isValid(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(operatore.getEmail())) {
					Operatore checkOperatore = this.operatoreRepository.findByEmail(dto.getEmail());
					if(checkOperatore != null) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "email gia esistente");
					}
					operatore.setEmail(dto.getEmail());
				}
				// cerca tipo operatore se modificato
				if(dto.getIdTipoOperatore() != 0 && dto.getIdTipoOperatore() != operatore.getTipoOperatore().getId()) {
					TipoOperatore tipoOperatore = this.tipoOperatoreRepository.findValidById(dto.getIdTipoOperatore());
					if(tipoOperatore == null) {
						throw new ApplicationException(ErrorCode.FIND_RESULT, "tipo operatore specificato inesistente");
					}
					operatore.setTipoOperatore(tipoOperatore);
				}
				// controllo password se modificata
				if(Utility.isValid(dto.getPassword())) {
					operatore.setPassword(Utility.encodeBASE64(dto.getPassword()));
				}
				// controllo nominativo se modificato
				if(Utility.isValid(dto.getNominativo(), true) && !dto.getNominativo().equals(operatore.getNominativo())) {
					operatore.setNominativo(dto.getNominativo());
				}
				// controllo numero telefono se modificato
				if(Utility.isValid(dto.getNumeroTelefono()) && !dto.getNumeroTelefono().equals(operatore.getNumeroTelefono())) {
					operatore.setNumeroTelefono(dto.getNumeroTelefono());
				}
				// controllo id dispositivo se modificato
				if(Utility.isValid(dto.getIdDispositivo()) && !dto.getIdDispositivo().equals(operatore.getIdDispositivo())) {
					operatore.setIdDispositivo(dto.getIdDispositivo());
				}
				// set dati ultima modifica
				operatore.setDatiUltimaModifica(idUtente);
			} else {
				throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
			}
		} catch(ApplicationException e) {
			log.error(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return operatore;
	}

}
