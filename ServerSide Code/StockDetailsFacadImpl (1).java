package com.SEM.InvestmentHoustSystem;



import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockDetailsFacadImpl implements StockDetailsFacad{

	@Autowired
	private StockDetailsDao stockDetailsDao;
	
	@Override
	public List<StockDetails> saveAll(StockDetails... stockDetails) {
		return stockDetailsDao.save(Arrays.asList(stockDetails));
	}

	@Override
	public List<StockDetails> getAllStockDetailsByInvokerEmail(String invokerEmail) {
		return stockDetailsDao.getAllStockDetailsByInvokerEmail(invokerEmail);
	}
	
}
