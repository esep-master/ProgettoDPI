package it.topnetwork.smartdpi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import it.topnetwork.smartdpi.entity.Kit;

public interface KitRepository extends JpaRepository<Kit, Long> {

	/**
	 * cerca kit tramite id
	 * @param idKit
	 * @return
	 */
	@Query("SELECT k FROM Kit k WHERE k.id = ?1")
	Kit findValidById(Long idKit);

	/**
	 * recupera tutti i kit validi
	 * @return
	 */
	@Query("SELECT k FROM Kit k")
	List<Kit> findAllValidi();

	/**
	 * recupera kit tramite settore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT k FROM Kit k WHERE k.settore.id = ?1")
	List<Kit> findBySettore(Long idSettore);

	/**
	 * count kit tramite settore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k WHERE k.settore.id = ?1")
	int countBySettore(Long idSettore);

	/**
	 * count kit non associati
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k WHERE k.operatore IS NULL")
	int countNonAssociati();

	/**
	 * count kit con un singolo DPI in allarme
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k WHERE k.id IN ("
			+ 	"SELECT a.kit.id "
			+ 	"FROM Allarme a "
			+ 	"WHERE a.kit.operatore.id IN ("
			+		"SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
			+			"SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+		")"
			+ 	") "
			+ 	"AND a.statoAllarme.statoFinale IS FALSE "
			+ 	"GROUP BY a.kit.id "
			+ 	"HAVING COUNT(a) = 1"
			+ ")")
	int countSingoloDPIAllarme(Long idUtente);

	/**
	 * count kit con piu di un DPI in allarme
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k WHERE k.id IN ("
			+ 	"SELECT a1.kit.id "
			+ 	"FROM Allarme a1, Allarme a2 "
			+ 	"WHERE a1.id <> a2.id "
			+ 	"AND a1.kit.operatore.id IN ("
			+		"SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
			+			"SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+		")"
			+ 	") "
			+ 	"AND a1.kit.id = a2.kit.id "
			+ 	"AND a1.statoAllarme.statoFinale IS FALSE "
			+ 	"AND a2.statoAllarme.statoFinale IS FALSE "
			+ 	"AND a1.dpi IS NOT NULL "
			+ 	"AND a2.dpi IS NOT NULL "
			+ 	"AND a1.dpi.id <> a2.dpi.id "
			+ 	"GROUP BY a1.kit.id "
			+ 	"HAVING COUNT(a1) > 1"
			+ ")")
	int countMultiDPIAllarme(Long idUtente);

	/**
	 * count kit disattivati (kit con tutti i DPI con sblocco automatico)
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k WHERE k.operatore.id IN ("
			+	"SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
			+		"SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+	")"
			+ ") "
			+ "AND k.id NOT IN ("
			+ 	"SELECT dk.kit.id FROM DPIKit dk "
			+ 	"WHERE (dk.sbloccoAllarmeDa IS NULL AND dk.sbloccoAllarmeA IS NULL) "
			+ 	"OR dk.sbloccoAllarmeDa > CURRENT_TIMESTAMP "
			+ 	"OR dk.sbloccoAllarmeA < CURRENT_TIMESTAMP"
			+ ")")
	int countDisattivati(Long idUtente);

	/**
	 * count kit con nessun dpi in allarme
	 * @param idUtente
	 * @return
	 */
	@Query("SELECT COUNT(k) FROM Kit k "
			+ "WHERE k.operatore.id IN ("
			+	"SELECT osc.operatore.id FROM OperatoreSedeCommessa osc WHERE osc.sedeCommessa.id IN ("
			+		"SELECT usc.sedeCommessa.id FROM UtenteSedeCommessa usc WHERE usc.utente.id = ?1"
			+	")"
			+ ") "
			+ "AND k.id NOT IN ("
			+ 	"SELECT a.kit.id "
			+	"FROM Allarme a "
			+	"WHERE a.statoAllarme.statoFinale IS FALSE"
			+ ")"
//			+ "WHERE k.id NOT IN ("
//			+ 	"SELECT a.kit.id "
//			+ 	"FROM Allarme a"
//			+ ") "
//			+ "OR k.id IN ("
//			+ 	"SELECT a.kit.id "
//			+ 	"FROM Allarme a "
//			+ 	"WHERE a.statoAllarme.statoFinale IS FALSE "
//			+ 	"GROUP BY a.kit.id "
//			+ 	"HAVING COUNT(a) = 0"
//			+ ") "
//			+ "OR k.id IN ("
//			+ 	"SELECT a.kit.id "
//			+ 	"FROM Allarme a "
//			+ 	"WHERE a.statoAllarme.statoFinale IS TRUE "
//			+ 	"GROUP BY a.kit.id "
//			+ 	"HAVING COUNT(a) >= 0"
//			+ ")")
			)
	int countOk(Long idUtente);

	/**
	 * recupera kit tramite operatore e settore
	 * @param idOperatore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT k FROM Kit k WHERE k.operatore.id = ?1 AND k.settore.id = ?2")
	Kit findByOperatoreAndSettore(Long idOperatore, Long idSettore);

	/**
	 * recupera tutti i kit a cui è associato il DPI
	 * @param idDPI
	 * @return
	 */
	@Query("SELECT k FROM Kit k WHERE k.id IN ("
			+ 	"SELECT dk.kit.id FROM DPIKit dk WHERE dk.dpi.id = ?1"
			+ ")")
	List<Kit> findByDPI(Long idDPI);

	/**
	 * soft delete kit
	 * @param idKit
	 * @param idUtenteOperazione
	 */
	@Procedure(procedureName = "ELIMINA_KIT", name = "Kit.eliminaKit")
	Map<String, Object> eliminaKit(@Param("ID_KIT") long idKit, @Param("ID_UTENTE_OPERAZIONE") long idUtenteOperazione);
}
