package trainLocationProblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import shuntingYard.Location;
import shuntingYard.Side;
import shuntingYard.Track;
import solutionSpace.SpaceSolutionAssembly;
import trainLocationProblem.TrackConsumers.OccupiedSides;

public class TlpMoment {
	
	private Location			location;	// The track on which this moment is stored. 
	private TlpMomentFactory	factory; 	// The factory which created this moment.
	
	/**
	 * The amount of conflicts enforced by this moment. We want the conflict
	 * cost to be as low as possible. This moment has +1 conflictCost for every
	 * jump it has to make and +1 if it exceeds its trackLength. it's track
	 * length.
	 */
	private int	conflicts; 
	
	/**
	 * The amount of tracks to which this moment can be relocated to, without
	 * enforcing new conflicts. Only tracks which are still in the solution
	 * space are taken into account. A high amount of freeRelocations means that
	 * much flexibility is left within the current instance-state.
	 */
	//private int freeRelocations; 
	private HashSet<Side> freeRelocations; 
	
	private Track trackWhereTrainWasAdded; 
	private Track trackWhereTrainWasRemoved; 
	
	/**
	 * The amount of track consumed by this moment. This is equal to the train
	 * length if this moment does not exceed the trackLength. We want the
	 * spaceConsumed cost to be as high as possible, this means that the
	 * complete train is scheduled within the borders of the track.
	 * 
	 * cost = max( 0, min( trackLength, coordinate + trainLength) - coordinate )
	 */
	private int spaceConsumed; 
	
	private LocationCost locationCost; 
	
	public TlpMoment(Location track, TlpMomentFactory factory, LocationCost locationCost) {
		this.location					= track; 
		this.factory					= factory; 
		this.freeRelocations			= new HashSet<>(); 
		this.locationCost				= locationCost;
		this.trackWhereTrainWasAdded	= null; 
		this.trackWhereTrainWasRemoved	= null; 
	}

	public TrackConsumers getTrackConsumers() {
		return this.factory.getTrackConsumers();
	}

	public Track getTrack() throws Exception{
		return this.location.getTrack();
	}

	public int getCoordinate() {
		return this.location.getCoordinate();
	}

	public int getTimePoint() {
		return this.factory.getMoment().getTimePoint();
	}

	public int getTrainLength() {
		return this.factory.getMoment().getTrainLength();
	}

	public boolean isOnCoordinate(int y) {
		boolean b = false; 
		if ((this.getCoordinate() <= y) && (this.getCoordinate() + this.getTrainLength() > y)) {
			b = true; 
		}
		return b;
	}

	public String getTrainName() {
		return this.factory.getMoment().getTrain().getName();
	}

	public boolean hasTrackAssigned() {
		return !Location.dummy().equals(this.location);
	}

	public ObservedMoment getObservedMoment() {
		return this.factory.getMoment(); 
	}

	public ArrayList<TlpMoment> getMomentsAtSameTimeAtSameTrack() throws Exception{
		return getMomentsAtTrack(this.getTrack()); 
	}

	public Side getArrivingSide() {
		return this.location.getArrivingSide();
	}

	public ArrayList<TlpMoment> getMomentsAtTrack(Track track) {
		TlpMomentFactory thisfactory		= this.factory; 
		TrackConsumers consumersPerTrack	= thisfactory.getTrackConsumers(); 
		OccupiedSides os					= consumersPerTrack.get(track); 
		ArrayList<TlpMoment> moments		= os.getAllMoments(); 
		return moments; 
	}
	
	public String toString() {
		return "moment: " + this.factory.getMoment().getTimePoint();
	}

	public void addMomentToTrackConsumers(TlpMoment m) throws Exception {
		this.factory.addMomentToTrackConsumers(m); 
	}

	/**
	 * @param changedMoment
	 * @param oldTrack
	 * @param newTrack
	 * @throws Exception
	 */
	public void relocateTrackConsumer(TlpMoment changedMoment, Track oldTrack, Track newTrack) throws Exception {
		if (this.freeRelocationsAreNotUpdated()) {
			if (!oldTrack.equals(newTrack)) {
				throw new Exception("We have to calculate the new freeRelocations of " + this.toString() 
					+ "\n before we can adjust the tracks of " + changedMoment.toString() 
					+ " from " + oldTrack.toString() + " to " + newTrack
					+ ". \n The track where a train was removed is " + this.trackWhereTrainWasRemoved
					+ ", the track where a train was added is " + this.trackWhereTrainWasAdded); 
			}
		}
		this.factory.getTrackConsumers().relocate(changedMoment, oldTrack, newTrack);

		this.trackWhereTrainWasRemoved	= oldTrack; 
		this.trackWhereTrainWasAdded	= newTrack; 
	}

	public boolean freeRelocationsAreNotUpdated() {
		return ((null != this.trackWhereTrainWasAdded) || (null != this.trackWhereTrainWasRemoved));
	}

