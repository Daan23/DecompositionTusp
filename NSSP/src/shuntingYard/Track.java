package shuntingYard;

import java.util.ArrayList;

public class Track {
	private Side 	sideA; 
	private Side 	sideB; 
	private String 	name;
	private TrackType trackType; 
	
	public enum TrackType {PARK, CLEAN, ENTRANCE, EXIT, NOPARK}; 
	
	/**
	 * Some classes require a Track to be non null. This dummy Track can
	 * be used in that case.
	 */
	private static Track dummy = null; 
	
	public static synchronized Track dummy() {
			if (null == Track.dummy) {
				Track.createDummy(); 
				//Track.dummy = new Track("dummy", 0);
			}
		return Track.dummy; 
	}
	
	private static synchronized void createDummy() {
		try {
			Track.dummy = new Track("dummy", 0, TrackType.PARK);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Track(String name, int length, TrackType type) throws Exception {
		this.sideA 		= new Side(this, 0); 
		this.sideB 		= new Side(this, length); 
		this.name 		= name; 
		this.trackType	= type; 
	}
	
	public Side getSideA() {
		return sideA;
	}

	public void setSideA(Side sideA) {
		this.sideA = sideA;
	}

	public Side getSideB() {
		return sideB;
	}

	public void setSideB(Side sideB) {
		this.sideB = sideB;
	}

	public String getName() {
		return name;
	}

	public Side getOtherSide(Side side) throws Exception {
		if(side == this.sideA) {
			return this.sideB; 
		}
		if(side == this.sideB) {
			return this.sideA; 
		}
		throw new Exception("The other side of " + side.toString() + " given side was not defined in track " + this.name); 
	}

	public void connectLeftToRight(Track right) {
		this.getSideB().connectTo(right.getSideA());
	}

	public void addOneWayConnectionSideBtoA(Track t) {
		this.getSideB().addOneWayConnectionTo(t.getSideA());
	}

	public void addOneWayConnectionSideAtoB(Track t) {
		this.getSideA().addOneWayConnectionTo(t.getSideB());
	}

	public void addOneWayConnectionSideAtoA(Track t) {
		this.getSideA().addOneWayConnectionTo(t.getSideA());
	}
	
	public String toStringExtra() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.name + ":\t"); 
		
		sb.append("SideA to : {"); 
		for(TrackConnection t : this.sideA.getConnectedSides()) {
			sb.append(t.getSink().getTrack().getName()); 
			sb.append(", "); 
		}
		sb.append("}, \t"); 

		sb.append("SideB to : {"); 
		for(TrackConnection t : this.sideB.getConnectedSides()) {
			sb.append(t.getSink().getTrack().getName()); 
			sb.append(", "); 
		}
		sb.append("}"); 
		
		return sb.toString(); 
	}
	

	public String toString() {
		return this.name; 
	}

	public boolean isConnectedTo(Track t) {
		boolean b = false; 
		for (TrackConnection tc : this.sideA.getConnectedSides()) {
			if (tc.getSink().getTrack().equals(t)) {
				b = true; 
			}
		}
		for (TrackConnection tc : this.sideB.getConnectedSides()) {
			if (tc.getSink().getTrack().equals(t)) {
				b = true; 
			}
		}
		return b; 
	}

	/**
	 * 
	 * @param t
	 * @return
	 * @throws Exception 
	 */
	public Side getSideConnectedTo(Track t) throws Exception {
		return this.getTrackConnectionTo(t).getSource(); 
	}

	public TrackConnection getTrackConnectionTo(Track to) throws Exception {
		ArrayList<TrackConnection> list = this.sideA.getConnectedSides(); 
		for (TrackConnection c : list) {
			if (c.getSink().getTrack().equals(to)) {
				return c; 
			}
		}
		list = this.sideB.getConnectedSides(); 
		for (TrackConnection c : list) {
			if (c.getSink().getTrack().equals(to)) {
				return c; 
			}
		}
		throw new Exception("The input track " + to.getName() + " was not connected to this track " + this.toString()); 
	}

	/**
	 * Returns the length of this track. 
	 * @return
	 */
	public int getLength() {
		// The b-side is always the side with the high coordinate
		// a-side always has coordinate 0.
		return this.sideB.getCoordinate(); 
	}

	public boolean isCleaningTrack() {
		return this.trackType.equals(TrackType.CLEAN); 
	}
}
