package com.SEM.InvestmentHoustSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stockexchange.client.StockCommandType;
import stockexchange.client.StockExchangeClient;
import stockexchange.client.StockExchangeCommand;


import com.SEM.InvestmentHoustSystem.ClientServerRequest;
import com.SEM.InvestmentHoustSystem.Request.RequestType;
import com.SEM.InvestmentHoustSystem.Account.AccountType;
import com.SEM.InvestmentHoustSystem.ClientServerRequest.ClientServerRequestType;

@Service
public class InvestmentHouse {
	
	@Autowired
	private StockExchangeClient stockMarket;
	@Autowired
	private RequestFacad requestFacad;
	@Autowired
	private PortfolioFacad portfolioFacad;
	@Autowired
	private AccountFacade accountFacade;
	private String stockClientId = "client1";
	private ArrayList<Stock> stocks = new ArrayList<>();	
	
	public InvestmentHouse(StockExchangeClient client) {
		super();
		this.stockMarket = client;
	}

	public void execute() throws Exception {
		
		/*
		String myId = "client1";
		stockMarket.startListening(myId, (o, event)->System.err.println(event));
		*/
		
		stockMarket.getStocksId().forEach(s -> stocks.add(new Stock(stockMarket.getQuote(s))));
		
		
		/** Delete Before Hand in*/
		//################################################################		
		
		for (Stock stock : stocks) {
			Request request = new Request(RequestType.ASK, "", stock.getId(), 0.0, stock.getQuote(), 1, new Date());
			stockMarket.sendCommand(new StockExchangeCommand(StockCommandType.ASK, stockClientId, request.getStockId(), request.getMinPrice(),
					request.getMaxPrice(), request.getAmount()));
		}
		
		stocks.forEach(s -> System.out.println(s));
		
		//################################################################
		
		// handle all users
		new Thread(new Clients()).start();
		
		requestFacad.checkForCompletedRequests(stockMarket);
		
		Timer timer = new Timer(10000 * 6, new ManagerForm());
		timer.start();
	}
	
	private class Clients implements Runnable {

