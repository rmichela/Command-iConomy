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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.nijiko.coelho.iConomy.iConomy;

public class CIListener extends PlayerListener {

	private CIPlugin plugin;
	private PriceCache pc;
	
	public CIListener(CIPlugin plugin, PriceCache pc) {
		this.plugin = plugin;
		this.pc = pc;
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerChatEvent event) {
	
		if(event.getPlayer() == null) return;
		
		// Is the player exempt by permission?
		if(iConomy.getPermissions().has(event.getPlayer(), "CommandIConomy.Free")) return;
		
		// Is this command one we are charging for?	
		for(String re : pc.getExpressions()) {
			
			Pattern p = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(event.getMessage());
			if(m.find()) {

				// Does the player have an account?
				String pName = event.getPlayer().getName();
				if(!iConomy.getBank().hasAccount(pName)) {
					String msg = plugin.getConfiguration().getString("NoAccountMessage", "No bank account.");
					event.getPlayer().sendMessage(ChatColor.RED + msg);
					event.setCancelled(true);
					return;
				}
				
				// Does the command have a cost of zero?
				double cost = pc.getValue(re);
				if(cost == 0f) return;
				
				// Does the player have sufficient funds?
				if(!(iConomy.getBank().getAccount(pName).getBalance() >= cost)) {
					String msg = plugin.getConfiguration().getString("InsuficientFundsMessage", "Insuficent funds.");
					event.getPlayer().sendMessage(ChatColor.RED + msg);
					event.setCancelled(true);
					return;
				}
				
				// All checks passed - deduct funds
				iConomy.getBank().getAccount(pName).subtract(cost);
				String msg = plugin.getConfiguration().getString("AccountDeductedMessage", "Charged {cost}");
				msg = msg.replaceAll("\\{cost\\}", iConomy.getBank().format(cost));
				event.getPlayer().sendMessage(ChatColor.GREEN + msg);
				
				return;
			}
		}
	}
}