	private void setLocation(Location l) throws Exception {
		this.location = l.copy(); 
	}

	public int getTrackLength() throws Exception{
		return this.getTrack().getLength();
	}

	public int getEndTime() {
		return this.factory.getEndTime(this);
	}

	public TrackSolutionSpace getSideOptions() {
		return this.factory.getSideOptions(this);
	}

	public boolean exceedsTrackLength() throws Exception{
		int c = this.getCoordinate(); 
		int l = this.getTrainLength(); 
		int t = this.getTrackLength(); 
		return (c + l > t);
	}

	public boolean happensOn(int time) {
		return ((this.getTimePoint() <= time) && (time <= this.getEndTime()));
	}

	public boolean isFirstMomentOfTrain() {
		return this.getObservedMoment().isFirstMoment();
	}

	public Train getTrain() {
		return this.factory.getMoment().getTrain();
	}

	public void baseMomentIsChanged(Location l) throws Exception {
		if (l.getArrivingSide().getTrack().equals(Track.dummy())) {
			// This line may in the future be removed if we decide that it is a good idea to allow this. 
			throw new Exception("We do not allow tracks to be changed back to the dummy track");  
		}

		if (this.getObservedMoment().getPrevious().hasLocation()) {
			if (this.getObservedMoment().getPrevious().getLocation().equals(l)) {
				// We cannot deal with two subsequent moments having the same location
				throw new Exception(this.toString() + " has location " + l.toString() + ", which is the same as it predecessor"); 
			}
		}
		if (!this.getObservedMoment().getNext().isEndMoment() && this.getObservedMoment().getNext().hasLocation()) {
			if (this.getObservedMoment().getNext().getLocation().equals(l)) {
				// We cannot deal with two subsequent moments having the same location
				throw new Exception(this.toString() + " has location " + l.toString() + ", which is the same as it successor"); 
			}	
		}
		this.spaceConsumed 		= this.calculateSpaceConsumed(l); 
		this.conflicts			= this.calculateConflicts(l); 
		
		if (!l.getTrack().equals(this.getTrack())) {
			if (null != this.trackWhereTrainWasAdded || 
					null != this.trackWhereTrainWasRemoved) {
				throw new Exception("We have to calculate the new freeRelocations of " + this.toString() 
				+ ", before we can adjust the tracks of other moments." 
						+ " Removed = " + this.trackWhereTrainWasRemoved.toString()
						+ ", Added = " + this.trackWhereTrainWasAdded.toString()); 
			}
			this.trackWhereTrainWasRemoved	= this.getTrack(); 
			this.trackWhereTrainWasAdded 	= l.getTrack(); 
		}
		
		this.setLocation(l); 
	}

	public void initializeFreeRelocations(HashMap<Moment, TlpMoment> moments) throws Exception {
		this.freeRelocations.clear(); 
		MomentLocator ml = new MomentLocator(); 
		for (Side option : this.getObservedMoment().getOptions()) {
			// We do not count relocating to the same track as free Relocation
			if (!option.getTrack().equals(this.getTrack())) {
				SpaceSolutionAssembly s = ml.locateMomentAt(this, option); 
				boolean b = false; 
				// changedCoordinates is an empty hashmap, since no changes have been made before solution s 
				HashMap<TlpMoment, Integer> changedCoordinates = new HashMap<>(); 
				b = this.locationCost.solutionIsFreeRelocation(s, option, changedCoordinates, this, option); 
				if (b) { 
					this.freeRelocations.add(option);  
				}
			}
		}
	}

