package it.topnetwork.smartdpi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.RuoloRepository;
import it.topnetwork.smartdpi.dao.SedeCommessaRepository;
import it.topnetwork.smartdpi.dao.UtenteRepository;
import it.topnetwork.smartdpi.dao.UtenteSedeCommessaRepository;
import it.topnetwork.smartdpi.dto.request.utente.ChangePasswordRequest;
import it.topnetwork.smartdpi.dto.request.utente.InsertUtenteRequest;
import it.topnetwork.smartdpi.dto.request.utente.UpdateUtenteRequest;
import it.topnetwork.smartdpi.dto.request.utente.UtenteSedeCommessaRequest;
import it.topnetwork.smartdpi.entity.Ruolo;
import it.topnetwork.smartdpi.entity.SedeCommessa;
import it.topnetwork.smartdpi.entity.Utente;
import it.topnetwork.smartdpi.entity.UtenteSedeCommessa;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class UtenteService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private UtenteRepository utenteRepository;

	@Autowired
	private RuoloRepository ruoloRepository;

	@Autowired
	private SedeCommessaRepository sedeCommessaRepository;

	@Autowired
	private UtenteSedeCommessaRepository utenteSedeCommessaRepository;

	/**
	 * recupera lista utenti visibili ad un particolare utente
	 * @param idUtente
	 * @return
	 * @throws ApplicationException 
	 */
	public List<Utente> getListaUtenti(Long idUtente) throws ApplicationException {
		List<Utente> utenti = null;
		try {
			utenti = this.utenteRepository.findPerUtente(idUtente);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return utenti;
	}

	/**
	 * inserimento nuovo utente
	 * @param dto
	 * @param idUtente 
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Utente insert(InsertUtenteRequest dto, Long idUtente) throws ApplicationException {
		Utente utente = null;
		try {
			// crea entity utente
			utente = this.createUtente(dto, idUtente);
			// salva utente
			utente = this.utenteRepository.save(utente);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return utente;
	}

	/**
	 * aggiornamento utente
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Utente update(UpdateUtenteRequest dto, Long idUtente) throws ApplicationException {
		Utente utente = null;
		try {
			// prepara entity utente da modificare
			utente = this.prepareUtenteForUpdate(dto, idUtente);
			// salva utente
			utente = this.utenteRepository.save(utente);
		}
		catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return utente;
	}

	/**
	 * cancellazione logica utente
	 * @param id
	 * @param idUtente
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void delete(Long id, Long idUtente) throws ApplicationException {
		try {
			// richiama procedure per cancellazione logica utente e entita associate
			Map<String, Object> output = this.utenteRepository.eliminaUtente(id, idUtente);
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
	 * cambio password
	 * @param dto
	 * @param idUtente
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public void changePassword(ChangePasswordRequest dto, Long idUtente) throws ApplicationException {
		try {
			if(dto.getOldPassword().equals(dto.getNewPassword())) {
				throw new ApplicationException(ErrorCode.SAME_PASSWORD, "la nuova password deve essere diversa dalla vecchia.");
			}
			// encode password BASE64
			String oldPasswordEncoded = Utility.encodeBASE64(dto.getOldPassword());
			String newPasswordEncoded = Utility.encodeBASE64(dto.getNewPassword());

			Utente utente = this.utenteRepository.findByIdAndPassword(idUtente, oldPasswordEncoded);

			if(utente != null) {
				// update password
				utente.setPassword(newPasswordEncoded);

				this.utenteRepository.save(utente);
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
	 * associa un utente ad una sede commessa
	 * @param dto
	 * @param idUtente
	 * @param isAssociazione TRUE se associazione, FALSE se disassociazione
	 * @return 
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public Utente associaSedeCommessa(UtenteSedeCommessaRequest dto, Long idUtente, boolean isAssociazione) throws ApplicationException {
		Utente utente = null;
		try {
			if(dto != null && dto.isValid()) {
				// recupero utente
				utente = this.utenteRepository.findValidById(dto.getIdUtente());
				if(utente == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "utente inesistente");
				}
				//recupero sede commessa
				SedeCommessa sedeCommessa = this.sedeCommessaRepository.findValidById(dto.getIdSedeCommessa());
				if(sedeCommessa == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "sede commessa inesistente");
				}

				// verifica esistenza associazione in base ad associazione/disassociazione
				UtenteSedeCommessa utenteSedeCommessa = this.utenteSedeCommessaRepository.findByUtenteAndSedeCommessa(dto.getIdUtente(), dto.getIdSedeCommessa());;
				if(isAssociazione) {
					if(utenteSedeCommessa != null) {
						throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "associazione utente - sede commessa gia esistente");
					}
					// creo associazione
					utenteSedeCommessa = new UtenteSedeCommessa();
					utenteSedeCommessa.setUtente(utente);
					utenteSedeCommessa.setSedeCommessa(sedeCommessa);
					utenteSedeCommessa.setDatiInserimento(idUtente);
					// salvo associazione
//					this.utenteSedeCommessaRepository.save(utenteSedeCommessa);
				} else {
					if(utenteSedeCommessa == null) {
						throw new ApplicationException(ErrorCode.FIND_RESULT, "associazione utente - sede commessa inesistente");
					}
					// remove associazione (soft delete)
					utenteSedeCommessa.setDatiCancellazione(idUtente);
//					this.utenteSedeCommessaRepository.save(utenteSedeCommessa);
				}

				this.utenteSedeCommessaRepository.save(utenteSedeCommessa);
				
				// ricarico associazioni sedi commesse utente
				Set<UtenteSedeCommessa> sediCommesseUtente = utente.getUtenteSediCommesse();
				if(isAssociazione) {
					sediCommesseUtente.add(utenteSedeCommessa);
				} else {
					sediCommesseUtente.remove(utenteSedeCommessa);
				}
//				Set<UtenteSedeCommessa> sediCommesseUtente = this.utenteSedeCommessaRepository.findByUtente(utente.getId());
//				utente.setUtenteSediCommesse(sediCommesseUtente);

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
		return utente;
	}

	/**
	 * recupero lista commesse visibili
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	public List<UtenteSedeCommessa> getListaCommesse(Long idUtente) throws ApplicationException {
		List<UtenteSedeCommessa> sediCommesse = null;
		try {
			// recupero associazioni sedi commesse utente
			Set<UtenteSedeCommessa> sediCommesseUtente = this.utenteSedeCommessaRepository.findByUtente(idUtente);
			sediCommesse = new ArrayList<>(sediCommesseUtente);
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.FIND_RESULT, e.getMessage());
		}
		return sediCommesse;
	}

	/**
	 * crea entity utente
	 * @param dto
	 * @param idUtente utente inserimento
	 * @return
	 * @throws ApplicationException
	 */
	private Utente createUtente(InsertUtenteRequest dto, Long idUtente) throws ApplicationException {
		Utente utente = new Utente();
		try {
			if(dto != null && dto.isValid()) {
				// cerca username già esistente
				Utente checkUtente = this.utenteRepository.findByUsername(dto.getUsername());
				if(checkUtente != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "username gia esistente");
				}
				// cerca email già esistente
				checkUtente = this.utenteRepository.findByEmail(dto.getEmail());
				if(checkUtente != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "email gia esistente");
				}
				// cerca tipo ruolo
				Ruolo ruoloUtente = this.ruoloRepository.findValidById(dto.getIdRuolo());
				if(ruoloUtente == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "ruolo specificato inesistente");
				}
				// set campi
				utente.setUsername(dto.getUsername());
				utente.setPassword(Utility.encodeBASE64(dto.getPassword()));
				utente.setEmail(dto.getEmail());
				utente.setNumeroTelefono(dto.getNumeroTelefono());
				utente.setNome(dto.getNome());
				utente.setCognome(dto.getCognome());
				utente.setRuolo(ruoloUtente);
				utente.setDatiInserimento(idUtente);
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
		return utente;
	}

	/**
	 * prepara utente per modifica
	 * @param dto
	 * @param idUtente
	 * @return
	 * @throws ApplicationException
	 */
	private Utente prepareUtenteForUpdate(UpdateUtenteRequest dto, Long idUtente) throws ApplicationException {
		Utente utente = null;
		if(dto != null && dto.isValid()) {
			// cerca utente tramite id
			utente = this.utenteRepository.findValidById(dto.getIdUtente());
			if(utente == null) {
				throw new ApplicationException(ErrorCode.FIND_RESULT, "utente specificato inesistente");
			}
			// cerca username già esistente per un altro utente se username modificato
			if(Utility.isValid(dto.getUsername()) && !dto.getUsername().equalsIgnoreCase(utente.getUsername())) {
				Utente checkUtente = this.utenteRepository.findByUsername(dto.getUsername());
				if(checkUtente != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "username gia esistente");
				}
				utente.setUsername(dto.getUsername());
			}
			// cerca email già esistente per un altro utente se email cambiata
			if(Utility.isValid(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(utente.getEmail())) {
				Utente checkUtente = this.utenteRepository.findByEmail(dto.getEmail());
				if(checkUtente != null) {
					throw new ApplicationException(ErrorCode.ALREADY_ESISTS, "email gia esistente");
				}
				utente.setEmail(dto.getEmail());
			}
			// cerca tipo ruolo se modificato
			if(dto.getIdRuolo() != 0 && dto.getIdRuolo() != utente.getRuolo().getId()) {
				Ruolo ruoloUtente = this.ruoloRepository.findValidById(dto.getIdRuolo());
				if(ruoloUtente == null) {
					throw new ApplicationException(ErrorCode.FIND_RESULT, "ruolo specificato inesistente");
				}
				utente.setRuolo(ruoloUtente);
			}
			// controllo password se modificata
			if(Utility.isValid(dto.getPassword())) {
				utente.setPassword(Utility.encodeBASE64(dto.getPassword()));
			}
			// controllo nome se modificato
			if(Utility.isValid(dto.getNome(), true)) {
				utente.setNome(dto.getNome());
			}
			// controllo cognome se modificato
			if(Utility.isValid(dto.getCognome(), true) && !dto.getCognome().equals(utente.getCognome())) {
				utente.setCognome(dto.getCognome());
			}
			// controllo numero telefono se modificato
			if(Utility.isValid(dto.getNumeroTelefono())) {
				utente.setNumeroTelefono(dto.getNumeroTelefono());
			}
			// set dati ultima modifica
			utente.setDatiUltimaModifica(idUtente);
		} else {
			throw new ApplicationException(ErrorCode.MISSING_FIELDS, "dati obbligatori mancanti");
		}
		return utente;
	}

}
