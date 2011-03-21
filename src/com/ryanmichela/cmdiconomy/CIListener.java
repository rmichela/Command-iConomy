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
				
				// Does the player have sufficient funds?
				double cost = pc.getValue(re);
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
			}
		}
	}
}