	void calculateFreeRelocations() throws Exception {
		if (null == this.trackWhereTrainWasAdded 
				&& null == this.trackWhereTrainWasRemoved) {
			return; 
		}
		if (this.trackWhereTrainWasAdded.equals(Track.dummy())) {
			this.trackWhereTrainWasAdded 	= null; 
			this.trackWhereTrainWasRemoved	= null; 
			return;
		} 
		
		MomentLocator ml = new MomentLocator(); 

		// Since no earlier changes are made before any solution s can be
		// executed, the variables below are empty.
		HashMap<TlpMoment, Integer> changedCoordinates 	= new HashMap<>();
		TlpMoment earlierChangedMoment					= null;
		Side earlierLocatedOn							= null;

		//if this moment could previously not be located on the old track
		if (!this.trackWhereTrainWasRemoved.equals(Track.dummy())) {
			Side option = this.trackWhereTrainWasRemoved.getSideA(); 
			if (!this.freeRelocations.contains(option) 
					&& this.hasLocationOption(option) 
					&& !this.getTrack().equals(option.getTrack())) {
				SpaceSolutionAssembly s = ml.locateMomentAt(this, option); 
				boolean b = this.locationCost.solutionIsFreeRelocation(s, option, changedCoordinates ,
						earlierChangedMoment, earlierLocatedOn);
				// if this moment can now be located on old track
				if (b) { 
					this.freeRelocations.add(option);  
				}
			}
		
			option = this.trackWhereTrainWasRemoved.getSideB(); 
			if (!this.freeRelocations.contains(option) 
					&& this.hasLocationOption(option)
					&& !this.getTrack().equals(option.getTrack())) {
				SpaceSolutionAssembly s = ml.locateMomentAt(this, option); 
				boolean b = this.locationCost.solutionIsFreeRelocation(s, option, changedCoordinates ,
						earlierChangedMoment, earlierLocatedOn);
				if (b) { 
					this.freeRelocations.add(option);  
				}
				
			}
		}
		
		// If the current moment was the moment which was located on the track
		if (this.getTrack().equals(this.trackWhereTrainWasAdded)) {
			this.freeRelocations.remove(this.trackWhereTrainWasAdded.getSideA()); 
			this.freeRelocations.remove(this.trackWhereTrainWasAdded.getSideB()); 
		} else {
		
			//if this moment could previously be located on new track
			Side option = this.trackWhereTrainWasAdded.getSideA(); 
			if (this.freeRelocations.contains(option)) {
				SpaceSolutionAssembly s = ml.locateMomentAt(this, option); 
				boolean b = this.locationCost.solutionIsFreeRelocation(s, option, changedCoordinates ,
						earlierChangedMoment, earlierLocatedOn);
				if (!b) {
					this.freeRelocations.remove(option); 
				}
			}
			
			option = this.trackWhereTrainWasAdded.getSideB(); 
			if (this.freeRelocations.contains(option)) {
				SpaceSolutionAssembly s = ml.locateMomentAt(this, option); 
				boolean b = this.locationCost.solutionIsFreeRelocation(s, option, changedCoordinates ,
						earlierChangedMoment, earlierLocatedOn);
				if (!b) {
					this.freeRelocations.remove(option); 
				}
			}
		
		}
		
		// Reset the tracks. 
		this.trackWhereTrainWasAdded 	= null; 
		this.trackWhereTrainWasRemoved	= null; 
	}

	private int calculateConflicts(Location l) throws Exception {
		int total = 0; 
		if (l.getCoordinate() + this.getTrainLength() > l.getTrack().getLength()) {
			total ++; 
		}
		return total;
	}

	private int calculateSpaceConsumed(Location l) throws Exception {
		 // cost = max( 0, min( trackLength, coordinate + trainLength) - coordinate )
		int lastCoordinate 		= l.getCoordinate() + this.getTrainLength(); 
		int trackLength			= l.getTrack().getLength(); 
		int minLastCoordinate	= (lastCoordinate < trackLength) ? lastCoordinate : trackLength; 
		int consumed			= minLastCoordinate - l.getCoordinate(); 
		return (0 > consumed) ? 0 : consumed; 
	}
	
	public int getCost() throws Exception {
		if (null != this.trackWhereTrainWasRemoved) {
			this.calculateFreeRelocations(); 
		}
		return 		this.locationCost.getConflictCost() 		* this.conflicts 
				+ 	this.locationCost.getFreeRelocationCost() 	* this.freeRelocations.size() 
				+ 	this.locationCost.getSpaceConsumedCost() 	* this.spaceConsumed; 
	}

	public int getSpaceConsumed() {
		return this.spaceConsumed;
	}

	public boolean hasLocationOption(Side o) {
		return this.getObservedMoment().hasLocationOption(o);
	}

	public boolean couldBeLocatedForFreeOn(Side s) {
		return this.freeRelocations.contains(s);
	}

	protected TlpMomentFactory getFactory() {
		return this.factory;
	}

	public boolean overlapsWith(TlpMoment that) {
		int firstA 	= this.getTimePoint(); 
		int lastA	= this.getObservedMoment().getNext().getTimePoint(); 
		int firstB 	= that.getTimePoint(); 
		int lastB	= that.getObservedMoment().getNext().getTimePoint(); 
		
		if (firstA < firstB) {
			if (lastA <firstB) {
				return false; 
			} else {
				return true; 
			}
		} else if (firstB < firstA) {
			if (lastB < firstA) {
				return false; 
			} else {
				return true; 
			}
		}
		return true;
	}

	public int numberOfFreeRelocations() {
		return this.freeRelocations.size();
	}
	
	HashSet<Side> getFreeRelocations() {
		return this.freeRelocations; 
	}

	public boolean hasToBeMadeConsistent() {
		return (null != this.trackWhereTrainWasAdded || null != this.trackWhereTrainWasRemoved);
	}

	public ArrayList<TlpMoment> getMomentsAtSameTime() {
		return this.getTrackConsumers().getAllMoments();
	}

	public void ignoreFreeRelocationsUpdate() {
		this.trackWhereTrainWasAdded 	= null; 
		this.trackWhereTrainWasRemoved	= null; 
	}

}
