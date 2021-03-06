package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;
import java.util.Collection;

import mspInstance.Train;
import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.Track;

/**
 * This class defines at what time a train gets to a specific location using a
 * specific Route. This class is abstract since no single Problem should have
 * the right to write this class. This class is extended by ObservedMoment,
 * problems do have the right to write that class.
 * 
 * @author daan.vandenheuvel
 *
 */
public class Moment {
	
// Input
	protected int timePoint; 					// Unique timePoint on which this moment is executed. 
	private Train train;                        // The train this moment belongs to

// Solution Space
	private TrackSolutionSpace locationOptions;    // Location options to be tried for this moment

// Output
	protected Route route;                      // The route to the current location from the location of the previous moment
	
	protected Moment previous	= null;		// Previous moment of the same train
	protected Moment next		= null;		// Next moment of the same train
	
	public static Moment start 	= new Moment(Integer.MIN_VALUE, null); 
	public static Moment end 	= new Moment(Integer.MAX_VALUE, null); 

	public static void init(ShuntingYard yard) throws Exception {
		// For some methods we need to know the previous location, this may not
		// be null in that case. We therefore need to add locations and routes
		// to the start and end nodes. 
		Route r1 = new Route(yard.getEntrance(), yard.getEntrance()); 
		r1.getDestination().setCoordinate(0);
		Moment.start.route = r1;
		Route r2 = new Route(yard.getExit(), yard.getExit()); 
		r2.getDestination().setCoordinate(-1);
		Moment.end.route = r2; 
	}

	/**
	 * Moments should always have an option to be stored at the shunting yard,
	 * therefore this method should be private.
	 * 
	 * @param id
	 * @param train
	 */
	private Moment(int timePoint, Train train) {
		this.locationOptions 	= new TrackSolutionSpace(); 
		this.route		= null; 
		this.timePoint 	= timePoint; 
		this.train 		= train; 
		this.previous	= Moment.start; 
		this.next		= Moment.end; 
	}	
	
	public Moment(int timePoint, Train train, Side possibleLocation) {
		this(timePoint, train); 
		// If possibleLocation is null, we do not want to add it to the possible locations. 
		if (!possibleLocation.equals(Side.dummy())) {
			this.addLocationOption(possibleLocation);
		}
	}
	
	public Moment(int id, Train train, Track possibleLocation) {
		this(id, train); 
		this.addLocationOption(possibleLocation.getSideA());
		this.addLocationOption(possibleLocation.getSideB());
	}

	public Moment(int id, Train train, Collection<Track> possibleLocation) {
		this(id, train); 
		Collection<Side> options = new ArrayList<>();
		for (Track t : possibleLocation) {
			options.add(t.getSideA()); 
			options.add(t.getSideB()); 
		}
		this.addLocationOption(options);
	}
	
	public Moment(int id, Train train, ArrayList<Side> possibilities) {
		this(id, train); 
		this.locationOptions.addAll(possibilities); 
	}

	protected Moment(int timePoint, Train train,
			TrackSolutionSpace trackSolutionSpace) {
		this(timePoint, train);
		this.locationOptions = trackSolutionSpace; 
	}

	public int getTrainLength() {
		return this.train.getLength(); 
	}

	public Location getLocation() throws Exception {
		if (null == this.route) {
			throw new Exception("No location assigned to " + this.toString()); 
		}
		return this.route.getDestination();
	}

	public Side getFirstPossibleSide() throws Exception {
		if (this.locationOptions.isEmpty()) {
			throw new Exception("No possible locations stored for " + this.toString() + " " + this.getTrain().getName()); 
		}
		else {
			return this.locationOptions.get(0); 
		}
		
	}

	public TrackSolutionSpace getOptions() {
		return this.locationOptions;
	}

	protected void addLocationOption(Side s) {
		this.locationOptions.add(s);
	}

	protected void removeOption(Side l) throws Exception {
		this.locationOptions.remove(l); 
	}

	public boolean hasOneLocationOption() {
		return (1 == this.locationOptions.size());
	}

	public boolean hasNoLocationOption() {
		return (0 == this.locationOptions.size());
	}

	public boolean hasLocationOptions() {
		return (0 < this.locationOptions.size());
	}

	public void addLocationOption(Collection<Side> sides) {
		for (Side s : sides) {
			this.addLocationOption(s); 
		}
	}

	public Train getTrain() {
		return this.train;
	}

