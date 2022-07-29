package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.Ruolo;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface RuoloRepository extends JpaRepository<Ruolo, Long> {

	/**
	 * recupera tutti i ruoli validi
	 * @return
	 */	
	@Query("SELECT r FROM Ruolo r")
	@Cacheable(value = CacheEntry.LISTA_RUOLI)
	List<Ruolo> findAllValidi();
	
	/**
	 * recupera ruolo valido tramite id
	 * @param idRuolo
	 * @return
	 */
	@Query("SELECT r FROM Ruolo r WHERE r.id = ?1")
	Ruolo findValidById(Long idRuolo);
}
