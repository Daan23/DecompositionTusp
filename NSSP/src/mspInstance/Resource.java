package mspInstance;

import shuntingYard.Location;

public class Resource {
	private int 		capacity; 
	private String 		name; 
	private Location 	location;
	
	
	public Resource(String name, int capacity) {
		this.name = name; 
		this.capacity = capacity; 
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	} 	
}
