package trainRoutingProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.MomentObserver;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.Track;

public class TrpMoment implements MomentObserver{
	
	ObservedMoment baseMoment; 

	// Required data for Route problem
	private TrpMoment previous;                        						// Previous moment of the same train
	private TrpMoment next;                           						// Next moment of the same train
	private HashMap<Track, HashMap<Integer, TrpMoment>> blockedTracks;  	// Blocked tracks by other moments, at the time of this moment
	private ArrayList<TrpMoment> observers;          						// List of Moments for which this moment blocks the track
	
	/**
	 * If the baseMoment is changed, we need to remember what the previous Track
	 * was to be able to change the key of this moment within observing moments.
	 */
	private Track baseMomentTrackCopy;
	
	private static RouteCost routeCost; 
	
	private static TrpMoment start 	= null; 
	private static TrpMoment end 	= null; 
	
	public static TrpMoment start() {
		if (null == start) {
			try {
				TrpMoment.createStart();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} 
		return start;
	}
	
	public static TrpMoment end() {
		if (null == end) {
			try {
				TrpMoment.createEnd();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} 
		return end;
	}
	
	private static synchronized void createEnd() throws Exception {
		Location l 			= new Location(Side.dummy()); 
		l.setCoordinate(-1);
		Route r 			= new Route(l, l); 
		ObservedMoment o	= new ObservedMoment(Integer.MAX_VALUE, (Train) null, Side.dummy()); 
		o.setRoute(r);
		end = new TrpMoment(o, true);
	}

	private static synchronized void createStart() throws Exception {
		Location l 			= new Location(Side.dummy()); 
		l.setCoordinate(0);
		Route r 			= new Route(l, l); 
		ObservedMoment o	= new ObservedMoment(Integer.MIN_VALUE, (Train) null, Side.dummy()); 
		o.setRoute(r);
		start = new TrpMoment(o, true);
	}
	
	public TrpMoment(ObservedMoment m) throws Exception {
		this.blockedTracks 	= new HashMap<>(); 
		this.observers 		= new ArrayList<>(); 
		this.previous 		= start(); 
		this.next 			= end(); 
		this.baseMoment		= m; 
		
		try {
			this.baseMomentTrackCopy		= this.baseMoment.getLocation().getTrack();
		} catch (Exception e) {
			if (e.getMessage().startsWith("No location assigned")) {
				this.baseMomentTrackCopy	= Track.dummy(); 
			}
			else {
				throw e; 
			}
		}
		m.addObserver(this);
	}

	public TrpMoment(ObservedMoment o, boolean b) {
		this.blockedTracks 	= new HashMap<>(); 
		this.observers 		= new ArrayList<>(); 
		this.baseMoment		= o; 
	}

	/**
	 * If the baseMoment is changed, this moment does not have to change it's
	 * data structure. But the baseMoment might be located on a different track,
	 * therefore all locationObservers should be notified.
	 * @throws Exception 
	 */
	@Override
	public void momentIsChanged(Moment m) throws Exception {
		
		// Change the hashmap key on which the current moment was stored within its observers. 
		if (this.hasRoute()) {
			Track oldT = this.baseMomentTrackCopy; 
			Track newT = m.getRoute().getDestination().getTrack(); 
			for (TrpMoment locationObserver : this.observers) {
					locationObserver.changeBlockingMoment(this, oldT, newT);
			}
		}
		
		try {
			this.baseMomentTrackCopy		= this.baseMoment.getLocation().getTrack();
		} catch (Exception e) {
			if (e.getMessage().startsWith("No location assigned")) {
				this.baseMomentTrackCopy	= null; 
			}
			else {
				throw e;
			}
		}
	}

	public int getTimePoint() {
		return this.baseMoment.getTimePoint();
	}

	public Train getTrain() {
		return this.baseMoment.getTrain();
	}

	public TrpMoment getLastMoment() throws Exception {
		TrpMoment m = this; 
		while (!m.isLast()) {
			m = m.next; 
		}
		return m;
	}

	public TrpMoment getNext() throws Exception {
		if (null == this.next) throw new Exception(this.toString() + " has no next moment assigned"); 
		return this.next;
	}

	public boolean isLast() throws Exception {
		if (this.equals(TrpMoment.start())) {
			throw new Exception("Checking start"); 
		}
		if (this.equals(TrpMoment.end())) {
			throw new Exception("Checking end"); 
		}
		
		return this.next.equals(TrpMoment.end);
	}

	public TrpMoment getPrevious() {
		return this.previous;
	}

	public static void init(ShuntingYard yard, RouteCost routeCost1) throws Exception {
		
		TrpMoment.routeCost = routeCost1; 
		
		// For some methods we need to know the previous location, this may not
		// be null in that case. We therefore need to add locations and routes
		// to the start and end nodes. 
			Route r1 = new Route(yard.getEntrance(), yard.getEntrance()); 
			r1.getDestination().setCoordinate(0);
			start().getBaseMoment().setRoute(r1);
			
			Route r2 = new Route(yard.getExit(), yard.getExit()); 
			r2.getDestination().setCoordinate(-1);
			end().getBaseMoment().setRoute(r2); 
	}
	
	private void addBlocked(TrpMoment blockingMoment, Track newBlocked) throws Exception {
		if (null == blockingMoment) throw new Exception("moment was null");
		if (null == newBlocked) throw new Exception("track was null");
		HashMap<Integer, TrpMoment> list = this.blockedTracks.get(newBlocked); 
		if (null == list) {
			list = new HashMap<>(); 
			this.blockedTracks.put(newBlocked, list); 
		}
		list.put(blockingMoment.getTimePoint(), blockingMoment); 
	}

	private void removeBlocked(TrpMoment blockingMoment, Track blockedTrack) throws Exception {
		
		if (blockedTrack.equals(Track.dummy())) {
			// The dummy track should not be added to the blockingMoments in the past. It can therefore not be removed. 
			return; 
		}
		
		HashMap<Integer, TrpMoment> list = this.blockedTracks.get(blockedTrack); 
		if (null == list) {
			throw new Exception("For track " + blockedTrack.toString() + 
				" blocked by " + blockingMoment + 
				" no list of blocking moments existed within " + this.toString());
		}
		TrpMoment b = list.remove(blockingMoment.getTimePoint());
		if (null == b) throw new Exception("Element was not found in the list");
	}
	
	public void changeBlockingMoment(TrpMoment blockingMoment, Track oldTrack, Track newTrack) throws Exception {
		if (null != oldTrack) {
			this.removeBlocked(blockingMoment, oldTrack);
		}
		this.addBlocked(blockingMoment, newTrack);
	}
	
	public void replaceBlockingMoment(TrpMoment oldM, Track oldTrack, TrpMoment newM, Track newTrack) throws Exception {
		this.removeBlocked(oldM, oldTrack);
		this.addBlocked(newM, newTrack);
	}

	public HashMap<Track, HashMap<Integer, TrpMoment>> getBlocked() {
		return this.blockedTracks;
	}
	
	public TrpMoment getBlocked(Track t, int id) {
		HashMap<Integer, TrpMoment> list = this.blockedTracks.get(t); 
		TrpMoment m = list.get(id); 
		return m; 
	}

	public Collection<Track> getBlockedTracks() {
		ArrayList<Track> list = new ArrayList<>(); 
		for (Track t : this.blockedTracks.keySet()) {
			if (0 < this.blockedTracks.get(t).size()) {
				list.add(t); 
			}
		}
		return list;
	}

	public void setNext(TrpMoment next) {
		this.next = next; 
		next.previous = this; 
	}

	public Location getPreviousLocation() throws Exception {
		Location l = null; 
		l = this.previous.getLocation();
		return l;
	}

	public Location getLocation() throws Exception {
		return this.baseMoment.getLocation();
	}

	/**
	 * Returns the side at which the next moment of the same train will arrive.
	 * If no location was assigned to the next moment yet, an arbitrary option
	 * is picked from the options available for the next moment.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Side getArrivingSideOfNextMoment() throws Exception {
		Side side = null; 
		try {
			side = this.next.getLocation().getArrivingSide();
		} catch (Exception e) {
			if (e.getMessage().startsWith("No location assigned")) {
				side = this.next.getBaseMoment().getFirstPossibleSide(); 
			}
			else {
				throw e;
			}
		}
		return side;
	}

	public void setBlockedTracks(HashMap<Train, TrpMoment> currentMoments) throws Exception {
		for (TrpMoment m : currentMoments.values()) {
			if (null == this.baseMomentTrackCopy) { throw new Exception("The baseMoment track copy may not be null, should at least be equal to dummy track"); } 
			Track t = null; 
			
			if (m.getBaseMoment().hasLocation()) {
				t = m.getLocation().getTrack(); 
			} else {
				t = this.baseMomentTrackCopy; 
			}
			
			this.addBlocked(m, t);
			m.setObserver(this); 
		}
	}

	private void setObserver(TrpMoment m) {
		if (!this.observers.contains(m)) {
			this.observers.add(m);
		}
	}

	public ArrayList<TrpMoment> getObservers() throws Exception {
		if (null == this.observers) throw new Exception("Observers is null");
		return this.observers;
	}

	public ObservedMoment getBaseMoment() {
		return this.baseMoment;
	}

	public Route getRoute() throws Exception {
		return this.baseMoment.getRoute();
	}
	
	public String toString() {
		return "TrpMoment " + this.getTimePoint(); 
	}

	public int getCoordinate() throws Exception {
		return this.getBaseMoment().getCoordinate();
	}

	@Override
	public void makeConsistent() throws Exception {
		// This data structure is already consistent after a solution is executed. 
		// We only check if a route smaller than 1 was added. 
		if (1 > this.getRoute().getTracks().size()) {
			throw new Exception("No Route was added to " + this.toString()); 
		}
		
		// Recalculate the routeCost for all observing moments if they have a route. 
		for (TrpMoment observer : this.getObservers()) {
			if (observer.hasRoute()) {
				int cost = TrpMoment.routeCost.totalCost(observer); 
				observer.getBaseMoment().setRouteCost(cost); 	
			}
		}
		int cost = TrpMoment.routeCost.totalCost(this); 
		this.getBaseMoment().setRouteCost(cost);
		
		if (!this.isLast()) {
			TrpMoment q = this.getNext(); 
			if (q.hasRoute()) {
				cost = TrpMoment.routeCost.totalCost(q); 
				q.getBaseMoment().setRouteCost(cost);
			}
		}
	}

	public TrackSolutionSpace getOptions() {
		return this.getBaseMoment().getOptions();
	}

	public void removeAsObserver() throws Exception {
		this.getBaseMoment().removeObserver(this); 
	}

	public Track getTrack() throws Exception {
		return this.getRoute().getDestination().getTrack();
	}

	public int routeCostIfBlockedChanged(int startCoordinate, TrpMoment blockingMoment, Side newBlocked, int blockedCoordinate) throws Exception {
		if (!this.hasRoute()) {
			throw new Exception ("No route was added to Moment: " + this.toString()); 
		}
		int blocked = 0; 
		
		// If this moment is currently being blocked by blockingMoment
		{
			int amount = this.getRoute().containsTrack(blockingMoment.getTrack()); 
			blocked -= amount; 
		}
		
		// If this moment is currently blocked at the start
		if (this.getPrevious().getTrack().equals(blockingMoment.getTrack())) {
			Side leavingSide = this.getRoute().getDestination().getArrivingSide(); 
			if (this.getRoute().getTracks().size() > 1 ) {
				leavingSide = this.getRoute().getLeavingSide(); 
			}
			// compare coordinates
			int thisStartCoordinate 	= this.getPrevious().getCoordinate(); 
			int blockingStartCoordinate	= blockingMoment.getCoordinate(); 
			if (leavingSide.isSideA()) {
				if (thisStartCoordinate >= blockingStartCoordinate) {
					blocked --; 
				}
			} else {
				if (thisStartCoordinate <= blockingStartCoordinate) {
					blocked --; 
				}
			}
		}
		// If this moment is going to be blocked at the Route by blockingMoment
		{
			int amount = this.getRoute().containsTrack(newBlocked.getTrack()); 
			blocked += amount; 
		}
		// If this moment is going to be blocked at the start
		if (this.getPrevious().getTrack().equals(newBlocked.getTrack())) {
			
			Side leavingSide = this.getRoute().getDestination().getArrivingSide(); 
			if (this.getRoute().getTracks().size() > 1 ) {
				leavingSide = this.getRoute().getLeavingSide(); 
			}
			
			// compare coordinates
			if (leavingSide.isSideA()) {
				if (startCoordinate >= blockedCoordinate) {
					blocked ++; 
				}
			} else {
				if (startCoordinate <= blockedCoordinate) {
					blocked ++; 
				}
			}
			
		}
		
		return blocked*TrpMoment.routeCost.getObstructionCost(); 
	}

	public boolean hasRoute() {
		return this.getBaseMoment().hasRoute();
	}

	public int getTrainLength() {
		return this.getTrain().getLength();
	}

	public static RouteCost getRouteCostObject() {
		return TrpMoment.routeCost;
	}

	public boolean hasLocation() {
		return this.getBaseMoment().hasLocation();
	}
	
}
