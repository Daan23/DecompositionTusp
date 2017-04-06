package shuntingYard;

import java.util.ArrayList;

public class Route {
	private Location source; 
	private Location destination; 
	private ArrayList<Track> route;

	// A Route does not really have a cost. This field is used within the
	// TRP, and therefore belongs to a TrpMoment. This should be refactored.
	// However, for debugging purposses we keep it here for the moment. 
	private int cost; 
	
	private Route() {
		this.route = new ArrayList<>(); 
		this.setCost(0); 
	}
	
	public Route(Location from, Location to) throws Exception {
		this(); 
		if (null == from) throw new Exception("Source of a route may not be null");
		if (null == to) throw new Exception("Destination of a route may not be null");
		this.source = from; 
		this.destination = to; 
	}
	
	public Route(Side from, Side to) throws Exception {
		this();
		this.source 		= new Location(from); 
		this.destination 	= new Location(to); 
	}

	public Location getSource() {
		return source;
	}
	
	public void setSource(Location from) throws Exception {
		if (null == from) throw new Exception("Source of a route may not be null");
		this.source = from;
	}
	
	public Location getDestination() {
		return destination;
	}
	
	public void setDestination(Location to) throws Exception {
		if (null == to) throw new Exception("Location was null");
		this.destination = to;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int routeCost) {
		this.cost = routeCost;
	}

	public ArrayList<Track> getTracks() {
		return route;
	}
	
	public void setRoute(ArrayList<Track> route) {
		this.route = route; 
	}

	public void addBefore(Track track) throws Exception {
		if (track.getName().equals("inv")) {
			throw new Exception("May not add the inverted exit"); 
		}
		this.route.add(0, track); 
	}

	public void addAfter(Track track) throws Exception {
		if (track.getName().equals("inv")) {
			throw new Exception("May not add the inverted exit"); 
		}
		this.route.add(track); 
	}

	public int containsLocation(Location l) throws Exception {
		if (null==l) throw new Exception("Location is null");
		return this.containsTrack(l.getTrack());
	}
	
	/**
	 * Returns the amount of times the track occurs in the route. 0 if it does
	 * not occur, 1 if it occurs once, 2 if a turn is made on the track.
	 * 
	 * @param t2
	 * @return
	 * @throws Exception
	 */
	public int containsTrack(Track t2) throws Exception {
		if (null==t2) throw new Exception("Track is null");
		int amount = 0; 
		for(int i = 1; i < this.route.size()-1; i++) {		
			Track t = this.route.get(i); 
			if(t.equals(t2)) {
				amount ++; 
			}
		}
		return amount;
	}

	public boolean hasRoute() {
		return (0 != this.route.size());
	}

	public Side getLeavingSide() throws Exception {
		Track prev 			= this.route.get(0); 
		Track next 			= this.route.get(1); 
		Side leavingSide 	= prev.getSideConnectedTo(next);
		return leavingSide;
	}

	public Route copy() throws Exception {
		Route copy = new Route(this.copySource(), this.copyDestination()); 
		copy.setRoute(this.copyTracks());
		copy.setCost(this.getCost());
		return copy;
	}

	private Location copyDestination() throws Exception {
		Location l = new Location(this.destination.getArrivingSide()); 
		l.setCoordinate(this.destination.getCoordinate());
		return l;
	}

	private Location copySource() throws Exception {
		return new Location(this.getSource().getHead());
	}

	private ArrayList<Track> copyTracks() {
		ArrayList<Track> tracks = new ArrayList<>(); 
		for (Track t : this.getTracks()) {
			tracks.add(t); 
		}
		return tracks;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		
		ArrayList<Track> trackList = this.getTracks(); 
		
		for(int i=0; i<trackList.size(); i++) {
			Track t = trackList.get(i);
			String string = t.getName(); 
			if(string.length() < 3) {

				if (i == trackList.size()-1) {
					sb.append(this.getDestination().getArrivingSide().toString()); 
				}
				else {
					sb.append(t.getName() + "."); 
				}
			}
		}
		return sb.toString(); 
	}

}
