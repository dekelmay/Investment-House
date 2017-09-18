package com.SEM.InvestmentHoustSystem;

import java.io.Serializable;

/**
 * @author Almogs
 *
 */
public class Stock implements Serializable {

	private static final long serialVersionUID = 6L;
	private String id;
	private String name;
	private double Quote;
	
	public Stock(stockexchange.client.Stock stock) {
		setId(stock.getId());
		setName(stock.getName());
		setQuote(stock.getQuote());
	}

	public Stock(String id, double quote, String name) {
		setId(id);
		setName(name);
		setQuote(quote);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getQuote() {
		return Quote;
	}

	public void setQuote(double quote) {
		Quote = quote;
	}

	@Override
	public String toString() {
		return "Stock [id=" + id + ", name=" + name + ", Quote=" + Quote + "]";
	}
	
}
