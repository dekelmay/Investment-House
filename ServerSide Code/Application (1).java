package com.SEM.InvestmentHoustSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import stockexchange.client.StockExchangeClient;
import stockexchange.client.StockExchangeClientImplementation;

@SpringBootApplication
public class Application implements CommandLineRunner{
	
    @Autowired
	private InvestmentHouse investmentHouse;
	
	@Bean
	public StockExchangeClient client(){
		return new StockExchangeClientImplementation();
	}
	
	@Bean
	@Autowired
	public InvestmentHouse mainDemo(StockExchangeClient client){
		return new InvestmentHouse(client);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			investmentHouse.execute();
		} catch (ClassNotFoundException e) {
	
		}		
	}
}
