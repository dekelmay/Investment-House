package com.SEM.InvestmentHoustSystem;



import java.util.List;

public interface StockDetailsFacad {
	
	public List<StockDetails> saveAll(StockDetails ...stockDetails);
	public List<StockDetails> getAllStockDetailsByInvokerEmail(String invokerEmail);

}
