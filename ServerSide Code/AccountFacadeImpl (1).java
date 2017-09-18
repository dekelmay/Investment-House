package com.SEM.InvestmentHoustSystem;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountFacadeImpl implements AccountFacade{
	@Autowired
	private AccountDao accountDao;
	
	@Override
	@Transactional
	public Account addAccount(Account account) {
		return accountDao.save(account);
	}

	@Override
	@Transactional(readOnly=true)
	public Account getAccount(String email) {
		return accountDao.findOne(email);
	}

	@Override
	@Transactional
	public void updateAccount(Account account) {
		accountDao.save(account);
	}

	@Override
	
	@Transactional(readOnly=true)
	public List<Account> getAllAccounts() {
		return accountDao.findAll();
	}

	@Override
	@Transactional
	public void saveAccounts(List<Account> accounts) {
		accountDao.save(accounts);
	}

}
