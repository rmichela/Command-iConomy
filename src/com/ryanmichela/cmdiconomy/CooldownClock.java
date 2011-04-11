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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class CooldownClock {

	private static Map<Player, Map<String, Calendar>> clocks = new HashMap<Player, Map<String,Calendar>>();
	
	public static void StartTimer(Player player, String re, int cooldown) {
		if(!clocks.containsKey(player)) {
			clocks.put(player, new HashMap<String, Calendar>());
		}
		
		Map<String, Calendar> playerClocks = clocks.get(player);
		Calendar endCal = Calendar.getInstance();
		endCal.add(Calendar.SECOND, cooldown);
		playerClocks.put(re, endCal);
	}
	
	public static boolean TimerExpired(Player player, String re) {
		if(clocks.containsKey(player)) {
			Map<String, Calendar> playerClocks = clocks.get(player);
			if(playerClocks.containsKey(re)) {
				Calendar endCal = playerClocks.get(re);
				Calendar nowCal = Calendar.getInstance();
				if(endCal.compareTo(nowCal) > 0) {
					return false;
				}
			}
		}
		return true;
	}
}
