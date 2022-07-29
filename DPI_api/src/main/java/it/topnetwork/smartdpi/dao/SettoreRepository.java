package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.Settore;
import it.topnetwork.smartdpi.utility.constants.CacheEntry;

public interface SettoreRepository extends JpaRepository<Settore, Long> {

	/**
	 * recupera tutti i settori validi
	 * @return
	 */
	@Query("SELECT s FROM Settore s")
	@Cacheable(value = CacheEntry.LISTA_SETTORI)
	List<Settore> findAllValidi();
	
	/**
	 * recupera settore valido tramite id
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT s FROM Settore s WHERE s.id = ?1")
	Settore findValidById(Long idSettore);

	/**
	 * recupera settore tramite nome
	 * @param nome
	 * @return
	 */
	@Query("SELECT s FROM Settore s WHERE s.nome = ?1")
	Settore findByNome(String nome);

	/**
	 * inserimento associazioni tipi_dpi - settore
	 * @param idSettore
	 * @param idUtente
	 */
	@Procedure("INSERT_TIPI_DPI_SETTORE")
    void insertTipiDPISettore(@Param("ID_SETTORE") Long idSettore, @Param("ID_UTENTE_OPERAZIONE") Long idUtente);
	
	/**
	 * soft delete settore
	 * @param idSettore
	 * @param idUtenteOperazione
	 */	
	@Procedure(procedureName = "ELIMINA_SETTORE", name = "Settore.eliminaSettore")
	@CacheEvict(value = CacheEntry.LISTA_SETTORI, allEntries = true)
	Map<String, Object> eliminaSettore(@Param("ID_SETTORE") long idSettore, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
	
}
