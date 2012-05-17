package com.github.limdingwen.RandomSpawn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.util.Vector;

public class Spawn implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Spawn(String world, Double x, Double y, Double z) {
		super();
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	String world;
	Vector location;
	Double x;
	Double y;
	Double z;
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Double getZ() {
		return z;
	}
	public void setZ(Double z) {
		this.z = z;
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
