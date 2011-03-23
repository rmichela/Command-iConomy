//Copyright (C) 2011  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
