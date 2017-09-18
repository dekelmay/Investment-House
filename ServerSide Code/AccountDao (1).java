package com.SEM.InvestmentHoustSystem;



import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountDao extends JpaRepository<Account, String>{
	
}