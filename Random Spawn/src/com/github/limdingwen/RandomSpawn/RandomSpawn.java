package com.github.limdingwen.RandomSpawn;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RandomSpawn extends JavaPlugin {
	Logger log;
	World world;
	Vector spawnLoc;
	String id;
	Player player = null;
	
	public Permission perms = null;
	
	public Map<String,String> worlds = new HashMap<String, String>();
	public Map<String,Double> locationsx = new HashMap<String, Double>();
	public Map<String,Double> locationsy = new HashMap<String, Double>();
	public Map<String,Double> locationsz = new HashMap<String, Double>();
	
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
	
	public void onEnable() {
		log = this.getLogger();
		
		if (!setupPermissions()) {
			getServer().getPluginManager().disablePlugin(this);
			log.info("RandomSpawn requires Vault & permissions! Plugin disabled to prevent glitches. Download Vault: http://dev.bukkit.org/server-mods/vault");
			return;
		}
	
		log.info("RandomSpawn ready for random spawning!");
	}
	
	private boolean setupPermissions() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		this.perms = ((Permission)rsp.getProvider());
		return this.perms != null;
	}
	
    public void reloadCustomConfig() {
        if (customConfigFile == null) {
        customConfigFile = new File(getDataFolder(), "spawns.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
     
        // Look for defaults in the jar
        InputStream defConfigStream = getResource("spawns.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("setRSpawn")) {
			player = null;
			
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			
			if (player != null) {
				if (args.length == 1) {
					world = player.getWorld();
					id = args[0];
					spawnLoc = player.getLocation().toVector();
				}
				else if (args.length == 4) {
					world = player.getWorld();
					id = args[0];
					spawnLoc = new Vector(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}
				else {
					sender.sendMessage("Incorrect usuage. /setrspawn <id> [x] [y] [z]. Spawn not created.");
				
					return true;
				}
			}
			else {
				if (args.length == 5) {
					world = Bukkit.getServer().getWorld(args[1]);
					id = args[0];
					spawnLoc = new Vector(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
				}
				else {
					sender.sendMessage("Incorrect usuage. In console you use: /setrspawn <id> <World Name> <X> <Y> <Z>. Spawn not created.");

					return true;
				}
			}
			
			// Save worlds
			
			try {
				worlds = (Map) SLAPI.load("Worlds");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Worlds file not detected. Creating file.");
			}
			worlds.put(id, world.getName());
			
			try {
				SLAPI.save(worlds, "Worlds");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Save locations
			// x
			
			try {
				locationsx = (Map) SLAPI.load("Locationsx");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Locationsx file not detected. Creating file.");
			}
			locationsx.put(id, spawnLoc.getX());
			
			try {
				SLAPI.save(locationsx, "Locationsx");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// y
			
			try {
				locationsy = (Map) SLAPI.load("Locationsy");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Locationsy file not detected. Creating file.");
			}
			locationsy.put(id, spawnLoc.getY());
			
			try {
				SLAPI.save(locationsy, "Locationsy");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// z
			
			try {
				locationsz = (Map) SLAPI.load("Locationsz");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Locationsz file not detected. Creating file.");
			}
			locationsz.put(id, spawnLoc.getZ());
			
			try {
				SLAPI.save(locationsz, "Locationsz");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
									
			return true;
		}
		else if (command.getName().equalsIgnoreCase("listrspawns")) {
			 
		}
		
		return false;
	}
	
	public void onDisable() {
		log.info("RandomSpawn disabled.");
	}
}
