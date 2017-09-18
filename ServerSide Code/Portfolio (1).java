package com.SEM.InvestmentHoustSystem;



import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="Portfolio")
public class Portfolio implements Serializable{

	private static final long serialVersionUID = 3L;
	private Long portfolioId;
	private ArrayList<StockDetails> investorStocks = new ArrayList<>();
	private ArrayList<Long> requestsIds = new ArrayList<>();
	private Double commissionPercentage = new Double(0.0035);
	private String InvokerEmail;
	
	public Portfolio() {
	}
	
	public Portfolio(String email) {
		setInvokerEmail(email);
	}
	
	@Id
	@GeneratedValue
	@Column(name="Portfolio_Id")
	public Long getPortfolioId() {
		return portfolioId;
	}
	
	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
	
	@Transient
	public ArrayList<StockDetails> getInvestorStocks() {
		return investorStocks;
	}
	
	public void setInvestorStocks(ArrayList<StockDetails> investorStocks) {
		this.investorStocks = investorStocks;
	}
	
	@Transient
	public ArrayList<Long> getRequestsIds() {
		return requestsIds;
	}
	
	public void setRequestsIds(ArrayList<Long> requests) {
		this.requestsIds = requests;
	}

	@Column(name="Commission_Percentage")
	public Double getCommissionPercentage() {
		return commissionPercentage;
	}

	public void setCommissionPercentage(Double commissionPercentage) {
		this.commissionPercentage = commissionPercentage;
	}

	@Column(name="Invoker_Email")
	public String getInvokerEmail() {
		return InvokerEmail;
	}

	public void setInvokerEmail(String invokerEmail) {
		InvokerEmail = invokerEmail;
	}
	
	@Override
	public String toString() {
		return "Portfolio [portfolioId=" + portfolioId + ", investorStocks=" + investorStocks + ", requestsIds="
				+ requestsIds + ", commissionPercentage=" + commissionPercentage + ", InvokerEmail=" + InvokerEmail
				+ "]";
	}
	
}
