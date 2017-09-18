package com.SEM.InvestmentHoustSystem;



import java.io.IOException;
import java.util.ArrayList;


public interface InvestmentHouseFacad {
	
	public ClientServerRequest login(String email, String password) throws IOException, ClassNotFoundException;
    public Account signup(Account account);
    public ArrayList<AnalyzedStock> getAllStocks();
    public ArrayList<Request> getAccountsRequests(Account account);
    public void sendRequest(Request request);
    public Portfolio getPortfolioByInvoker(Account account);
    public void logOut();
    public Account getAccountByEmail(String email);
    public AnalyzedStock StockDetailsToAanalyzedStock(StockDetails s);
    public ArrayList<AnalyzedStock> getAllStocksOffline();
}
