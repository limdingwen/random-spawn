package com.github.limdingwen.RandomSpawn;

import java.awt.List;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
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

import com.avaje.ebeaninternal.server.persist.BindValues.Value;

public class RandomSpawn extends JavaPlugin {
	Logger log;
	World world;
	Vector spawnLoc;
	String id;
	Player player = null;
	
	public Permission perms = null;
	
	public Map<String,Spawn> spawns = new HashMap<String, Spawn>();
	
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
			
			// Save spawns
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Saves file not detected. Creating file.");
			}
			spawns.put(id, new Spawn(world.getName(), spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()));
			
			try {
				SLAPI.save(spawns, "Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			}
			
			// Confirm existence
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Oh no! Something went wrong. We cannot find the file. Post a ticket. Aborting.");
				
				return true;
			}
			
			sender.sendMessage("You have just created spawn " + id + " with coordinates of " + spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ() + " (X,Y,Z) in the world " + world.getName() + ".");
			
			if (sender instanceof Player) {
				log.info(sender.getName() + " has just created the spawn " + id + " (" + spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ() + ") in the world " + world.getName() + ".");
			}
			else {
				log.info("The Console has just created the spawn " + id + " (" + spawnLoc.getX() + ", " + spawnLoc.getY() + ", " + spawnLoc.getZ() + ") in the world " + world.getName() + ".");
			}
			
			return true;
		}
		else if (command.getName().equalsIgnoreCase("listRSpawns")) {
			// Load
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("There is no file!");
				sender.sendMessage("No spawns detected. Aborting.");
				
				return true;
			}
			
			ArrayList<Spawn> vlist = new ArrayList<Spawn>(spawns.values());
			ArrayList<String> klist = new ArrayList<String>(spawns.keySet());
			
			sender.sendMessage("Existing spawns:");
			
			for (int i = 0; i < klist.size(); i++) {
				sender.sendMessage(klist.get(i) + " (" + vlist.get(i).world + ")");
			}
		}
		
		return false;
	}
	
	public void onDisable() {
		log.info("RandomSpawn disabled.");
	}
}
