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
		
		File configFile = new File(getDataFolder(), "prices.yml");			
		if(!configFile.exists()) {
			try {
				log.info("[Command iConomy] Populating initial config file");
				PrintStream out = new PrintStream(new FileOutputStream(configFile));
				out.println("# To charge for a command, list a matching regular expression below on its own");
				out.println("# line with the price, separated by a colon. For more info on regular expressions");
				out.println("# see http://www.regular-expressions.info/reference.html");
				out.println();
				out.println("# ^/tp: 10");
				out.close();
			} catch (IOException e) {
				log.severe("[Command iConomy] Error initializing config file. You're on your own!");
			}
		}
		
		if(getServer().getPluginManager().getPlugin("iConomy") == null) {
			log.severe("[Command iConomy] Could not find iConomoy!");
		} else {
			try {
				PriceCache pc = new PriceCache(configFile);		
				CIListener listener = new CIListener(this, pc);
				getServer().getPluginManager().registerEvent(Type.PLAYER_COMMAND_PREPROCESS, listener , Priority.Normal, this);
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
