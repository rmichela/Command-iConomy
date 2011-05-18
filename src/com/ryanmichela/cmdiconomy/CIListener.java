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

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

import com.iConomy.*;

public class CIListener extends PlayerListener {

	private CIPlugin plugin;
	private PriceCache pc;
	
	public CIListener(CIPlugin plugin, PriceCache pc) {
		this.plugin = plugin;
		this.pc = pc;
		
		if(verbose()) {
			log().info("[Command iConomy] Verbose mode enabled.");
		}
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		chargePlayerForCommand(event);
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		chargePlayerForCommand(event);
	}
	
	private void chargePlayerForCommand(PlayerChatEvent event) {
	
		if(event.getPlayer() == null) return;
		
		try {
			// Is the player exempt by permission?
			if(iConomy.getPermissions().has(event.getPlayer(), "CommandIConomy.Free")) return;
		} catch (NoClassDefFoundError e) {
			// Permissions not installed, fall back to ops
			if(event.getPlayer().isOp()) return;
		}
		
		// Is this command one we are charging for?	
		for(String re : pc.getExpressions()) {
			
			Pattern p = Pattern.compile(re, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(event.getMessage());
			if(m.find()) {
				
				// Is this command currently cooling down?
				if(!CooldownClock.TimerExpired(event.getPlayer(), re)) {
                                             event.getPlayer().sendMessage(ChatColor.RED +"This function in still on a cooldown.");
					event.setCancelled(true);
					return;
				}

				// Does the player have an account?
				String pName = event.getPlayer().getName();
				if(!iConomy.hasAccount(pName)) {
					String msg = config().getString("NoAccountMessage", "No bank account.");
					if(verbose()) {
						log().info("[Command iConomy] " + event.getPlayer().getName() + " does not have a bank account ?!");
					}
					event.getPlayer().sendMessage(ChatColor.RED + msg);
					event.setCancelled(true);
					return;
				}
				
				// Does the command have a cost of zero?
				double cost = pc.getCost(re);
				if(cost == 0f) return;
				
				// Does the player have sufficient funds?
				if(!(iConomy.getAccount(pName).getHoldings().balance() >= cost)) {
					String msg = config().getString("InsuficientFundsMessage", "Insuficent funds. {cost} needed.");
					msg = msg.replaceAll("\\{cost\\}", iConomy.format(cost));
					if(verbose()) {
						log().info("[Command iConomy] " + event.getPlayer().getName() + " has insuficent funds to invoke " + event.getMessage());
					}
					event.getPlayer().sendMessage(ChatColor.RED + msg);
					event.setCancelled(true);
					return;
				}
				
				///////////////////////////////////////////////////
				
				// All checks passed - deduct funds
				iConomy.getAccount(pName).getHoldings().subtract(cost);
				String msg = config().getString("AccountDeductedMessage", "Charged {cost}");
				if(verbose()) {
					log().info("[Command iConomy] " + event.getPlayer().getName() + " was charged " + iConomy.format(cost) + " for " + event.getMessage());
				}
				msg = msg.replaceAll("\\{cost\\}", iConomy.format(cost));
				event.getPlayer().sendMessage(ChatColor.GREEN + msg);
				
				// Start the cooldown timer
				CooldownClock.StartTimer(event.getPlayer(), re, pc.getCooldown(re));
				
				// If there is a pay to account, make a payment
				String payTo = config().getString("PayTo");
				if(payTo != null) {
					try {
						iConomy.getAccount(payTo).getHoldings().add(cost);
						if(verbose()) {
							log().info("[Command iConomy] " + payTo + " was credited " + iConomy.format(cost));
						}
					} catch (NullPointerException e) {
						log().severe("[Command iConomy] Cannot deposit funds into the account " + payTo + " because it does not exist!");
					}
				}
				return;
			}
		}	
	}

	private boolean verbose() {
		return plugin.getConfiguration().getBoolean("Verbose", false);
	}
	
	private Configuration config() {
		return plugin.getConfiguration();
	}
	
	private Logger log() {
		return plugin.getServer().getLogger();
	}
}
