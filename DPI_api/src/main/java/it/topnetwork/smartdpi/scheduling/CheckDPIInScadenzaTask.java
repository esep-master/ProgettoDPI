package it.topnetwork.smartdpi.scheduling;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.topnetwork.smartdpi.entity.Allarme;
import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.service.AllarmeService;
import it.topnetwork.smartdpi.service.ConfigurazioneService;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.Config;

@Component
public class CheckDPIInScadenzaTask {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AllarmeService allarmeService;

	@Autowired
	private ConfigurazioneService configurazioneService;
	
	@Autowired
	private Config config;

	//	@Scheduled(fixedRate = 50000)
	@Scheduled(cron = "0 0 9 * * ?")
	public void checkDPI()
	{
		try {
			Configurazione giorniPreavvisoConfig = this.configurazioneService.getConfig(this.config.getGiorniPreavvisoScadenzaDPI());
			if(giorniPreavvisoConfig != null) {
				// recupero giorni scadenza interi
				int giorniScadenza = Utility.getIntValue(giorniPreavvisoConfig.getValore());
				if(giorniScadenza > 0) {
					// call rest api
					log.info("Scheduled check DPI expriring in {} days [{}]", giorniScadenza, Utility.dateFormat.format(new Date()));
					List<Allarme> allarmiGenerati = this.allarmeService.insertDPIInScadenza(giorniScadenza);
					if(allarmiGenerati != null && !allarmiGenerati.isEmpty()) {
						log.info("DPI expire in {} days: ", giorniScadenza);
						for(Allarme allarme : allarmiGenerati) {
							log.info("allarme {}: dpi {} expires on {}", allarme.getId(), allarme.getDpi().getId(), allarme.getDpi().getDataScadenza());
						}
					} else {
						log.info("no DPI expiring in {} days: ", giorniScadenza);
					}
				}
			}
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}

}
