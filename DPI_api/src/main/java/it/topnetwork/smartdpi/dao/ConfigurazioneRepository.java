package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.Configurazione;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface ConfigurazioneRepository extends JpaRepository<Configurazione, Long> {

	/**
	 * recupera tutte le configurazioni
	 * @return
	 */
	@Query("SELECT c FROM Configurazione c WHERE c.loginApp = TRUE")
	@Cacheable(value = CacheEntry.LISTA_CONFIG)
	List<Configurazione> findAllLogin();
	
	@Query("SELECT c FROM Configurazione c WHERE c.nome = ?1")
	Configurazione findByNome(String nome);
}
