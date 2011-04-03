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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

public class CIPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Logger log = getServer().getLogger();
		
		// Initialize config directory
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		File pricesFile = new File(getDataFolder(), "prices.yml");			
		if(!pricesFile.exists()) {
			try {
				log.info("[Command iConomy] Populating initial prices file");
				PrintStream out = new PrintStream(new FileOutputStream(pricesFile));
				out.println("# To charge for a command, list a matching regular expression below on its own");
				out.println("# line with the price, separated by a colon. For more info on regular expressions");
				out.println("# see http://www.regular-expressions.info/reference.html");
				out.println();
				out.println("# ^/tp: 10");
				out.close();
			} catch (IOException e) {
				log.severe("[Command iConomy] Error initializing prices file. You're on your own!");
			}
		}
		
		File configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			try {
				log.info("[Command iConomy] Populating initial config file");
				PrintStream out = new PrintStream(new FileOutputStream(configFile));
				out.println("Verbose: false");
				out.println("NoAccountMessage: No bank account.");
				out.println("InsuficientFundsMessage: Insuficent funds.");
				out.println("AccountDeductedMessage: Charged {cost}");
				out.close();
			} catch (IOException e) {
				log.severe("[Command iConomy] Error initializing config file. You're on your own!");
			}
		}
		
		if(getServer().getPluginManager().getPlugin("iConomy") == null) {
			log.severe("[Command iConomy] Could not find iConomoy!");
		} else {
			try {
				PriceCache pc = new PriceCache(pricesFile);		
				CIListener listener = new CIListener(this, pc);
				getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, listener , Priority.Lowest, this);
				log.info("[Command iConomy] Loaded.");
			} catch (Exception e) {
				log.log(Level.SEVERE, "[Command iConomy] Failed to process prices.config", e);
			}
		}
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
}