	/**
	 * Returns the timePoint on which this moment is executed. This timePoint
	 * should be unique within the complete schedule.
	 * 
	 * @return
	 */
	public int getTimePoint() {
		return this.timePoint;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("Moment: "); 
		sb.append(this.timePoint + ":");
		if(null == this.route || this.equals(Moment.end) || this.equals(Moment.start)) {
			sb.append("null"); 
		} 
		else {
			sb.append(this.route.getDestination().toString()); 
		}
		return sb.toString(); 
	}

	public boolean hasLocation(Track track) throws Exception {
		if (null != this.route) {
			return this.route.getDestination().hasTrack(track);
		}
		return false;
	}

	public Route getRoute() throws Exception {
		if (null == this.route) throw new Exception("No route was added to " + this.toString());
		return this.route;
	}
	
	public boolean isBlockedBy(Moment m) throws Exception {
		if (this.getTimePoint() > m.getTimePoint() && this.getTimePoint() < m.next.getTimePoint()) {
			if(0 < this.getRoute().containsLocation(m.getLocation())) {
				return true; 
			}
			if (this.getRoute().getTracks().size() <= 1) {
				if (this.getLocation().equals(m.getLocation())) {
					// For the TLP this may not occur since routes have to be longer than 1
					// For the TRP the moment is blocked
					return true; 
				}
			} 
			if(this.getRoute().getSource().getTrack().equals(m.getTrack())) {
				Track first 	= this.getRoute().getTracks().get(0); 
				Track second	= this.getRoute().getTracks().get(1); 
				
				Side leavingAt	= first.getSideConnectedTo(second);
				
				if (leavingAt.isSideA()) {
					if (this.previous.getCoordinate() >= m.getCoordinate()) {
						return true; 
					}
				} 
				else {
					if (this.previous.getCoordinate() <= m.getCoordinate()) {
						return true; 
					}
				}
			}
			if(this.getRoute().getDestination().getTrack().equals(m.getTrack())) {
				// In normal cases the TLP should have made this consistent 

				ArrayList<Track> tracks = this.getRoute().getTracks(); 
				Track last 				= tracks.get(tracks.size()-1); 
				Track beforeLast 		= tracks.get(tracks.size()-2); 
				
				Side leavingBeforeLastFrom			= beforeLast.getSideConnectedTo(last); 
				if (leavingBeforeLastFrom.isConnectedTo(last.getSideA())) {
					if (this.getCoordinate() >= m.getCoordinate()) {
						return true; 
					}
				}
				else if (leavingBeforeLastFrom.isConnectedTo(last.getSideB())) {
					if (this.getCoordinate() <= m.getCoordinate()) {
						return true; 
					}
				}
				else {
					throw new Exception("leavingAt should be connected to one of the two sides of track last"); 
				}
			}
		}
		return false;
	}

	public boolean hasRoute() {
		boolean hasRoute = (null != this.route); 
		if (hasRoute) {
			hasRoute = this.route.hasRoute(); 
		}
		return hasRoute;
	}

	public void setRouteCost(int cost) throws Exception {
		this.getRoute().setCost(cost);
	}

	public int getRouteCost() throws Exception {
		return this.getRoute().getCost();
	}

	/**
	 * Checks if a moment has a location. 
	 * 
	 * @return
	 */
	public boolean hasLocation() {
	/* If a moment has a route (even though that route may not contain a
	 * sequence of tracks to get there (Route.route)), it has a location
	 * assigned. This is true since locations within the class Route may not be
	 * null.
	 */
		return (null != this.route);
	}

	public Track getTrack() throws Exception {
		return this.getLocation().getTrack();
	}

	public Side getArrivingSide() throws Exception {
		Side s = null; 
		Location l 	= this.getLocation();
		s 			= l.getArrivingSide(); 
		return s;
	}

	public boolean isEndMoment() {
		return (Moment.end == this);
	}

	public boolean isFirstMoment() {
		return (this.previous == Moment.start);
	}

	public int getCoordinate() {
		return this.route.getDestination().getCoordinate();
	}

	public boolean hasLocationOption(Side o) {
		return this.locationOptions.contains(o);
	}

	public int getTrackLength() throws Exception {
		return this.getTrack().getLength();
	}


	public Moment getNext() {
		return this.next;
	}


	public void setTimePoint(int i) {
		this.timePoint = i; 
	}


	public void restoreInitialLocationOptions() {
		this.locationOptions.restore(); 
	}

	protected TrackSolutionSpace getTrackSolutionSpace() {
		return this.locationOptions;
	}

}
