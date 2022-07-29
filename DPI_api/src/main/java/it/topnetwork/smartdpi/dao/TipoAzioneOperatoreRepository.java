package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoAzioneOperatore;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface TipoAzioneOperatoreRepository extends JpaRepository<TipoAzioneOperatore, Long> {

	/**
	 * recupera tutti i tipi azione validi
	 * @return
	 */
	@Query("SELECT tao FROM TipoAzioneOperatore tao")
	@Cacheable(value = CacheEntry.LISTA_TIPI_AZIONI_OPERATORI)
	List<TipoAzioneOperatore> findAllValidi();
	
	/**
	 * recupera tipo azione operatore valida tramite id
	 * @param idTipoAzioneOperatore
	 * @return
	 */
	@Query("SELECT tao FROM TipoAzioneOperatore tao WHERE tao.id = ?1")
	TipoAzioneOperatore findValidById(Long idTipoAzioneOperatore);
	
}
