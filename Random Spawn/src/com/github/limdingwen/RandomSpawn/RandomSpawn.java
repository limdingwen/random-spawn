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

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.avaje.ebeaninternal.server.persist.BindValues.Value;

public class RandomSpawn extends JavaPlugin implements Listener {
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
			log.warning("RandomSpawn requires Vault & permissions! Plugin disabled to prevent glitches. Download Vault: http://dev.bukkit.org/server-mods/vault");
			return;
		}
		
		getServer().getPluginManager().registerEvents(this, this);
	
		log.info("RandomSpawn ready for random spawning! Made by limdingwen, idea by vasil7112.");
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
				log.warning("Saves file not detected. Creating file.");
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
				log.warning("Oh no! Something went wrong. We cannot find the file. Post a ticket. Aborting.");
				
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
				log.warning("There is no file!");
				sender.sendMessage("No spawns detected. Aborting.");
				
				return true;
			}
			
			ArrayList<Spawn> vlist = new ArrayList<Spawn>(spawns.values());
			ArrayList<String> klist = new ArrayList<String>(spawns.keySet());
						
			if (klist.size() != 0) {
				sender.sendMessage("Existing spawns:");
				
				for (int i = 0; i < klist.size(); i++) {
					sender.sendMessage(klist.get(i) + " (" + vlist.get(i).world + ")");
				}
			}
			else {
				sender.sendMessage("There are currently no existing spawns.");
			}
			
			return true;
		}
		else if (command.getName().equalsIgnoreCase("checkRSpawn")) {
			// Load
			
			if (args.length != 1) {
				sender.sendMessage("Incorrect usuage. Use /checkRSpawn <id>. Spawn not checked.");
				
				return true;
			}
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.warning("There is no file!");
				sender.sendMessage("No spawn data detected. Aborting.");
				
				return true;
			}
			
			if (spawns.containsKey(args[0])) {
				sender.sendMessage("Info about " + args[0] + ":");
				sender.sendMessage("World: " + spawns.get(args[0]).world);
				sender.sendMessage("Location: X:" + spawns.get(args[0]).x + " Y:" + spawns.get(args[0]).y + " Z:" + spawns.get(args[0]).z);
			}
			else {
				sender.sendMessage("Sorry, I cannot find that spawn to check the info.");
				
				return true;
			}
				
			return true;
		}
		else if (command.getName().equalsIgnoreCase("spawn")) {
			// Load
			
			Player player;
			
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				sender.sendMessage("This can only be run in a client!");
				
				return true;
			}
			
			String id = null;
			
			if (args.length == 0) {
				id = null;
			}
			else if (args.length == 1) {
				id = args[0];
			}
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.warning("There is no file!");
				sender.sendMessage("No spawn data detected. Aborting.");
				
				return true;
			}
			
			if (spawns.size() > 0) {
				if (id == null) {
					ArrayList<Spawn> vlist = new ArrayList<Spawn>(spawns.values());
					
					int randomNumber = (int) Math.floor(Math.random() * vlist.size());
					
					player.teleport(new Location(Bukkit.getServer().getWorld(vlist.get(randomNumber).world), vlist.get(randomNumber).x, vlist.get(randomNumber).y, vlist.get(randomNumber).z));
				}
				else {
					if (spawns.containsKey(id)) {
						player.teleport(new Location(Bukkit.getServer().getWorld(spawns.get(id).world), spawns.get(id).x, spawns.get(id).y, spawns.get(id).z));
					}
					else {
						sender.sendMessage("Sorry, there is no spawn with the id " + id + ".");
						
						return true;
					}
				}
			}
			else {
				sender.sendMessage("There is no spawn data! Aborted as it is impossible to teleport without spawn data.");
				
				return true;
			}
			
			sender.sendMessage("Woosh!");
			
			if (id != null) {
				log.info(player.getName() + " teleported to " + id + ".");
			}
			else {
				log.info(player.getName() + " teleported to a random spawn.");
			}
			
			return true;
		}
		else if (command.getName().equalsIgnoreCase("removerspawn")) {
			// Load
			
			if (args.length != 1) {
				sender.sendMessage("Incorrect usuage. Use /removeRSpawn <id>. Spawn not removed.");
				
				return true;
			}
			
			try {
				spawns = (Map) SLAPI.load("Spawns");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.warning("There is no file!");
				sender.sendMessage("No spawn data detected. Aborting.");
				
				return true;
			}
			
			if (spawns.containsKey(args[0])) {
				spawns.remove(args[0]);
				
				try {
					SLAPI.save(spawns, "Spawns");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					sender.sendMessage("Failed to delete spawn as an error occured in saving the file.");
					e.printStackTrace();
				}
				
				sender.sendMessage("Sucess! Spawn " + args[0] + " was removed from the database.");
			}
			else {
				sender.sendMessage("Sorry, I cannot find that spawn to remove.");
				
				return true;
			}
				
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event) {
		LoginRespawnSharing(event.getPlayer());
	}
	
	public void LoginRespawnSharing(final Player sharedPlayer) {
		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					spawns = (Map) SLAPI.load("Spawns");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.warning("There is no spawn file detected, so " + sharedPlayer.getName() + " is not affected by RandomSpawn. Stacktrace:");
					e.printStackTrace();
					sharedPlayer.sendMessage("Sorry, due to an internal server error RandomSpawn cannot work properly, and you are now using the default spawning system. Please contact the server admins for more details.");
					
					return;
					
				}
				
				if (spawns.size() > 0) {
					ArrayList<Spawn> vlist = new ArrayList<Spawn>(spawns.values());
					
					int randomNumber = (int) Math.floor(Math.random() * vlist.size());
					sharedPlayer.teleport(new Location(Bukkit.getServer().getWorld(vlist.get(randomNumber).world), vlist.get(randomNumber).x, vlist.get(randomNumber).y, vlist.get(randomNumber).z));
				}
				else {
					log.warning("No data found in spawn file, player not teleported.");
					sharedPlayer.sendMessage("Sorry, due to an internal server error RandomSpawn cannot work properly, and you are now using the default spawning system. Please contact the server admins for more details.");
				}
				
				log.info("Sucessfully sent " + sharedPlayer.getName() + " to a spawn point.");
				sharedPlayer.sendMessage("You have spawned randomly by Random Spawn.");
			}
		}, 10L);
	}
	
	@EventHandler
	public void PlayerRespawnEvent(PlayerRespawnEvent event) {
		LoginRespawnSharing(event.getPlayer());
	}
	
	public void onDisable() {
		
		log.info("RandomSpawn disabled safely.");
	}
}
