package com.SEM.InvestmentHoustSystem;
/**
 * Created by roie on 27/05/2017.
 */
public class AnalyzedStock extends Stock{
    /**
	 * 
	 */
	private static final long serialVersionUID = -9022031691558041181L;
	private double openingPrice;
    private double turnOver;
    private float change;

    public AnalyzedStock(String id,  double quote, String name, double turnOver, float change, double openingPrice) {
        super(id,quote,name);
        this.turnOver = turnOver;
        this.change = change;
        this.openingPrice = openingPrice;
    }


    public float getChange() {
        return change;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public double getTurnOver() {
        return turnOver;
    }

    public void setTurnOver(double turnOver) {
        this.turnOver = turnOver;
    }


	public double getOpeningPrice() {
		return openingPrice;
	}


	public void setOpeningPrice(double openingPrice) {
		this.openingPrice = openingPrice;
	}

    @Override
    public String toString() {
        return "AnalyzedStock{" +
                "openingPrice=" + openingPrice +
                ", turnOver=" + turnOver +
                ", change=" + change +
                '}';
    }
}
