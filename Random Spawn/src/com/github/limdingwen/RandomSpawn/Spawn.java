package com.github.limdingwen.RandomSpawn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.util.Vector;

public class Spawn implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	/**
	 * Always treat de-serialization as a full-blown constructor, by
	 * validating the final state of the de-serialized object.
   */
   private void readObject(
     ObjectInputStream aInputStream
   ) throws ClassNotFoundException, IOException {
     //always perform the default de-serialization first
     aInputStream.defaultReadObject();
  }

    /**
    * This is the default implementation of writeObject.
    * Customise if necessary.
    */
    private void writeObject(
      ObjectOutputStream aOutputStream
    ) throws IOException {
      //perform the default serialization for all non-transient, non-static fields
      aOutputStream.defaultWriteObject();
    }
}
