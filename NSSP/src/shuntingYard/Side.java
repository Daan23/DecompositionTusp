package shuntingYard;

import java.util.ArrayList;

public class Side {
	private Track track; 
	private ArrayList<TrackConnection> connectedSides; 
	private int coordinate; 
	

	private static Side dummy;
	
	public static Side dummy() {
		if (null == dummy) {
			Side.createDummy(); 
		}
		return Side.dummy; 
	}
	
	private static synchronized void createDummy() {
		Track t = Track.dummy(); 
		Side.dummy = t.getSideA(); 
	}

	public Side(Track track, int coordinate) {
		this.setTrack(track);
		this.coordinate 	= coordinate; 
		this.connectedSides = new ArrayList<>(); 
	}

	public Track getTrack() {
		return track;
	}

	private void setTrack(Track track) {
		this.track = track;
	}

	public ArrayList<TrackConnection> getConnectedSides() {
		return connectedSides;
	}
	
	public void connectTo(Side connect) {
		TrackConnection tc1 = new TrackConnection(this, connect); 
		this.addOneWayConnectionTo(tc1); 
		TrackConnection tc2 = new TrackConnection(connect, this); 
		connect.addOneWayConnectionTo(tc2);
	}
	
	protected void addOneWayConnectionTo(Side destination) {
		TrackConnection tc = new TrackConnection(this, destination); 
		this.addOneWayConnectionTo(tc);
	}

	protected void addOneWayConnectionTo(TrackConnection connect) {
		this.connectedSides.add(connect); 
	}

	public int getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(int coordinate) {
		this.coordinate = coordinate;
	}

	public Side getOtherSide() throws Exception {
		return this.track.getOtherSide(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.getTrack().getName() + "_"); 
		if(this.getTrack().getSideA().equals(this)) {
			sb.append("a"); 
		} 
		else {
			sb.append("b"); 
		}
		return sb.toString(); 
	}
	
	/**
	 * returns true if this Side is the A-Side of the track. 
	 * returns false if this Side is the B-Side of the track. 
	 * @return
	 */
	public boolean isSideA() {
		return this.getTrack().getSideA().equals(this); 
	}

	public boolean isSideB() {
		return this.getTrack().getSideB().equals(this); 
	}

	public boolean isConnectedTo(Side to) {
		boolean b = false; 
		for (TrackConnection t : this.connectedSides) {
			if (t.getSink().equals(to)) {
				b = true; 
			}
		}
		return b;
	}

	public boolean isCleaningTrack() {
		return this.track.isCleaningTrack(); 
	}
	
}
