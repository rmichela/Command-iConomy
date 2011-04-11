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
	
	private Map<String, MatchExpression> prices = new HashMap<String, MatchExpression>();
	
	public PriceCache(File f) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		while((line = reader.readLine()) != null) {
			if(line.startsWith("#")) continue;
			
			String[] parts = line.split(":");			
			if(parts.length != 2) continue;
			
			MatchExpression me = new MatchExpression();
			me.re = parts[0];
			
			String[] costParts = parts[1].trim().split(" ");
			
			me.cost = Double.parseDouble(costParts[0]);
			if(costParts.length == 2) {
				me.cooldown = Integer.parseInt(costParts[1]);
			} else {
				me.cooldown = 0;
			}
			
			prices.put(me.re, me);
		}
		reader.close();
	}
	
	public Set<String> getExpressions() {
		return prices.keySet();
	}
	
	public double getCost(String expression) {
		return prices.get(expression).cost;
	}
	
	public int getCooldown(String expression) {
		return prices.get(expression).cooldown;
	}
	
	private class MatchExpression {
		public String re;
		public double cost;
		public int cooldown;
	}
}
