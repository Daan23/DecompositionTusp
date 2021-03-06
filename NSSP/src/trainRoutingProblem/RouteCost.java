package trainRoutingProblem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import shuntingYard.Side;
import shuntingYard.Track;
import shuntingYard.TrackConnection;

public class RouteCost {

	protected HashMap<Side, DijkstraNode> nodeList; 
	
	/**
	 * For each track a Hashmap of moments of which location is on the key
	 * track. The moments keys are their timePoints.
	 */
	private HashMap<Track, HashMap<Integer, TrpMoment>> blockedTracks;
	
	private int turnCost;
	private int straightCost; 
	private int obstructionCost; 
	
	/**
	 * The coordinate of the moment below is needed for calculating the cost of starting from a given track. 
	 */
	private int startingCoordinate;  
	
	private static final boolean DEBUG = false; 
	
	/**
	 * The constructor intentionally initializes the nodelist as null. This
	 * nodelist is created within the Dijkstra method, the init method should be
	 * called before any compare function is used. The comparator largely
	 * influences how routes are found, by choosing a different comparator to
	 * initialize Dijkstra with, we can re-use the dijkstra code and only
	 * recreate a Comparator.
	 */
	public RouteCost() {
		this.nodeList 				= null; 
		this.turnCost 				= 4; 
		this.straightCost 			= 1; 
		this.obstructionCost		= 10; 
	}
	
	public RouteCost(int straight, int turn, int obstruction) {
		this.nodeList			= null; 
		this.straightCost		= straight; 
		this.turnCost			= turn; 
		this.obstructionCost	= obstruction; 
	}

	public void init(HashMap<Side, DijkstraNode> nodes, HashMap<Track, HashMap<Integer, TrpMoment>> blockedTracks, int startingCoordinate) {
		this.nodeList 			= nodes; 
		this.blockedTracks 		= blockedTracks; 
		this.startingCoordinate	= startingCoordinate; 
	}

	public int value(Side source, Side sink) throws Exception {
		int cost = 0; 
		cost = this.getInitialCost(source, sink); 
		if (0 == cost) {
			throw new Exception("The initial cost should never be 0 if this method is called."); 
		} else {
			cost += this.hopCost(source.getTrack(), sink.getTrack()); 
		} 
		return cost; 
	}
	
	/**
	 * Returns the cost of reaching the previous node (source)
	 * 
	 * @param source
	 * @param sink
	 * @return
	 * @throws Exception 
	 */
	protected int getInitialCost(Side source, Side sink) throws Exception {
		if (null == nodeList) {
			throw new Exception ("This comparator was not initialised!");
		}
		int cost = 0; 
		if (source.getTrack().equals(sink.getTrack())) {
			cost = nodeList.get(source).getCost();  
		}
		else {
			cost = nodeList.get(source).getTwinCost();  
		}
		return cost;
	}
	
	protected int hopCost(Track source, Track sink) throws Exception{
		int cost = 0; 
		if (source.equals(sink)) {
			cost += this.turnCost(source); 
		}
		else {
			cost += this.straightCost(source); 
		}
		return cost; 
	}
	
	private int turnCost(Track t) throws Exception {
		int cost = this.turnCost; 
		cost += obstructionCost(t); 
		return cost; 
	}

	private int straightCost(Track t) throws Exception {
		int cost = this.straightCost; 
		cost += obstructionCost(t);
		return cost;
	}

	private int obstructionCost(Track t) throws Exception {
		if (null == this.blockedTracks) {
			throw new Exception("Blocked Tracks was not initialized"); 
		}
		int cost = 0; 
		if(this.blockedTracks.containsKey(t)) {
			int size = this.blockedTracks.get(t).size(); 
			cost += (size * this.obstructionCost);
		}
		return cost;
	}
	
