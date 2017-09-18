package com.SEM.InvestmentHoustSystem;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Request")
public class Request implements Serializable{

	private static final long serialVersionUID = 4L;
	public enum RequestType {BID, ASK, REQUESTDATA};
	public enum RequestStatus {PENDING, COMPLETED, CANCELED};
	private RequestType type;
	private Long requestId;
	private String invokerEmail;
	private String stockId;
	private Double minPrice;
	private Double maxPrice;
	private Double purchasePrice;
	private Integer amount;
	private Date timeStamp;
	private RequestStatus status = RequestStatus.PENDING;
	
	public Request() {
	}
	
	public Request(RequestType type , String email,String stockId, Double minPrice, Double maxPrice,Integer amount, Date timeStamp) {
		setType(type);
		setInvokerEmail(email);
		setStockId(stockId);
		setMaxPrice(maxPrice);
		setMinPrice(minPrice);
		setAmount(amount);
		setTimeStamp(timeStamp);
	}
	
	@Id
	@Column(name="Request_Id")
	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
	
	@Column(name="Request_Type")
	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	@Column(name="Stock_Id")
	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	@Column(name="Minimum_Price")
	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}
	
	@Column(name="Maximum_Price")
	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Column(name="Amount_Of_Shares")
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	@Column(name="Invoker_Email")
	public String getInvokerEmail() {
		return invokerEmail;
	}

	public void setInvokerEmail(String invokerEmail) {
		this.invokerEmail = invokerEmail;
	}

	@Column(name="Request_Status")
	public RequestStatus getStatus() {
		return status;
	}

	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	@Column(name="Purchase_Price")
	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Column(name="Time_Stamp")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "Request [type=" + type + ", requestId=" + requestId + ", invokerEmail=" + invokerEmail + ", stockId="
				+ stockId + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + ", purchasePrice=" + purchasePrice
				+ ", amount=" + amount + ", timeStamp=" + timeStamp + ", status=" + status + "]";
	}
	
}
