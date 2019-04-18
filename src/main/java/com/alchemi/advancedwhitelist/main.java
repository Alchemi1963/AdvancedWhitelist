package com.alchemi.advancedwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener{

	public FileConfiguration conf;
	public static main instance;
	public static boolean enabled = true;
	
	@Override
	public void onEnable() {
		instance = this;
		conf = getConfig();
		conf.addDefault("enableWhitelist", enabled);
		if (!conf.contains("enableWhitelist")) conf.set("enableWhitelist", enabled);
		enabled = conf.getBoolean("enableWhitelist");
		saveConfig();
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginCommand("whitelist").setExecutor(new Command());
		getServer().getPluginCommand("whitelist").setTabCompleter(new TabComplete());
		
		
	}
	
	public void toggleWhitelist(boolean enable) {
		
		main.enabled = enable;
		conf.set("enableWhitelist", enable);
		saveConfig();
		
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerLoginEvent e) {
		if (!main.enabled) return;
		
		Bukkit.getServer().reloadWhitelist();
		
		if (!e.getPlayer().isWhitelisted() && !(e.getPlayer().hasPermission("awl.*") 
				|| e.getPlayer().hasPermission("awl." + Bukkit.getServer().getName()) 
				|| e.getPlayer().isOp())) {
			e.setKickMessage("You are not whitelisted on this server.");
			e.setResult(Result.KICK_WHITELIST);
		}
		
	}
	
}
