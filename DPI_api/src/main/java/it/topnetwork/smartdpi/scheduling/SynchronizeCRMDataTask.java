package it.topnetwork.smartdpi.scheduling;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.service.CommessaService;
import it.topnetwork.smartdpi.service.ConfigurazioneService;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.Config;

@Component
public class SynchronizeCRMDataTask {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommessaService commessaService;
	
	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private Config config;

//	@Scheduled(fixedRate = 25000)
	@Scheduled(cron = "0 0 8 * * ?")
	public void readCRMData()
	{
		try {
			Configurazione useCRMConfig = this.configurazioneService.getConfig(this.config.getUseCRM());
			if(useCRMConfig != null && Utility.getBooleanValue(useCRMConfig.getValore())) {
				// call rest api
				log.info("Scheduled synchronization CRM started at [{}]", Utility.dateFormat.format(new Date()));
				// in caso di sync schedulata, imposto a 0 l'id utente operazione
				this.commessaService.synchronizeCRMData(0l);
			}
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}

}
