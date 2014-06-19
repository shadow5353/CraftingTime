package me.shadow5353.craftingtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CraftingTime extends JavaPlugin implements Listener{
    FileConfiguration config;
    File cfile;
    
	ArrayList<Player> ccooldown = new ArrayList<Player>();
	ArrayList<Player> place = new ArrayList<Player>();
	ArrayList<Player> fur = new ArrayList<Player>();
	ArrayList<Player> furcool = new ArrayList<Player>();
	ArrayList<Player> toggle = new ArrayList<Player>();
	MessageManager msg = MessageManager.getInstance();
	
	HashMap<Player, Integer> ccooldownTime = new HashMap<Player, Integer>();
	HashMap<Player, BukkitRunnable> ccooldownTask = new HashMap<Player, BukkitRunnable>();
	
	public void onEnable(){
		config = getConfig();
		config.options().copyDefaults(true);
		saveDefaultConfig();
		cfile = new File(getDataFolder(), "config.yml");
		
		if(getConfig().getString("metrics-enable").contains("true")){
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				// Failed to submit the stats :-(
			}
		}
		if(getConfig().getString("metrics-enable").contains("false")){
			
		}
		if (getConfig().getString("Auto-update").contains("true")) {
			Updater updater = new Updater(this, 65511, this.getFile(),Updater.UpdateType.DEFAULT, true);
		}
		if (getConfig().getString("Auto-update").contains("false")) {
		}
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
	
	@EventHandler
	public void OperatorJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.isOp()){
			if (getConfig().getString("Auto-update").contains("false")) {
				msg.info(p, "Please help the plugin with enabling metrics in config!");
				return;
			}
		}
	}
	
	@EventHandler
	public void CraftPlace(final BlockPlaceEvent e){
		if(!(e.getBlockPlaced().getType().equals(Material.WORKBENCH))) return;
		
		final Player p = e.getPlayer();
		if(toggle.contains(p)){
			return;
		}
		if(ccooldown.contains(p)){
			msg.error(p, "You can craft agian in " + ccooldownTime.get(p) + " seconds!");
			e.getBlockPlaced().setType(Material.AIR);
			e.getPlayer().getInventory().addItem(new ItemStack(Material.WORKBENCH, 1));
			return;
		}
		
		if(place.contains(p)){
			msg.error(p, "You have already placed a crafting tabel!");
			e.getBlockPlaced().setType(Material.AIR);
			return;
		}
		msg.good(p, "You can now craft in " + getConfig().getInt("craft-time") + " seconds!");
		place.add(p);
		new BukkitRunnable(){
			private int countdown = getConfig().getInt("craft-time");
			@Override
			public void run(){
					countdown--;
					if(countdown == 10){
						msg.info(p, "10 seconds to craft in!");
					}
					if(countdown == 5){
						msg.info(p, "5 seconds back, hurry up!");
					}
					if(countdown == 4){
						msg.info(p, "4..");
					}
					if(countdown == 3){
						msg.info(p, "3..");
					}
					if(countdown == 2){
						msg.info(p, "2..");
					} 
					if(countdown == 1){
						msg.info(p, "1..");
					}
					
					if(countdown == 0){
						cancel();
						e.getBlockPlaced().setType(Material.AIR);
						place.remove(p);
						ccooldown.add(p);
						ccooldownTime.put(p, getConfig().getInt("craft-cooldown"));
		                msg.good(p, "Your crafting tabel have disappeared, you can place a new one in " + getConfig().getInt("craft-cooldown") + " seconds!");
					}
				}
			}.runTaskTimer(this, 20L, 20L);
			
			ccooldownTime.put(p, getConfig().getInt("craft-cooldown"));
	        ccooldownTask.put(p, new BukkitRunnable() {
	        	public void run() {
	        		ccooldownTime.put(p, ccooldownTime.get(p) - 1);
	        		if (ccooldownTime.get(p) == 0) {
	        			ccooldownTime.remove(p);
	        			ccooldownTask.remove(p);
	        			ccooldown.remove(p);
	        			msg.good(p, "You can now place a crafting tabel again!");
	        			cancel();
	        			}
	        		}
	        	});
	        
	        ccooldownTask.get(p).runTaskTimer(this, 20L, 20L);
	        }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(!(sender instanceof Player)){
			msg.error(sender, "Only players can use this command!");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("craft")){
			if(!(p.hasPermission("craftingtime.use"))){
				msg.perm(p);
				return true;
			}
			if(args.length == 0){
				if(p.hasPermission("craftingtime.give")){
					msg.cmd(p, "/c give" + ChatColor.BLACK + " : " + ChatColor.YELLOW + "Gives a crafting tabel");
				}
				if(p.hasPermission("craftingtime.craft")){
					msg.cmd(p, "/c craft" + ChatColor.BLACK + " : " + ChatColor.YELLOW + "Opens a virtuel crafting tabel");
				}
				if(p.hasPermission("craftingtime.toggle")){
					msg.cmd(p, "/c toggle" + ChatColor.BLACK + " : " + ChatColor.YELLOW + "Toggles the crafting tabel");
				}
				if(p.hasPermission("craftingtime.reload")){
					msg.cmd(p, "/c reload" + ChatColor.BLACK + " : " + ChatColor.YELLOW + "Reload the config file");
				}
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("give")){
					if(!(p.hasPermission("craftingtime.give"))){
						msg.perm(p);
						return true;
					}
					PlayerInventory pi = p.getInventory();
		            
		            pi.addItem(new ItemStack(Material.WORKBENCH, 1));
				}
			}
		}
		return true;
	}

}
