package shuntingYard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import shuntingYard.Track.TrackType;

/**
 *	This class contains all tracks present within the shunting yard.
 * @author daan.vandenheuvel 
 * 
 */
public class ShuntingYard {
	private HashMap<String, Track> tracks; 
	private HashMap<String, Track> noParkTracks; 
	
	// The tracks below are not contained in the hashmap above. 
	private Track entrance; 			// No route may go to the entrance. 
	private Track exit; 				// No routes may leave from the entrance. 
	private Track inverseExit; 			// Contains the inverse of all edges leading to the exit. 
	private ArrayList<Track> cleaningTracks;
	
	public ShuntingYard() throws Exception {
		this.tracks 		= new HashMap<String, Track>(); 
		this.noParkTracks	= new HashMap<String, Track>(); 
		this.cleaningTracks	= new ArrayList<>(); 
		setEntrance(">"); 
		setExit("->"); 
		setInverseExit("inv"); 
	}
	
	private void setEntrance(String name) throws Exception {
			this.entrance = new Track(name, 0, TrackType.ENTRANCE);
	}

	private void setExit(String name) throws Exception {
			this.exit = new Track(name, Integer.MAX_VALUE, TrackType.EXIT);
	}
	
	private void setInverseExit(String name) throws Exception {
			this.inverseExit = new Track(name, 0, TrackType.EXIT);
	}
	
	public Track addParkTrack(String name, int length) throws Exception {
		Track newTrack = null;
		newTrack = new Track(name, length, TrackType.PARK);
		this.tracks.put(name, newTrack); 
		return newTrack;  
	}
	
	public Track addCleanTrack(String name, int length) throws Exception {
		Track newTrack = null;
		newTrack = new Track(name, length, TrackType.CLEAN);
		this.tracks.put(name, newTrack); 
		this.cleaningTracks.add(newTrack); 
		return newTrack;  
	}
	
	public Track addTrackNoParking(String name, int length) throws Exception {
		Track newTrack = null;
		newTrack = new Track(name, length, TrackType.NOPARK);
		this.noParkTracks.put(name, newTrack); 
		return newTrack;  
	}
	
	public Collection<Track> getAllTracks() {
		ArrayList<Track> c = new ArrayList<>(); 
		c.addAll(this.tracks.values()); 
		c.addAll(this.noParkTracks.values()); 
		c.add(this.entrance); 
		c.add(this.exit); 
		c.add(this.inverseExit); 
		return c; 
	}
	
	public Collection<Track> getParkTracks() {
		return this.tracks.values(); 
	}
	
	public Collection<Track> getNoShuntTracks() {
		return this.noParkTracks.values(); 
	}
	
	public Track getTrack(String name) {
		Track t = this.tracks.get(name); 
		if (null == t) {
			t = this.noParkTracks.get(name); 
		}
		return t; 
	}

	public Side getEntrance() {
		return this.entrance.getSideB();
	}

	public Side getExit() {
		return this.exit.getSideA(); 
	}

	public void connectLeftToEntrance(Track a) {
		this.entrance.addOneWayConnectionSideBtoA(a); 
	}

	public void connectToExit(Track t) throws Exception {
		t.addOneWayConnectionSideBtoA(this.exit);
		this.inverseExit.addOneWayConnectionSideAtoB(t);
		this.invariant();
	}

	public void connectLeftToExit(Track t) throws Exception {
		t.addOneWayConnectionSideAtoA(this.exit);
		this.inverseExit.addOneWayConnectionSideAtoA(t);
		this.invariant();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("ShuntingYard: \n" ); 

		sb.append("entrance:\t" + this.entrance.toString() + "\n"); 
		sb.append("exit: \t\t" + this.exit.toString() + "\n"); 
		
		for(Track t : this.tracks.values()) {
			sb.append("\t\t" + t.toString() + "\n"); 
		}
		
		for(Track t : this.noParkTracks.values()) {
			sb.append("\t\t" + t.toString() + "\n"); 
		}
		
		return sb.toString(); 
	}

	public Location getExitAsStartingPoint() throws Exception {
		return new Location(this.inverseExit.getSideA());
	}
	
	private void invariant() throws Exception {
		for (Track t : this.tracks.values()) {
			if (t.isConnectedTo(this.inverseExit)) {
				throw new Exception("Track " + t.getName() + "is connected to the inverseExit"); 
			}
			if (t.isConnectedTo(this.entrance)) {
				throw new Exception("Track " + t.getName() + "is connected to the entrance"); 
			}
		}
		for (Track t : this.noParkTracks.values()) {
			if (t.isConnectedTo(this.inverseExit)) {
				throw new Exception("Track " + t.getName() + "is connected to the inverseExit"); 
			}
			if (t.isConnectedTo(this.entrance)) {
				throw new Exception("Track " + t.getName() + "is connected to the entrance"); 
			}
		}
		if (this.entrance.getSideA().getConnectedSides().size() != 0) {
			throw new Exception("The a-Side of the entrance may not be connected to anything"); 
		}
		if (this.exit.getSideA().getConnectedSides().size() != 0) {
			throw new Exception("The exit may not be connected to anything"); 
		}
		if (this.exit.getSideB().getConnectedSides().size() != 0) {
			throw new Exception("The exit may not be connected to anything"); 
		}
	}

	public Side getInverseExit() {
		return this.inverseExit.getSideA();
	}

	public Collection<Track> getCleaningTracks() throws Exception {
		if (0 == this.cleaningTracks.size()) {
			throw new Exception("No cleaning tracks are present at this shunting yard"); 
		}
		return this.cleaningTracks;
	}

}