	public DijkstraRouteCost InvertedDijkstra() {
		return new DijkstraRouteCost(this) {
			
			@Override
			int value(TrackConnection x) {
				int cost 			= 0; 
				try {
					Side source = x.getSource(); 
					Side sink 	= x.getSink(); 
					cost 		= rc.getInitialCost(source, sink);
					if (0 == cost) {
						cost 	+= rc.straightCost; 
						cost 	+= rc.getStopCost(source.getTrack()); 
					} else {
						// In the inverted Dijkstra we always need to add the obstruction cost of the NEXT (sink) node. 
						cost 	= rc.value(source, sink);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return cost;
			}

		};
	}

	public DijkstraRouteCost normalDijkstra() {
		return new DijkstraRouteCost(this) {
			
			@Override
			int value(TrackConnection x) {
				int cost 			= 0; 
				try {
					Side source 		= x.getSource(); 
					Side sink 			= x.getSink(); 
					cost 				= rc.getInitialCost(source, sink);
					if (0 == cost) {
						cost += rc.getStartCost(source); 
						cost += rc.straightCost; 
					} else {
						cost = rc.value(source, sink);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return cost;
			}

		};
	}
	
	protected int getStopCost(Track sink) throws Exception {
		int cost = 0; 
		return cost;
	}

	protected int getStartCost(Side source) throws Exception {
		int cost =0; 
		if (this.blockedTracks.containsKey(source.getTrack())) {
			HashMap<Integer, TrpMoment> blocking = this.blockedTracks.get(source.getTrack()); 

			if (source.isSideA()) {
				for (TrpMoment m : blocking.values()) {
					if (this.startingCoordinate >= m.getCoordinate()) {
						cost += this.obstructionCost; 
					}
				}
			} else {
				for (TrpMoment m : blocking.values()) {
					if (this.startingCoordinate <= m.getCoordinate()) {
						cost += this.obstructionCost; 
					}
				}
			}
		}
		return cost; 
	}

	public abstract class DijkstraRouteCost implements Comparator<TrackConnection> {
		
		RouteCost rc; 
		
		public DijkstraRouteCost(RouteCost rc) {
			this.rc = rc; 
		}
		
		/** 
		 * This method is used for the comparator
		 */
		abstract int value(TrackConnection x); 
		
		@Override
		public int compare(TrackConnection x, TrackConnection y) {
			int xVal=0, yVal=0; 
			xVal = this.value(x);
			yVal = this.value(y);
	        if (xVal < yVal) {
	            return -1;
	        }
	        if (xVal > yVal) {
	            return 1;
	        }
	        return 0;
		}
	}
	
	/**
	 * Calculates the total route cost, given the blocked tracks
	 * given within method m. After calling this method the blocked Tracks are
	 * changed to the blocked tracks of the input moment.
	 * 
	 * @param m
	 * @return
	 * @throws Exception 
	 */
	public int totalCost(TrpMoment m) throws Exception {
		if (!m.hasRoute()) { throw new Exception("No route was added to Moment: " + m.toString()); }
		ArrayList<Track> tracks 							= m.getRoute().getTracks(); 
		HashMap<Track, HashMap<Integer, TrpMoment>> blocked	= m.getBlocked(); 
		int startingCoordinate								= m.getPrevious().getCoordinate(); 
		if (1 > tracks.size()) {
			throw new Exception("The route of " + m.toString() + " was not initialized"); 
		}
		int cost = this.totalCost(tracks, blocked, startingCoordinate); 
		return cost; 
		
	}

	public int totalCost(
			ArrayList<Track> tracks, HashMap<Track, 
			HashMap<Integer, TrpMoment>> blocked,
			int startingCoordinate) throws Exception {
		this.blockedTracks 		= blocked; 
		this.startingCoordinate = startingCoordinate; 
		int cost = 0; 
		// If a moment stays on the same track, but that track also contains
		// another moment, the obstruction cost are added. 
		if (1 == tracks.size()) {
			Track t = tracks.get(0); 
			cost += this.obstructionCost(t); 
		} else {
			Track prev = tracks.get(0); 
			Track next = tracks.get(1); 
			Side prevSide 			= prev.getSideConnectedTo(next);
			
			cost += this.getStartCost(prevSide); 
			cost += this.straightCost; 
			
			for (int i = 2; i<tracks.size(); i++ ) {
				prev = next; 
				next = tracks.get(i); 
				cost += this.hopCost(prev, next); 
			}
			cost += this.getStopCost(next); 
		}
		return cost; 
	}

	public void debugPrintInit() {
		if (DEBUG) {
			System.out.println("RouteCost: \t "
					+ "straigth=" + this.straightCost 
					+ "\t turn=" + this.turnCost 
					+ "\t Obstruct=" + this.obstructionCost 
					);
		}
	}

	public void setStraightCost(int i) {
		this.straightCost = i; 
	}

	public void setTurnCost(int i) {
		this.turnCost = i; 
	}

	public void setObstructionCost(int i) {
		this.obstructionCost = i; 
	}

	public int getObstructionCost() {
		return this.obstructionCost;
	}

	public int getTurnCost() {
		return this.turnCost;
	}
	
}
