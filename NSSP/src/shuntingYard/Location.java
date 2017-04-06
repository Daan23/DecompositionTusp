package shuntingYard;

import java.util.ArrayList;

/**
 * This class curently is identical to a track. 
 * In the future a train might be located on multiple tracks. 
 * To make it easier to implement this in the future, this class is introduced. 
 * @author Daan
 *
 */
public class Location {	
	
	private Side 	arrivingSide; 	// The side at which this moment arrived. 
	private int 	coordinate; 	// The coordinate to indicate where on the track this moment is stored. 
	
	/**
	 * Some classes require a location to be non null. This dummy location can
	 * be used in that case.
	 */
	private static Location dummy;
	
	public static Location dummy() {
		if (null == Location.dummy) {
			Location.setDummy();
		}
		return Location.dummy;
	}
	
	private synchronized static void setDummy() {
		try {
			Location.dummy  = new Location(Side.dummy());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public Location(Side arrivingSide) throws Exception {
			if (null == arrivingSide.getTrack()) {
				throw new Exception("The track added to a location may not be null."); 
			}
		this.arrivingSide	= arrivingSide; 
		this.coordinate = 0; 
	}
	
	public Location(Side s, int c) throws Exception {
		this(s); 
		this.setCoordinate(c);
	}

	public Track getTrack() throws Exception {
		if (null == this.arrivingSide) {
			throw new Exception("No track was added");
		}
		return arrivingSide.getTrack();
	}

	public ArrayList<TrackConnection> getDepartures() throws Exception {
		ArrayList<TrackConnection> list = new ArrayList<>(); 
		list.addAll(this.getTrack().getSideA().getConnectedSides()); 
		list.addAll(this.getTrack().getSideB().getConnectedSides()); 
		return list;
	}

	public Side getHead() {
		try {
			return this.getTrack().getSideA();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 

	public Side getTail() throws Exception {
		return this.getTrack().getSideB();
	}

	@Override
	public boolean equals(Object that) {
		if(Location.class != that.getClass()) {
			return false; 
		} else
			try {
				if(this.getTrack().equals(((Location) that).getTrack())) {
					if (this.getArrivingSide().equals(((Location) that).getArrivingSide())) {
						return true; 
					}
				}
			} catch (Exception e) {
				if (e.getMessage().equals("No track was added")) {
					// not equal if one or both tracks are null
					return false; 
				}
			}
		return false;
	} 
	
	public String toString() { 
		return this.arrivingSide.toString(); 
	}

	public boolean hasTrack(Track that) throws Exception {
		return this.getTrack().equals(that);
	}

	public int getCoordinate() {
		return this.coordinate;
	}
	
	public void setCoordinate(int coordinate) {
		this.coordinate = coordinate; 
	}

	public Side getArrivingSide() {
		return this.arrivingSide;
	}

	public Location copy() throws Exception {
		Location copy = new Location(this.getArrivingSide()); 
		copy.setCoordinate(this.coordinate);
		return copy;
	}

}
