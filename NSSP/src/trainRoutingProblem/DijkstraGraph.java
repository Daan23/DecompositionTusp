package trainRoutingProblem;

import java.util.ArrayList;
import java.util.HashMap;

import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.Track;

public class DijkstraGraph extends HashMap<Side, DijkstraNode>{
	
	private static final long serialVersionUID = 1L;
	
	private ShuntingYard yard; 
	
	private boolean inverted; 
	
	public DijkstraGraph (ShuntingYard yard, boolean inverted) {
		super(); 
		this.genNodes(yard);
		this.yard 		= yard; 
		this.inverted 	= inverted; 
	}
	
	public DijkstraNode get(Side s) {
		if (inverted && s.equals(yard.getExit())) {
			return super.get(yard.getInverseExit()); 
		} else {
			return super.get(s); 
		}
	}
	
	/**
	 * Creates the needed DijkstraNodes for using the dijkstra algorithm. 
	 * Nodes are initialised to have a cost of Interger.Max. 
	 * 
	 * Method is made public to be able to test it. 
	 * @return
	 */
	public void genNodes(ShuntingYard yard) {
		for(Track t : yard.getAllTracks()) {
			DijkstraNode a = new DijkstraNode(t.getSideA()); 
			DijkstraNode b = new DijkstraNode(t.getSideB()); 
			a.connect(b);
			super.put(t.getSideA(), a); 
			super.put(t.getSideB(), b); 
		}
	}

	/**
	 * After the dijkstra method is used, this method can be used to find a
	 * single route within the globally defined nodes.
	 * 
	 * @param end
	 * @param begin
	 * @return
	 */
	public Route traverseRoute(Side sink) throws Exception {
		if (null == sink) {
			throw new Exception("sink is null"); 
		}
		DijkstraNode sink1 	 = this.get(sink); 
		if(Integer.MAX_VALUE == sink1.getCost()) {
			throw new Exception("No route was found to " + sink.toString()); 
		}
		// Assigns the sink to both the source and destination of the Route.
		Route r = this.genRoute(sink); 
		r.setCost(sink1.getCost());
		DijkstraNode prev = sink1; 
		// Skip the first track if it has the same track as the second track. 
		// This prevents that a turn is made on the last track. This is not allowed. 
		if (null != prev.getOrigin()) {
			DijkstraNode next = prev.getOrigin(); 
			if (prev.getTrack().equals(next.getTrack())) {
				prev = next; 
			}
		}
		while(null != prev.getOrigin()) { 
			this.addBefore(r, prev.getTrack()); 
			prev = prev.getOrigin(); 
		}
		this.addBefore(r, prev.getTrack());
		// Add the real source to the Route.
		r.setSource(new Location(prev.getSide()));
		if (0 == r.getTracks().size()) {
			throw new Exception ("No tracks are added to the route");
		}
		if (r.getTracks().size() > 1) {
			if (r.getTracks().get(r.getTracks().size()-1).equals(
				r.getTracks().get(r.getTracks().size()-2))) {
				throw new Exception ("The last two tracks may not be equal"); 
			}
		}
		if (r.getDestination().getTrack().equals(yard.getInverseExit().getTrack())) {
			throw new Exception("The next location may not be the inverted Exit."); 
		}
		return r; 
	}

	private Route genRoute(Side s) throws Exception {
		Route r = null; 
		if (s.equals(yard.getInverseExit())) {
			r = new Route(yard.getExit(), yard.getExit()); 
		}
		else {
			r = new Route(s, s); 
		}
		return r;
	}

	private void addBefore(Route r, Track t) throws Exception {
		if (t.equals(yard.getInverseExit().getTrack())) {
			r.addBefore(yard.getExit().getTrack());
		} else {
			r.addBefore(t);
		}
		
	}

	/**
	 * Calculates the route from the source to the sink. 
	 * @param nodes contains all shortest paths starting from the sink (inverse form source)
	 * @param best
	 * @param nextLocation
	 * @return
	 * @throws Exception 
	 */
	public Route traverseRouteInverse(Side source) throws Exception {
		if (null == source) {
			throw new Exception("Source is null"); 
		}
		DijkstraNode source1 	= this.get(source); 
		DijkstraNode cheapest 	= source1; 
		
		// Assigns the source to both the source and destination of the Route.
		Route r = this.genRoute(source); 
		ArrayList<Track> tracks = r.getTracks(); 
		
		if(Integer.MAX_VALUE == cheapest.getCost()) {
			throw new Exception("No route was found"); 
		}
		r.setCost(cheapest.getCost());
		DijkstraNode next = cheapest; 
		while(null != next.getOrigin()) {
			this.addAfter(r, next.getTrack()); 
			next = next.getOrigin(); 
		}
		
		// Prevent that a train makes a turn on the last track (that the track occurs twice). 
		Track lastAdded = (tracks.size() > 0) ? tracks.get(tracks.size()-1) : null; 
		Track nextTrack = next.getTrack(); 
		Side destination = null; 
		if (!nextTrack.equals(lastAdded)) {
			this.addAfter(r, nextTrack);
			destination = next.getSide();
		} else {
			destination = next.getSide().getOtherSide();
		}
		
		// prevent that the destination is set as the inverse exit. 
		if (destination.getTrack().equals(yard.getInverseExit().getTrack())) {
			destination = yard.getExit(); 
		}
				
		this.setDestination(r, destination);
		
		if (0 == r.getTracks().size()) {
			throw new Exception ("No tracks are added to the route");
		}
		if (r.getTracks().size() > 1) {
			if (tracks.get(tracks.size()-1).equals(
				tracks.get(tracks.size()-2))) {
				throw new Exception ("The last two tracks may not be equal, route is: " + r.toString());
			}
			if (tracks.get(0).equals(tracks.get(1))) {
				throw new Exception ("The first two tracks may not be equal, route is: " + r.toString()); 
			}
		}
		if (r.getDestination().getTrack().equals(yard.getInverseExit().getTrack())) {
			throw new Exception("The next location may not be the inverted Exit."); 
		}
		return r; 
	}

	private void addAfter(Route r, Track t) throws Exception {
		if (t.equals(yard.getInverseExit().getTrack())) {
			r.addAfter(yard.getExit().getTrack());
		} else {
			r.addAfter(t);
		}
	}

	private void setDestination(Route r, Side s) throws Exception {
		if (s.equals(yard.getInverseExit())) {
			r.setDestination(new Location(yard.getExit()));
		} else {
			r.setDestination(new Location(s));
		}
	}

	public void initStartLocation(Side from, int cost) throws Exception {
		super.get(from).setCost(cost);
	}

	/**
	 * This method returns the Side to which the path is shortest from the
	 * source to the given track.
	 * 
	 * @param t
	 * @return
	 */
	public Side getCheapestSide(Track t) {
		DijkstraNode a 	= super.get(t.getSideA()); 
		DijkstraNode b 	= super.get(t.getSideB()); 
		Side s			= (a.getCost() <= b.getCost()) ? a.getSide() : b.getSide(); 
		return s; 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("DijkstraGraph: {"); 
		for (DijkstraNode d : super.values()) {
			sb.append(d.getSide().toString()); 
			sb.append("<=="); 
			if (null == d.getOrigin()) {
				sb.append("null"); 
			} else {
				sb.append(d.getOrigin().getSide()); 
			}
			sb.append(" cost="); 
			sb.append(d.getCost()); 
			sb.append(",\t"); 
		}
		sb.append("}"); 
		return sb.toString(); 
	}
}
