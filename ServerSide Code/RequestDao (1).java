package com.SEM.InvestmentHoustSystem;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestDao extends JpaRepository<Request, String>{
	
	@Query("SELECT r FROM Request as r WHERE r.invokerEmail = :invokerEmail")
	public List<Request> getRequestsByInvokerEmail(@Param("invokerEmail") String invokerEmail);
	
	@Query("SELECT r FROM Request as r WHERE r.status = :status")
	public List<Request> getRequestsByStatus(@Param("status") Request.RequestStatus status);
	
}