		@Override
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(1234)) {
				while(true) {
					try {
						
						final Socket socket = serverSocket.accept();
						new Thread(new Runnable() {
						
							@Override
							public void run() {
								boolean finished = false;
								System.out.println("Someone Logged In");
								try {
									ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
									ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
									while(true && !finished && socket.isConnected() && !socket.isClosed()) {
														
										ClientServerRequest csr = getRequestsFromTheClient(ois, socket);
										
										switch (csr.getType()) {
											
											case LOGIN: {
												sendAnswersToTheClient(login(csr), oos);
												break;
											}
											case GETALLREQUESTSBYINVOKEREMAIL: {
												csr.setAllRequests(requestFacad.getRequestsByInvokerEmail(csr.getEmail()));
												sendAnswersToTheClient(csr, oos);
												break;
											}	
											case GETALLSTOCKS: {
												csr.setAllStocks(getAllStocks());
												sendAnswersToTheClient(csr, oos);
												break;
											}
											case GETPORTFOLIO: {
												csr.setPortfolio(portfolioFacad.getPortfolioById(csr.getAccount().getPortfolioId()));
												sendAnswersToTheClient(csr, oos);
												break;
											}
											case REQUEST: {
												handleRequests(csr.getRequest());
												break;
											}
											case GETACCOUNT: {
												csr.setAccount(accountFacade.getAccount(csr.getEmail()));
												sendAnswersToTheClient(csr, oos);
												break;
											}
											case SIGNUP: {
												csr = signup(csr);
												sendAnswersToTheClient(csr, oos);
												break;
											}
											case FINISHED: {
												oos.close();
												ois.close();
												socket.close();
												finished = true;
												break;
											}
							
											default:
												break;
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										socket.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}

						}).start();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private ClientServerRequest signup(ClientServerRequest csr) {
		Account acc = accountFacade.getAccount(csr.getAccount().getEmail());
		
		if (acc != null)
			csr.setAccount(null);
		else {
			csr.setPortfolio(portfolioFacad.saveAll(new Portfolio(csr.getAccount().getEmail())).get(0));
			csr.getAccount().setPortfolioId(csr.getPortfolio().getPortfolioId());
			csr.getAccount().setType(AccountType.INVESTOR);
			csr.setAccount(accountFacade.addAccount(csr.getAccount()));
			csr.setAllStocks(stocks);
		}
		
		return csr;
	}
	
	private void handleRequests(Request request) {
		StockExchangeCommand sec = null;
		switch (request.getType()) {
			case BID: {
				sec = new StockExchangeCommand(StockCommandType.BID, stockClientId, request.getStockId(), request.getMinPrice(),
											request.getMaxPrice(), request.getAmount());
				break;
			}
			case ASK: {
				sec = new StockExchangeCommand(StockCommandType.ASK, stockClientId, request.getStockId(), request.getMinPrice(),
						request.getMaxPrice(), request.getAmount());
				break;
			}
			default: {
				break;
			}
		}
		
		if (sec != null) {
			request.setRequestId(stockMarket.sendCommand(sec));
			requestFacad.saveAll(request);
			chargeCommision(request);
		}
	}
	
	private void chargeCommision(Request request) {
		Account account = accountFacade.getAccount(request.getInvokerEmail());
		Portfolio portfolio = portfolioFacad.getPortfolioById(account.getPortfolioId());
		account.setBalance(account.getBalance() - (request.getMaxPrice() * request.getAmount()) * portfolio.getCommissionPercentage());
		accountFacade.updateAccount(account);
	}
	
	public ArrayList<Stock> getAllStocks() {
		List<String> stocksIds = stockMarket.getStocksId();
		ArrayList<Stock> allStocks = new ArrayList<>();
		stocksIds.forEach(s -> allStocks.add(new Stock(stockMarket.getQuote(s))));
		return allStocks;
	}
	
	public ClientServerRequest login(ClientServerRequest csr) {
		Account account = accountFacade.getAccount(csr.getEmail());
		if(account != null && account.getPassword().equals(csr.getPassword())) {
			csr.setAccount(account);
			csr.setPortfolio(portfolioFacad.getPortfolioByInvokerEmail(csr.getEmail()));
			csr.setAllStocks(stocks);
		}
		else
			csr.setAccount(null);
		
		return csr;
	}
	
	private void sendAnswersToTheClient(ClientServerRequest csr, ObjectOutputStream oos) {
		try {
			oos.writeObject(csr);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ClientServerRequest getRequestsFromTheClient(ObjectInputStream ois, Socket socket) {
		ClientServerRequest csr = null;
		while(csr == null && socket.isConnected() && !socket.isClosed()) {
			try {
				csr = (ClientServerRequest) ois.readObject();
			} catch (Exception e) {
				csr = new ClientServerRequest(ClientServerRequestType.FINISHED);
			} 
		}
		return csr;
	}
	
	private class ManagerForm implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			StringBuilder stringBuilder = new StringBuilder();
			
			stringBuilder.append("\n" + new Date().toString() + "\n");
			stringBuilder.append("\nThe number of users signed to the system is : " + accountFacade.getAllAccounts().size() + "\n");
			stringBuilder.append("\nAll the requests in the system : " + "\n");

			ArrayList<Request> requests = (ArrayList<Request>)requestFacad.getAll();
			
			double totalProfit = 0;
			for (Request request : requests) {
				stringBuilder.append(request.toString() + "\n");
				totalProfit += request.getAmount() * request.getMaxPrice() * portfolioFacad.getPortfolioByInvokerEmail(request.getInvokerEmail()).getCommissionPercentage();
			}
			
			stringBuilder.append("\nTotal Investment house profit is : $" + totalProfit + "\n");
			MessgingSystem.send("100plus.manager@gmail.com", "Investment House Report", stringBuilder.toString());
		}
		
	} 
	
	
}