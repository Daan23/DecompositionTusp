package mspInstance;

import java.util.ArrayList;

public class Instance {
	private ArrayList<Train> 		trains; 
	
	public Instance() {
		trains 		= new ArrayList<Train>(); 
	}

	public void addTrain(String name, int length) {
		Train t = new Train(name, length); 
		trains.add(t); 
	}

	public int nofTrains() {
		return trains.size();
	}

	public Train getTrain(int index) {
		return trains.get(index); 
	}
	
	public ArrayList<Train> getTrains() {
		return this.trains; 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nof Trains: " + this.nofTrains() + "\n"); 
		for(int i=0; i<this.nofTrains(); i++) {
			sb.append(this.getTrain(i).toString() + "\n"); 
		}
		sb.append("\n"); 
		return sb.toString(); 
	}	

}
