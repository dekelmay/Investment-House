package com.SEM.InvestmentHoustSystem;



import java.util.List;

import stockexchange.client.StockExchangeClient;


public interface RequestFacad {

	public List<Request> saveAll(Request ...requests);
	public List<Request> getAll();
	public Request getRequestById(String requestId);
	public List<Request> getRequestsByInvokerEmail(String invokerEmail);
	public List<Request> getRequestsByStatus(Request.RequestStatus status);
	public void checkForCompletedRequests(StockExchangeClient sec);
	
}
