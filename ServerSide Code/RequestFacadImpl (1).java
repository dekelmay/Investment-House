package com.SEM.InvestmentHoustSystem;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SEM.InvestmentHoustSystem.Request.RequestStatus;

import stockexchange.client.StockExchangeClient;
import stockexchange.client.StockExchangeTransaction;

@Service
public class RequestFacadImpl implements RequestFacad {

	@Autowired
	private RequestDao requestDao;
	@Autowired
	private StockDetailsDao stockDetailsDao;
	@Autowired
	private AccountDao accountDao;

	@Override
	@Transactional
	public List<Request> saveAll(Request... requests) {
		return requestDao.save(Arrays.asList(requests));
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getAll() {
		return requestDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Request getRequestById(String requestId) {
		return requestDao.findOne(requestId);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getRequestsByInvokerEmail(String invokerEmail) {
		return requestDao.getRequestsByInvokerEmail(invokerEmail);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Request> getRequestsByStatus(Request.RequestStatus status) {
		return requestDao.getRequestsByStatus(status);
	}

	@Override
	public void checkForCompletedRequests(StockExchangeClient sec) {
		new Thread(new checkForCompletedRequestsAtTheStockMarket(sec)).start();
	}
	
	
	private class checkForCompletedRequestsAtTheStockMarket implements Runnable {

		private StockExchangeClient stockMarket;
		
		public checkForCompletedRequestsAtTheStockMarket(StockExchangeClient sec) {
			this.stockMarket = sec;
		}
		
		@Override
		public void run() {
			while(true) {
				List<Request> requests = getAll();
				for (Request request : requests) {
					if (request.getStatus() == RequestStatus.PENDING) {
						List<StockExchangeTransaction> lst = stockMarket.getTransactionsForCommand(request.getRequestId());
						if(!lst.isEmpty()) {
							Account account = accountDao.findOne(request.getInvokerEmail());
							ArrayList<StockDetails> stocks = (ArrayList<StockDetails>) stockDetailsDao.getAllStockDetailsByInvokerEmail(account.getEmail());
							
							StockDetails sd = null;
							for (StockDetails stockDetails : stocks) {
								if (stockDetails.getStockId().equals(request.getStockId())){
									sd = stockDetails;
									break;
								}
							}
									
							System.out.println(lst.get(0).getActualAmount() * lst.get(0).getActualPrice());
							
							double counter = 0 ;
							double price;
							for (StockExchangeTransaction stockExchangeTransaction : lst)
								counter += stockExchangeTransaction.getActualPrice() * stockExchangeTransaction.getActualAmount();

							if(sd != null) {
								if (request.getType().ordinal() == Request.RequestType.ASK.ordinal()) {
									sd.setAmount(sd.getAmount() - request.getAmount());
									account.setBalance(account.getBalance() + counter);
								}
								else {
									sd.setAmount(sd.getAmount() + request.getAmount());
									account.setBalance(account.getBalance() - counter);
								}
								price = (sd.getPurchasePrice() * sd.getAmount() + counter) / (sd.getAmount() + request.getAmount());
								sd.setPurchasePrice(price);
								stockDetailsDao.save(sd);
							}
							else {
								account.setBalance(account.getBalance() - counter);
								price = counter / request.getAmount(); 
								stockDetailsDao.save(new StockDetails(request.getStockId(), price , request.getAmount(), request.getInvokerEmail()));
							}	
							
							request.setStatus(Request.RequestStatus.COMPLETED);
							request.setPurchasePrice(price);
							accountDao.save(account);
							requestDao.save(request);
						}
					}
				}	
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
