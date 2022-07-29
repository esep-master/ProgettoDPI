package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.Beacon;

public interface BeaconRepository extends JpaRepository<Beacon, Long> {

	/**
	 * cerca beacon tramite id
	 * @param idBeacon
	 * @return
	 */
	@Query("SELECT b FROM Beacon b WHERE b.id = ?1")
	Beacon findValidById(Long idBeacon);
	
	/**
	 * recupera beacon disponibili
	 * @return
	 */
	@Query("SELECT b FROM Beacon b WHERE b.livelloBatteria > 0 AND b.id NOT IN ("
				+ "SELECT d.beacon.id FROM DPI d"
			+ ")")
	List<Beacon> findDisponibili();
	
}
