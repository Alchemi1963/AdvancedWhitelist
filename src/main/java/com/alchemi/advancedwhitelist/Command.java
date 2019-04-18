package com.alchemi.advancedwhitelist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Command implements CommandExecutor {

	File whitelistFile = new File("whitelist.json");
	Gson json = new Gson();
	Type mapType = new TypeToken<List<Map<String, String>>>() {}.getType();
	
	List<Map<String, String>> whitelist = new ArrayList<Map<String, String>>();
	
	public Command() {
		try {
			whitelist = json.fromJson(new FileReader(whitelistFile), mapType);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
	
		if (sender.isOp() || !(sender instanceof Player) || sender.hasPermission("awl.whitelist")) {
			Bukkit.reloadWhitelist();
			if (args.length == 0) show(sender);
			else if (args.length == 1) {
				if (args[0].equals("add")) sender.sendMessage("Usage: /whitelist add <player>");
				else if (args[0].equals("remove")) sender.sendMessage("Usage: /whitelist remove <player>");
				else if (args[0].equals("on")) {
					main.instance.toggleWhitelist(true);
					sender.sendMessage("Whitelist is now on");
					enforce();
				}
				else if (args[0].equals("off")) {
					main.instance.toggleWhitelist(false);
					sender.sendMessage("Whitelist is now off");
				}
				else if (args[0].equals("show") || args[0].equals("list")) show(sender);
				else if (args[0].equals("enforce")) {
					enforce();
					sender.sendMessage("Whitelist is now on");
				}
				else sender.sendMessage("Usage: /whitelist <add|remove|on|off|show|enforce>");
			}
			else if (args.length > 1) {
				
				if (args[0].equals("add")) {
					if (add(args[1])) sender.sendMessage(args[1] + " is now whitelisted.");
					else sender.sendMessage(args[1] + " is already on the whitelist.");
				} else if (args[0].equals("remove")) {
					
					if (remove(args[1])) sender.sendMessage(args[1] + " is no longer whitelisted.");
					else sender.sendMessage(args[1] + " isn't on the whitelist.");
				}
				
			}
			
		}
		
		return true;
	}
	
	public void show(CommandSender sender) {
		
		if (Bukkit.getServer().getWhitelistedPlayers().isEmpty()) {
			sender.sendMessage("No players are whitelisted.");
			return;
		}
		
		sender.sendMessage("Whitelisted players:");
		
		for (OfflinePlayer player : Bukkit.getServer().getWhitelistedPlayers()) {
			sender.sendMessage("    " + player.getName());
		}
		
	}
	
	public boolean add(String name) {
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
		
		if (!Bukkit.getOfflinePlayer(offlineUUID).isWhitelisted()) whitelist.add(new HashMap<String,String>(){
			{
				put("uuid", offlineUUID.toString());
				put("name", name);
			}
		}); else return false;
		
		String newJson = json.toJson(whitelist, mapType);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(whitelistFile));
			writer.write(newJson);
		    writer.close();

		} catch (IOException e) {e.printStackTrace();}
		return true;
	    		
	}
	
	public boolean remove(String name) {
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
		
		if (Bukkit.getOfflinePlayer(offlineUUID).isWhitelisted()) {
			whitelist.remove(new HashMap<String, String>(){{put("uuid", offlineUUID.toString()); put("name", name);}});
		} else return false;
		
		String newJson = json.toJson(whitelist, mapType);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(whitelistFile));
			writer.write(newJson);
		    writer.close();

		} catch (IOException e) {e.printStackTrace();}
		enforce();
		return true;
	}
	
	public void enforce() {	
		
		Bukkit.getServer().reloadWhitelist();
		main.instance.toggleWhitelist(true);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!player.isWhitelisted() && 
					!(player.hasPermission("awl.*") 
							|| player.hasPermission("awl." + Bukkit.getServer().getName()) 
							|| player.isOp())) player.kickPlayer("You are not whitelisted on this server");
		}
		
	}

}
