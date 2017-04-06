package trainRoutingProblem;

import java.util.Collection;

import shuntingYard.Side;
import shuntingYard.Track;
import shuntingYard.TrackConnection;

public class DijkstraNode {
	private int 			cost; 		// The cost to reach this side, inf if not reached
	private Side	 		location;
	private DijkstraNode	origin; 
	private DijkstraNode	twin; 
	                    
	public DijkstraNode(Side side) {
		this.setSide(side); 
		this.cost = Integer.MAX_VALUE; 
	}
	
	public void connect(DijkstraNode twin) {
		this.setTwin(twin); 
		twin.setTwin(this); 
	}
	
	private void setTwin(DijkstraNode twin) {
		this.twin = twin; 
	}
	
	public int getTwinCost() {
		return this.twin.getCost(); 
	}

	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}

	public Track getTrack() {
		return location.getTrack();
	}

	private void setSide(Side side) {
		this.location = side;
	}

	public DijkstraNode getOrigin() {
		return origin;
	}

	public void setOrigin(DijkstraNode origin) {
		this.origin = origin;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.getSide().toString()); 
		sb.append("\t("); 
		if (null == this.getOrigin()) {
			sb.append("null"); 
		}
		else {
			sb.append(this.getOrigin().getSide().toString());
		}
		sb.append(",\t"); 
		sb.append(this.getCost()); 
		sb.append(")"); 
		return sb.toString(); 
	}

	public boolean isReached() {
		return (Integer.MAX_VALUE != this.getCost());
	}
	
	public boolean twinIsReached() {
		return (Integer.MAX_VALUE != this.getTwinCost());
	}

	public Collection<? extends TrackConnection> getTwinConnectedSides() {
		return this.twin.getSide().getConnectedSides();
	}

	public Side getSide() {
		return this.location;
	}

	public DijkstraNode getTwin() {
		return this.twin;
	}

	public TrackConnection CreateTrackConnectionToTwin() {
		return new TrackConnection(this.location, this.twin.location);	
	}

	public boolean isTwinOf(DijkstraNode that) {
		boolean bool = this.twin.equals(that); 
		return bool;
	}
	
}
