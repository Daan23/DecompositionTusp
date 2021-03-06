package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.Track;

public class NsspInstance {
	private ShuntingYard 					yard; 			// The shunting yard 
	private TreeSet<ObservedMoment> 		moments; 		// All moments for all trains, stored in order of their occurance
	
	HashMap<Train, ObservedMoment> 			firstMoments; 	// The first moment for each train. 
	private boolean 						debuggingOutput; 
	
	private int 							highestId; 		// If we want to add moments without specifying the timePoint, we need the highest current id. 
	private HashMap<Integer,ObservedMoment> allTimePoints;	// We must verify that no two moments contain the same timePoint
	
	protected MomentAdder add; 
	
	private NsspInstance() {
		this.add = new MomentAdder(this); 
	}
	
	protected class MomentAdder {
		private NsspInstance in; 
		
		public MomentAdder(NsspInstance in) {
			this.in = in; 
		}
		
		public void Moment(ObservedMoment m) throws Exception {
			if (in.allTimePoints.containsKey(m.getTimePoint())) {
				throw new Exception("Two timePoints may not be equal"); 
			}
			if(in.highestId < m.getTimePoint()) {
				in.highestId = m.getTimePoint(); 
			}
			in.moments.add(m); 
			in.allTimePoints.put(m.getTimePoint(), m); 
			if (in.firstMoments.containsKey(m.getTrain())) {
				Moment first 		= in.firstMoments.get(m.getTrain());  
				Moment predecessor 	= getLastMoment(first); 
				
				if(predecessor.hasLocationOption(in.yard.getExit())) {
					throw new Exception("Trains may not re-enter the shunting yard. The train leaves on " 
							+ predecessor.getTimePoint() + " and enters again at " + m.getTimePoint()); 
				}
				
				m.setPrevious(predecessor); 
				predecessor.next = m;
				if (predecessor.getTimePoint() >= m.getTimePoint()) {
					throw new Exception("Moments of the same train need to be added in chronological order: " + predecessor.getTimePoint() + " was added before " + m.timePoint ); 
				}
			}
			else {
				in.firstMoments.put(m.getTrain(), m); 
			}
		}
	}
	
	public NsspInstance(ShuntingYard yard) throws Exception {
		this(); 
		this.firstMoments		= new HashMap<>(); 
		this.yard				= yard; 
		this.moments 			= new TreeSet<>(this.comparator());  
		this.debuggingOutput	= false; 
		this.highestId			= -1; 
		this.allTimePoints		= new HashMap<>(); 
		Moment.init(yard);
	}
	
	private Comparator<Moment> comparator() {
		Comparator<Moment> comp = new Comparator<Moment>() {
			@Override
			public int compare(Moment x, Moment y) {
				return compareMoments(x, y); 
			}
		}; 
		return comp; 
	}
	
	private int compareMoments(Moment x, Moment y) {
		int xVal=x.getTimePoint(); 
		int yVal=y.getTimePoint(); 
	    if (xVal < yVal) {
	        return -1;
	    }
	    if (xVal > yVal) {
	        return 1;
	    }
	    return 0;
	}

	public void addNextMoment(Train train, Track locationOption) throws Exception {
		this.addMoment(new ObservedMoment(this.highestId +1, train, locationOption));
	}
	
	public void addNextMoment(Train train,  Collection<Track> locationOptions) throws Exception {
		this.addMoment(new ObservedMoment(this.highestId +1, train, locationOptions));
	}
	
	public void addNextMoment(Train train) throws Exception {
		this.addMoment(new ObservedMoment(this.highestId +1, train, this.getExit()));
	}
	
	public void addMoment(int id, Train train, Track locationOption) throws Exception {
		this.addMoment(new ObservedMoment(id, train, locationOption)); 
	}

	public void addMoment(int id, Train train, Collection<Track> locationOptions) throws Exception {
		ObservedMoment m; 
		if (null == locationOptions) {
			m = new ObservedMoment(id, train, this.getExit()); 
		} else if (locationOptions.size() == 0) {
			m = new ObservedMoment(id, train, this.getExit()); 
		}
		else { 
			m = new ObservedMoment(id, train, locationOptions);
		}
		this.addMoment(m); 
	}

	/**
	 * Add a moment with only the exit as option for its location. 
	 * @param id
	 * @param train
	 * @throws Exception 
	 */
	public void addMoment(int id, Train train) throws Exception {
		ObservedMoment m = new ObservedMoment(id, train, this.getExit()); 
		this.addMoment(m); 
	}
	
	public ObservedMoment addMomentForceUniqueTimePoint(int timePoint, Train train, Collection<Track> locationOptions) throws Exception {
		ObservedMoment m = new ObservedMoment(timePoint, train, locationOptions);
		this.addMomentForceUniqueTimePoint(m); 
		return m; 
	}
	
	public void addMomentForceUniqueTimePoint(int timePoint, Train train, Track locationOption) throws Exception {
		this.addMomentForceUniqueTimePoint(new ObservedMoment(timePoint, train, locationOption)); 
	}
	
	public ObservedMoment addMomentForceUniqueTimePoint(int timePoint, Train train) throws Exception {
		ObservedMoment m = new ObservedMoment(timePoint, train, this.getExit()); 
		this.addMomentForceUniqueTimePoint(m); 
		return m; 
	}
	
