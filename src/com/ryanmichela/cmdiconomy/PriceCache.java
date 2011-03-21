package com.ryanmichela.cmdiconomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PriceCache {
	
	private Map<String, Double> prices = new HashMap<String, Double>();
	
	public PriceCache(File f) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		while((line = reader.readLine()) != null) {
			if(line.startsWith("#")) continue;
			String[] parts = line.split(":");
			if(parts.length == 2) {
				prices.put(parts[0], Double.parseDouble(parts[1]));
			}
		}
		reader.close();
	}
	
	public Set<String> getExpressions() {
		return prices.keySet();
	}
	
	public double getValue(String expression) {
		return prices.get(expression);
	}
}
