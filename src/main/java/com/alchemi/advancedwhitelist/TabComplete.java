package com.alchemi.advancedwhitelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!sender.hasPermission("awl.whitelist") && !sender.isOp())
			return tabSuggest;
		
		if (args.length == 1) {
			
			list.add("add");
			list.add("remove");
			list.add("on");
			list.add("off");
			list.add("show");
			list.add("enforce");
				
		} else if (args.length == 2 && (args[0].equals("add") || args[0].equals("remove"))) {
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				list.add(p.getName());
			}
			
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}

}
