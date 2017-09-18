package com.SEM.InvestmentHoustSystem;



import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNTS")
public class Account implements Serializable{

	private static final long serialVersionUID = 1L;
	public enum AccountType {LEAD, INVESTOR, MANAGER};
	private String email;
	private AccountType type;
	private String name;
	private String password;
	private Double balance;
	private Long portfolioId;
	
	public Account() {
	}
	
	/**Lead Constructor*/
	public Account(String name,Double balance,String email){
		setName(name);
		setBalance(balance);
		setType(AccountType.INVESTOR);
		setEmail(email);

	}
	
	public Account(AccountType type, String name, Double balance) {
		setType(type);
		setName(name);
		setBalance(balance);
	}
	
	public Account(AccountType type, String email, String name, Double balance, String password) {
		setType(type);
		setName(name);
		setBalance(balance);
		setPassword(password);
	}
	
	@Id
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public AccountType getType() {
		return type;
	}
	
	public void setType(AccountType type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
	
	@Override
	public String toString() {
		return "Account [email=" + email + ", type=" + type + ", name=" + name + ", password=" + password + ", balance="
				+ balance + ", portfolioId=" + portfolioId + "]";
	}

}
