package com.SEM.InvestmentHoustSystem;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockDetailsDao extends JpaRepository<StockDetails, String>{
	
	@Query("SELECT s FROM StockDetails as s WHERE s.invokerEmail = :invokerEmail")
	public List<StockDetails> getAllStockDetailsByInvokerEmail(@Param("invokerEmail")String invokerEmail);

}
