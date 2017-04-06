package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;
import java.util.Collection;

import mspInstance.Train;
import shuntingYard.Route;
import shuntingYard.Side;
import shuntingYard.Track;

/**
 * This class extends the moment class and only adds a list of observers. All
 * these observers have their own data structure which is based on the data
 * within ObservedMoments. Whenever one sub problem changes data from an
 * ObservedMoment, all other observers are notified and must adjust their own
 * data structure.
 * 
 * @author daan.vandenheuvel
 *
 */
public class ObservedMoment extends Moment{
	
	ArrayList<MomentObserver> observers; 

	public ObservedMoment(int id, Train train, Collection<Track> possibleLocation) {
		super(id, train, possibleLocation);
		this.observers = new ArrayList<>(); 
	}
	
	public ObservedMoment(int id, Train train, Track locationOption) {
		super(id, train, locationOption); 
		this.observers = new ArrayList<>(); 
	}

	public ObservedMoment(int id, Train train, Side exitSide) {
		super(id, train, exitSide); 
		this.observers = new ArrayList<>(); 
	}

	private ObservedMoment(int timePoint, Train train,
			TrackSolutionSpace trackSolutionSpace) {
		super(timePoint, train, trackSolutionSpace); 
		this.observers = new ArrayList<>(); 
	}

	public void addObserver(MomentObserver m) {
		this.observers.add(m); 
	}


	public void setRoute(Route r) throws Exception {
		if (null == r) throw new Exception("route is set to null");
		super.route = r; 
		this.notifyObservers();
	}
	
	private void notifyObservers() throws Exception{
		for(MomentObserver mo : this.observers) {
			mo.momentIsChanged(this);
		}
	}

	public void clearObservers() {
		this.observers.clear();
	}
	
	@Override 
	public void removeOption(Side l) throws Exception {
		super.removeOption(l);
		this.notifyObservers();
	}

	public void setTrackCoordinate(int coordinate) throws Exception {
		super.getLocation().setCoordinate(coordinate);
		this.notifyObservers();
	}

	public int getFirstCoordinate() throws Exception {
		int c = 0; 
		c = this.getLocation().getCoordinate();
		return c; 
	}

	public int getLastCoordinate() throws Exception {
		int c = this.getFirstCoordinate(); 
		c += this.getTrainLength(); 
		return c; 
	}

	public void setTimePoint(int timePoint) {
		super.timePoint = timePoint; 
	}

	public Moment getPrevious() throws Exception {
		if (null == this.previous) {
			throw new Exception("previous was not initialized for moment " + this.timePoint); 
		}
		return this.previous;
	}

	public void setPrevious(Moment predecessor) {
		super.previous = predecessor; 
		
	}

	public Moment getNext() {
		return this.next;
	}

	public void makeConsistent() throws Exception{
		for (MomentObserver m : this.observers) {
			m.makeConsistent(); 
		}
	}

	public void removeObserver(MomentObserver m) throws Exception {
		if (!this.observers.contains(m)) {
			throw new Exception("This momentObserver" + m.toString() + " was not in the list of observer. "); 
		}
		this.observers.remove(m); 
	}

	/**
	 * Method for debugging purposses. Returns the observers of this moment. 
	 * @return
	 */
	public ArrayList<MomentObserver> getObservers() {
		return this.observers; 
	}

	public ObservedMoment copy() {
		ObservedMoment copy = new ObservedMoment(super.getTimePoint(), super.getTrain(), super.getTrackSolutionSpace());
		return copy;
	}
	
}