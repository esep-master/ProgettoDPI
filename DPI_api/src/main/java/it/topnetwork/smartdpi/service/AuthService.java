package it.topnetwork.smartdpi.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.dao.OperatoreRepository;
import it.topnetwork.smartdpi.dao.TipoAllarmeRepository;
import it.topnetwork.smartdpi.dao.TipoAzioneOperatoreRepository;
import it.topnetwork.smartdpi.dao.UtenteRepository;
import it.topnetwork.smartdpi.dto.response.model.LoginAppResponse;
import it.topnetwork.smartdpi.dto.response.model.LoginResponse;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.TipoAllarme;
import it.topnetwork.smartdpi.entity.TipoAzioneOperatore;
import it.topnetwork.smartdpi.entity.Utente;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.mail.MailUtil;
import it.topnetwork.smartdpi.utility.TokenUtil;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class AuthService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UtenteRepository utenteRepository;
	
	@Autowired
	private OperatoreRepository operatoreRepository;
	
	@Autowired
	private TipoAzioneOperatoreRepository tipoAzioneOperatoreRepository;
	
	@Autowired
	private TipoAllarmeRepository tipoAllarmeRepository;

	@Autowired
	private AzioneOperatoreService azioneOperatoreService;
	
	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private TokenUtil tokenUtil;	
	
	@Autowired
	private MailUtil mailUtil;
	
	/**
	 * autenticazione utente
	 * @param username
	 * @param password
	 * @return
	 * @throws ApplicationException
	 */
	public LoginResponse auth(String username, String password) throws ApplicationException {
		LoginResponse login = new LoginResponse();

		try {
			// encode password BASE64
			String passwordEncoded = Utility.encodeBASE64(password);
			
			Utente utente = this.utenteRepository.findByUsernameAndPassword(username, passwordEncoded);
			
			if(utente != null) {
				// recupero configs
				List<Configurazione> configs = this.configurazioneService.getLoginConfigs();
				// create token
				try {
					String token = this.tokenUtil.createJWT(utente.getId().toString());
					
					login.setUser(utente);
					login.setConfigurazioni(configs);
					login.setToken(token);
				}
				catch(Exception e) {
					log.error(e.getMessage());
					throw new ApplicationException(ErrorCode.AUTH_TOKEN, e.getMessage());
				}
			} else {
				throw new ApplicationException(ErrorCode.WRONG_CREDENTIAL, "username e/o password errati");
			}
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return login;
	}

	/**
	 * recupero password
	 * @param email
	 * @throws ApplicationException
	 */
	public void recoveryPassword(String email) throws ApplicationException {
		Utente utenteDaNotificare = this.utenteRepository.findByEmail(email);

		if(utenteDaNotificare != null) {
			this.mailUtil.sendRecoveryPasswordMail(utenteDaNotificare);
		} else {
			throw new ApplicationException(ErrorCode.FIND_EMAIL_ADDRESS, "impossibile recuperare email");
		}
	}

	/**
	 * Autenticazione operatore app
	 * @param matricola
	 * @param password
	 * @return
	 * @throws ApplicationException
	 */
	@Transactional(readOnly = false, rollbackFor = {ApplicationException.class, RuntimeException.class})
	public LoginAppResponse authApp(String matricola, String password) throws ApplicationException {
		LoginAppResponse login = new LoginAppResponse();
		try {
			// recupero operatore
			Operatore operatore = this.operatoreRepository.findByMatricola(matricola);
			if(operatore == null) {
				throw new ApplicationException(ErrorCode.WRONG_CREDENTIAL, "matricola non valida");
			}

			// encode password BASE64
			String passwordEncoded = password != null ? Utility.encodeBASE64(password) : "";
			// verifica correttezza password
			if(!operatore.getPassword().equals(passwordEncoded)) {
				throw new ApplicationException(ErrorCode.WRONG_CREDENTIAL, "password non valida");
			}

			// recupera super admin e admin per sede commesse operatore
			List<Utente> listaAdmin = this.utenteRepository.findAdminSediCommesseOperatore(operatore.getId());
			// rimuovi funzioni ruolo per tutti gli admin
			if( listaAdmin != null && !listaAdmin.isEmpty()) {
				for(Utente admin : listaAdmin) {
					admin.getRuolo().setFunzioniRuolo(null);
				}
			}
			// recupera lista tipi azioni operatore
			List<TipoAzioneOperatore> tipiAzioni = this.tipoAzioneOperatoreRepository.findAllValidi();
			// recupero configs
			List<Configurazione> configs = this.configurazioneService.getLoginConfigs();
			// recupera lista tipi allarmi
			List<TipoAllarme> tipiAllarmi = this.tipoAllarmeRepository.findAllValidi();
			// create token
			try {
				String token = this.tokenUtil.createJWT(operatore.getId().toString());
				
				// set campi
				login.setOperatore(operatore);
				login.setAdmin(listaAdmin);
				login.setTipiAzioneOperatori(tipiAzioni);
				login.setConfigurazioni(configs);
				login.setTipiAllarmi(tipiAllarmi);
				login.setToken(token);
			}
			catch(Exception e) {
				log.error(e.getMessage());
				throw new ApplicationException(ErrorCode.AUTH_TOKEN, e.getMessage());
			}
			// salva azione operatore (login)
			this.azioneOperatoreService.login(operatore.getId(), new Date());
		} catch(ApplicationException ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
		return login;
	}
	
	/**
	 * recupero password operatore
	 * @param email
	 * @throws ApplicationException
	 */
	public void recoveryPasswordOperatore(String email) throws ApplicationException {
		Operatore operatoreDaNotificare = this.operatoreRepository.findByEmail(email);

		if(operatoreDaNotificare != null) {
			this.mailUtil.sendRecoveryPasswordMail(operatoreDaNotificare);
		} else {
			throw new ApplicationException(ErrorCode.FIND_EMAIL_ADDRESS, "impossibile recuperare email");
		}
	}

}
