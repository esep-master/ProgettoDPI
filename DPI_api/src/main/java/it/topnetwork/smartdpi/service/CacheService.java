package it.topnetwork.smartdpi.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Service
@Transactional(readOnly = true)
public class CacheService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
    private CacheManager cacheManager;
	
	/**
	 * clear entry in cache o intera cache se cacheEntryName è NULL
	 * @param cacheEntryName
	 * @throws ApplicationException
	 */
	public void clearCache(String cacheEntryName) throws ApplicationException {
		try {
			boolean clearAll = !Utility.isValid(cacheEntryName); 
			Collection<String> cacheNames = this.cacheManager.getCacheNames();
			if(cacheNames != null && !cacheNames.isEmpty()) {
				if(clearAll) {
					log.info("CLEAR ALL CACHE");
					// azzera tutte le entry
					for(String entryName : cacheNames){
			            this.cacheManager.getCache(entryName).clear();
			        }
				} else {
					// azzera singola entry se esiste
					if(cacheNames.contains(cacheEntryName)) {
						log.info("CLEAR [{}] CACHE ENTRY", cacheEntryName);
						 this.cacheManager.getCache(cacheEntryName).clear();
					}
				}
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new ApplicationException(ErrorCode.GENERIC_ERROR, e.getMessage());
		}
	}
	
}
