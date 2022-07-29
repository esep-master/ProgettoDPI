package it.topnetwork.smartdpi.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.AzioneOperatore;

public interface AzioneOperatoreRepository extends JpaRepository<AzioneOperatore, Long> {
	
	/**
	 * recupera azioni operatore
	 * @param idOperatore
	 * @return
	 */
	@Query("SELECT a FROM AzioneOperatore a WHERE a.operatore.id = ?1")
	List<AzioneOperatore> findPerOperatore(Long idOperatore);

	/**
	 * log azione operatore
	 * @param idOperatore
	 * @param idTipoAzioneOperatore
	 * @param idIntervento
	 * @param idDPI
	 * @return
	 */
	@Procedure(procedureName = "LOG_AZIONE_OPERATORE", name = "AzioneOperatore.logAzioneOperatore")
	Map<String, Object> logAzioneOperatore(@Param("ID_OPERATORE") long idOperatore, @Param("ID_TIPO_AZIONE_OPERATORE") long idTipoAzioneOperatore,
			@Param("ID_INTERVENTO") long idIntervento, @Param("ID_DPI") long idDPI, @Param("ID_TIPO_ALLARME") long idTipoAllarme, @Param("DATA_AZIONE") Date dataAzione);
	
}
