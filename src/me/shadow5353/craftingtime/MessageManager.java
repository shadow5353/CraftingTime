package me.shadow5353.craftingtime;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageManager {

	private MessageManager() { }

	private static MessageManager instance = new MessageManager();

	public static MessageManager getInstance() {
		return instance;
	}

	private String prefix = ChatColor.BLACK + "[" + ChatColor.DARK_RED + "CraftingTime" + ChatColor.BLACK + "] ";

	public void info(CommandSender s, String msg) {
		msg(s, ChatColor.YELLOW, msg);
	}
	public void cmd(CommandSender s, String msg) {
		msg(s, ChatColor.GOLD, msg);
	}
	public void g(CommandSender s, String msg) {
		msg(s, ChatColor.GRAY, msg);
	}

	public void error(CommandSender s, String msg) {
		msg(s, ChatColor.RED, msg);
	}

	public void good(CommandSender s, String msg) {
		msg(s, ChatColor.GREEN, msg);
	}
	
	public void perm(CommandSender s) {
		msg(s, ChatColor.RED, "You do not have permission");
	}
	
	public void notdone(CommandSender s) {
		msg(s, ChatColor.RED, "This feature is not yet implemented");
	}

	private void msg(CommandSender s, ChatColor color, String msg) {
		s.sendMessage(prefix + color + msg);
	}
}