	/**
	 * This methods adds a moment, but if the timePoint is not unique the method
	 * tries if the timePoint +1 or +2 can be added instead. 
	 * 
	 * @param m
	 */
	private void addMomentForceUniqueTimePoint(ObservedMoment m) throws Exception {
		int timePoint = m.getTimePoint(); 
		for (int i=0; i<3; i++) {
			try {
				m.setTimePoint(timePoint + i); 
				this.addMoment(m);
				return; 
			} catch (Exception e) {
				if (!e.getMessage().equals("Two timePoints may not be equal")) {
					throw e; 
				}
			}
		}
		throw new Exception("TimePoint +1 or +2 already existed. Trying to add timePoint " + m.getTimePoint()); 
	}

	
	public void addMoment(ObservedMoment m) throws Exception {
		this.add.Moment(m);
	}
	
	
	/**
	 * Returns the first moment of each train in the schedule. The list is
	 * sorted, the moment which happens first in time, occurs first in the list.
	 * 
	 * @return
	 */
	public ArrayList<ObservedMoment> getFirstMoments() {
		ArrayList<ObservedMoment> list = new ArrayList<>( this.firstMoments.values()); 
		Collections.sort(list, this.comparator()); 
		return list; 
	}
	
	public TreeSet<ObservedMoment> getMoments(){
		return this.moments; 
	}
	
	private Moment getLastMoment(Moment m) {
		Moment next = m; 
		while (next.next != Moment.end) {
			next = next.next; 
		}
		return next; 
	}
	
	/**
	 * Returns the moment happening on the indicated timepoint
	 * 
	 * @param timePoint
	 * @return
	 */
	public ObservedMoment getMoment(int timePoint) {
		return this.allTimePoints.get(timePoint); 
	}
	
	public ShuntingYard getShuntingYard() {
		return this.yard;
	}

	public int numberOfMoments() {
		return this.moments.size();
	}

	public Track getTrack(String name) {
		return this.yard.getTrack(name);
	}

	public Side getExit() {
		return this.yard.getExit();
	}

	/** 
	 * Return the sum of the route cost for each moment.  
	 * @return
	 * @throws Exception 
	 */
	public int getCost() throws Exception {
		int cost = 0; 
		for (Moment m : this.getMoments()) {
			if (m.hasRoute()) {
				cost += m.getRouteCost(); 
			}
		}
		return cost;
	}

	/**
	 * For debugging purposes we want to initialize a moment on a specific
	 * location. The route to this location is not calculated here, the tusp
	 * solver is able to handle initialized moments and only calculate their
	 * routes.
	 * 
	 * @param i
	 * @param from
	 * @param to
	 * @throws Exception 
	 */
	public void initMomentRoute(int i, Side from, Side to, Search search) throws Exception {
		ObservedMoment m = this.getMoment(i); 
		Route r;
		r = new Route(new Location(from), new Location(to));
		m.setRoute(r);
		if(search.equals(Search.normal)) {
			m.removeOption(to);
		}
	}

	public void setDebugging(boolean b) {
		this.debuggingOutput = b;
	}

	public boolean debugOutput() {
		return this.debuggingOutput;
	}

	public ArrayList<Integer> getAllTimePoints() {
		ArrayList<Integer> timePoints = new ArrayList<>(this.allTimePoints.keySet()); 
		Collections.sort(timePoints);
		return timePoints;
	}

	public Moment getLastMomentOfTrain(Train t) {
		Moment m = this.getFirstMomentOfTrain(t); 
		if (null != m) {
			while (!m.getNext().isEndMoment()) {
				m = m.getNext(); 
			}
		}
		return m;
	}

	private Moment getFirstMomentOfTrain(Train t) {
		Collection<ObservedMoment> moments = this.getFirstMoments(); 
		Moment first = null; 
		for (Moment m : moments) {
			if (m.getTrain().equals(t)) {
				first = m; 
			}
		}
		return first;
	}

	public int getNofMoments() {
		return this.moments.size();
	}

	public Moment getLastArrivedMoment() {
		Collection<ObservedMoment> moments = this.getFirstMoments(); 
		Moment last = null; 
		for (Moment m : moments) {
			if (null == last) {
				last = m; 
			} else {
				if (last.getTimePoint() < m.getTimePoint()) {
					last = m; 
				}
			}
		}
		return last;
	}

	public void changeMomentTimePoint(int now, int changed) {
		ObservedMoment m = this.allTimePoints.remove(now); 
		m.setTimePoint(changed);
		this.allTimePoints.put(changed, m); 
	}

	public int getNofTrains() {
		return this.firstMoments.size();
	}

	public void restoreInitialLocationOptions() {
		for(Moment m : this.moments) {
			m.restoreInitialLocationOptions(); 
		}
	}

	public Moment getFirstMoment(Train t) throws Exception {
		if (!this.firstMoments.containsKey(t)) {
			throw new Exception("This instances does not contain train: " + t.getName()); 
		}
		return this.firstMoments.get(t);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("TUSP instance: \n"); 
		
		//print a line for each train
		for(Moment m : this.getFirstMoments()) {
			m.toString(); 
			//sb.append(ConsoleOut.trainToString(m) + "\n"); 
			//TO DO: implement toString method
			sb.append("Not implemented :-(\n"); 
		}
		
		// Add index
		sb.append("\t" + this.line(this.moments.size()) + "\n"); 
		for (int i=0; i<this.moments.size(); i++) {
			sb.append("\t" + i); 
		}
		sb.append("\n"); 
		
		return sb.toString(); 
	}

	private String line(int length) {
		StringBuilder sb = new StringBuilder(); 
		for (int i=0; i<length; i++) {
			sb.append("______\t"); 
		}
		return sb.toString();
	}

	public NsspInstance copy() throws Exception {
		NsspInstance copy = new NsspInstance(this.yard); 
		copy.setDebugging(this.debuggingOutput);
		for (ObservedMoment m : this.moments) {
			ObservedMoment copyM = m.copy(); 
			copy.addMoment(copyM);
		}
		return copy;
	}
	
}
