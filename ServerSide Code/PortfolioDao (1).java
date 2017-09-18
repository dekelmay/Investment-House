package com.SEM.InvestmentHoustSystem;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioDao extends JpaRepository<Portfolio, Long>{
	
	@Query("SELECT p FROM Portfolio as p WHERE p.invokerEmail = :invokerEmail")
	public Portfolio getPortfolioByInvokerEmail(@Param("invokerEmail") String invokerEmail);
	
}
