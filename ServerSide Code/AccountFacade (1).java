package com.SEM.InvestmentHoustSystem;



import java.util.List;

public interface AccountFacade {
	//CRU
	public Account addAccount(Account account);
	public Account getAccount(String email);
	public void updateAccount(Account account);
	public List<Account> getAllAccounts();
	public void saveAccounts(List<Account> accounts);
}
