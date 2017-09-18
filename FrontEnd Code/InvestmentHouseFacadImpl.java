package com.SEM.InvestmentHoustSystem;




import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvestmentHouseFacadImpl implements InvestmentHouseFacad{

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Map<String, Stock> allStocks = new HashMap<>();
	private ArrayList<AnalyzedStock> offlineStocks;
	public InvestmentHouseFacadImpl() {
		try {
			this.socket = new Socket("localhost", 1234);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ClientServerRequest login(String email, String password) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.LOGIN, email, password);
		sendClientServerRequestToTheInvestmentHouse(csr);
		csr = getAnswersFromInvestmentHouse();
		allStocks = new HashMap<>();
		if(csr.getAccount()!=null)
		csr.getAllStocks().forEach(s -> allStocks.put(s.getId(), s));
		return csr;
	}

	@Override
	public Account signup(Account account) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.SIGNUP, account);
		sendClientServerRequestToTheInvestmentHouse(csr);
		csr = getAnswersFromInvestmentHouse();
		System.out.println(csr.getAccount());
		if(csr.getAccount()!=null)
		csr.getAllStocks().forEach(s -> allStocks.put(s.getId(), s));
		return csr.getAccount();
	}

	@Override
	public ArrayList<AnalyzedStock> getAllStocks() {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.GETALLSTOCKS);
		sendClientServerRequestToTheInvestmentHouse(csr);
		ArrayList<AnalyzedStock> stocks = new ArrayList<>();
		csr = getAnswersFromInvestmentHouse();
		ArrayList<Stock> newStocks = (ArrayList<Stock>) csr.getAllStocks();
		for (Stock s : newStocks) {
			double change = (s.getQuote() - allStocks.get(s.getId()).getQuote());
			double turnOver =(change / allStocks.get(s.getId()).getQuote())*100;
			stocks.add(new AnalyzedStock(s.getId(), s.getQuote(),s.getName().length()>20 ?s.getName().substring(0,20):s.getName(), turnOver, (float)change, allStocks.get(s.getId()).getQuote()));
		}
		offlineStocks = stocks;
		return stocks;
	}
	public ArrayList<AnalyzedStock> getAllStocksOffline(){
		return offlineStocks;
	}

	@Override
	public ArrayList<Request> getAccountsRequests(Account account) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.GETALLREQUESTSBYINVOKEREMAIL, account.getEmail());
		sendClientServerRequestToTheInvestmentHouse(csr);
		return new ArrayList<>(getAnswersFromInvestmentHouse().getAllRequests());
	}

	@Override
	public void sendRequest(Request request) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.REQUEST, request);
		sendClientServerRequestToTheInvestmentHouse(csr);
	}
	
	@Override
	public Portfolio getPortfolioByInvoker(Account account) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.GETPORTFOLIO, account);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return getAnswersFromInvestmentHouse().getPortfolio();
	}
	
	@Override
	public void logOut() {
		try {
			ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.FINISHED);
			sendClientServerRequestToTheInvestmentHouse(csr);
			ois.close();
			oos.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Account getAccountByEmail(String email) {
		ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.GETACCOUNT, email);
		sendClientServerRequestToTheInvestmentHouse(csr);
		return getAnswersFromInvestmentHouse().getAccount();
	}


	@Override
	public AnalyzedStock StockDetailsToAanalyzedStock(StockDetails s) {
		float change = (float) (s.getPurchasePrice() - allStocks.get(s.getStockId()).getQuote());
		double turnOver = (change / allStocks.get(s.getStockId()).getQuote())*100;
		return new AnalyzedStock(s.getStockId(), s.getPurchasePrice(), allStocks.get(s.getStockId()).getName(), turnOver, change, allStocks.get(s.getStockId()).getQuote());
	}

	private synchronized void sendClientServerRequestToTheInvestmentHouse(ClientServerRequest csr) {
		try {
			oos.writeObject(csr);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized ClientServerRequest getAnswersFromInvestmentHouse() {
		ClientServerRequest csr = null;
		while(csr == null) {
			try {
				csr = (ClientServerRequest) ois.readObject();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		return csr;
	}
	
}