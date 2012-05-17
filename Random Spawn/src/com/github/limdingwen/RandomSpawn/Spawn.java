package com.github.limdingwen.RandomSpawn;

import org.bukkit.World;
import org.bukkit.util.Vector;

public class Spawn {
	public Spawn(World world, Vector location) {
		super();
		this.world = world;
		this.location = location;
	}
	World world;
	Vector location;
	public World getWorld() {
		return world;
	}
	public void setWorld(World world) {
		this.world = world;
	}
	public Vector getLocation() {
		return location;
	}
	public void setLocation(Vector location) {
		this.location = location;
	}
}
