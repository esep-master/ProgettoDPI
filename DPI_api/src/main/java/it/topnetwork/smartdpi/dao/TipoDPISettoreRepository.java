package it.topnetwork.smartdpi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.topnetwork.smartdpi.entity.TipoDPI;
import it.topnetwork.smartdpi.entity.TipoDPISettore;

public interface TipoDPISettoreRepository extends JpaRepository<TipoDPISettore, Long> {

	/**
	 * recupera lista dpi per il settore
	 * @param idSettore
	 * @return
	 */
	@Query("SELECT tds.tipoDPI FROM TipoDPISettore tds WHERE tds.settore.id = ?1")
	List<TipoDPI> findBySettore(Long idSettore);
}
