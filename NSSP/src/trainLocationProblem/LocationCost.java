package trainLocationProblem;

import java.util.ArrayList;
import java.util.HashMap;

import nedtrainSchedulingShuntingProblem.Moment;
import shuntingYard.Side;
import shuntingYard.Track;
import solutionSpace.SpaceSolution;
import solutionSpace.SpaceSolutionAssembly;

public class LocationCost {
	
	private int spaceConsumedCost;
	private int conflictCost;
	private int freeRelocationCost; 
	
	/**
	 * A lookup table to indicate which moments are mapped to which TlpMoments.
	 */
	private HashMap<Moment, TlpMoment> momentsMap; 
	
	public LocationCost() {
		this.spaceConsumedCost	= -1;
		this.freeRelocationCost	= -1000; 
		this.conflictCost		= 1000000;
	}
	
	public void setMomentsMap(TlpInstance instance) {
		this.momentsMap			= instance.getMomentsHashMap(); 
	}
	
	public int costSpaceSolutionAssembly(TlpMoment changed, Side oldSide,
			Side newSide, HashMap<TlpMoment, Integer> changedCoordinates) throws Exception {
		
		int moreSpaceConsumed 	= this.calculateMoreSpaceConsumed(changed, oldSide, newSide, changedCoordinates); 
		int moreConflicts		= this.calculateMoreSpaceConflicts(changed, oldSide, newSide, changedCoordinates);
		int moreFreeRelocations	= this.calculateMoreFreeRelocations(changed, oldSide, newSide, changedCoordinates);
		int cost = 	moreSpaceConsumed 		* this.spaceConsumedCost
					+ moreConflicts 		* this.conflictCost
					+ moreFreeRelocations 	* this.freeRelocationCost; 
		return cost;
	}

	private int calculateMoreFreeRelocations(TlpMoment changed, Side oldSide,
			Side newSide, HashMap<TlpMoment, Integer> changedCoordinates) throws Exception {
		MomentLocator ml = new MomentLocator(); 
		int freeRelocations = 0; 
		ArrayList<TlpMoment> momentsAtSameTime = changed.getMomentsAtSameTime();
		

		// If the changed moment could be located for free on the new Node it will have one less freeRelocation. 
		if (changed.couldBeLocatedForFreeOn(newSide)) {
			freeRelocations --; 
		}
		if (changed.couldBeLocatedForFreeOn(newSide.getOtherSide())) {
			freeRelocations --; 
		}
		
		
		for (TlpMoment m : momentsAtSameTime) {
			
			if (m.hasLocationOption(oldSide)) {
				if (!m.couldBeLocatedForFreeOn(oldSide)
						&& !m.getTrack().equals(oldSide.getTrack())) {
					SpaceSolutionAssembly s = ml.locateMomentAtSideAfterChanges(m, oldSide, changed, newSide, changedCoordinates); 
					if (solutionIsFreeRelocation(s, oldSide, changedCoordinates, changed, newSide)) {
						freeRelocations ++; 
					}
				}
			}
			if (m.hasLocationOption(oldSide.getOtherSide())) {
				if (!m.couldBeLocatedForFreeOn(oldSide.getOtherSide()) 
						&& !m.getTrack().equals(oldSide.getTrack())) {
					SpaceSolutionAssembly s = ml.locateMomentAtSideAfterChanges(m, oldSide.getOtherSide(), changed, newSide, changedCoordinates); 
					if (solutionIsFreeRelocation(s, oldSide.getOtherSide(), changedCoordinates, changed, newSide)) {
						freeRelocations ++; 
					}
				}
			}
			

			if (m.hasLocationOption(newSide)) {
				if (m.couldBeLocatedForFreeOn(newSide)) {
					SpaceSolutionAssembly s = ml.locateMomentAtSideAfterChanges(m, newSide, changed, newSide, changedCoordinates); 
					if (!solutionIsFreeRelocation(s, newSide, changedCoordinates, changed, newSide)) {
						freeRelocations --; 
					}
				}
			}
			if (m.hasLocationOption(newSide.getOtherSide())) {
				if (m.couldBeLocatedForFreeOn(newSide.getOtherSide())) {
					SpaceSolutionAssembly s = ml.locateMomentAtSideAfterChanges(m, newSide.getOtherSide(), changed, newSide, changedCoordinates); 
					if (!solutionIsFreeRelocation(s, newSide.getOtherSide(), changedCoordinates, changed, newSide)) {
						freeRelocations --; 
					}
				}
			}
			
		}
		return freeRelocations;
	}

	/**
	 * If one or more moments are shifted such that either a new conflict is
	 * created or an existing conflict is shifted more to the right, this method
	 * returns false. If no moments need to be shifted such that the complete
	 * problem get other conflicts or existing conflicts become more difficult, 
	 * this method returns true.
	 * 
	 * @param s
	 *            The solution.
	 * @param option
	 *            The location to which the baseMoment (s.getMoment) is located.
	 * @param changedCoordinates
	 *            all coordinates that where changed before the solution s.
	 * @param moments
	 *            A HashMap mapping all moments given in s to TlpMoments within
	 *            the TLP.
	 * @param earlierChangedMoment
	 * @param earlierLocatedOn
	 * @return
	 */
	public boolean solutionIsFreeRelocation(SpaceSolutionAssembly s,
			Side option, HashMap<TlpMoment, Integer> changedCoordinates,
			TlpMoment earlierChangedMoment, Side earlierLocatedOn)  throws Exception{

		Moment qMoment 			= s.getMoment(); 
		TlpMoment qTlpMoment	= this.momentsMap.get(qMoment); 
		// If q is going to be located outSide of the track
		if (s.getLastCoordinate() > option.getTrack().getLength()) {
			return false; 
		}
		
		for (SpaceSolution q : s.getOtherSolutions()) {
			qMoment		= q.getMoment(); 
			qTlpMoment	= this.momentsMap.get(qMoment); 
			Track locatedOn = (qTlpMoment.equals(earlierChangedMoment)) ? earlierLocatedOn.getTrack() : qTlpMoment.getTrack(); 
			int trackLength = locatedOn.getLength(); 
			// If q is going to be located outSide of the track
			if (q.getLastCoordinate() > trackLength) { 
				int previousCoordinate = qTlpMoment.getCoordinate(); 
				// If the location of q changed or q shifted on the same location (before solution s), it's new coordinate is given in changedCoordinates
				if (changedCoordinates.containsKey(qTlpMoment)) {
					previousCoordinate = changedCoordinates.get(qTlpMoment); 
				}
				// If the new coordinate is higher than it was before (making the conflict worse)
				if (q.getFirstCoordinate() > previousCoordinate) {
					return false; 
				}
			}
		}
		
		return true;
	}

	private int calculateMoreSpaceConflicts(TlpMoment changed, Side oldSide,
			Side newSide, HashMap<TlpMoment, Integer> changedCoordinates)  throws Exception{
		int conflicts 	= 0; 
		for (TlpMoment m : changedCoordinates.keySet()) {
			int newCoordinate 	= changedCoordinates.get(m); 
			Track newTrack 		= m.equals(changed) ? newSide.getTrack() : m.getTrack(); 
			if (newCoordinate + m.getTrainLength() > newTrack.getLength()) {
				conflicts ++;
			}
			if (m.getCoordinate() + m.getTrainLength() > m.getTrackLength()) {
				conflicts --; 
			}
		}
		return conflicts;
	}

	/**
	 * Calculates the difference between the current space consumed and the
	 * amount of space consumed if we will execute this solution. The outcome is
	 * positive if more space is consumed and negative if less space is
	 * consumed.
	 * 
	 * @param changed
	 * @param oldSide
	 * @param newSide
	 * @param changedCoordinates
	 * @return
	 */
	private int calculateMoreSpaceConsumed(TlpMoment changed, Side oldSide,
			Side newSide, HashMap<TlpMoment, Integer> changedCoordinates)  throws Exception{
		int spaceConsumed 	= 0; 
		for (TlpMoment m : changedCoordinates.keySet()) {
			int newCoordinate 	= changedCoordinates.get(m); 
			Track newTrack 		= m.equals(changed) ? newSide.getTrack() : m.getTrack(); 
			spaceConsumed 	-= m.getSpaceConsumed(); 
			spaceConsumed 	+= this.spaceConsumed(m, newTrack, newCoordinate); 
		}
		return spaceConsumed;
	}

	private int spaceConsumed(TlpMoment m, Track track, int newCoordinate) {
		// cost = max( 0, min( trackLength, coordinate + trainLength) - coordinate )
		int lastCoordinate 		= newCoordinate + m.getTrainLength(); 
		int trackLength 		= track.getLength(); 
		int minLastCoordinate 	= (lastCoordinate < trackLength) ? lastCoordinate : trackLength; 
		int consumed			= minLastCoordinate - newCoordinate; 
		return (0 > consumed) ? 0 : consumed;
	}

	public int getConflictCost() {
		return this.conflictCost;
	}

	public int getFreeRelocationCost() {
		return this.freeRelocationCost;
	}

	public int getSpaceConsumedCost() {
		return this.spaceConsumedCost;
	}

	public void setFreeRelocationCost(int cost) {
		this.freeRelocationCost = cost; 
	}

	public void setSpaceConsumedCost(int cost) {
		this.spaceConsumedCost = cost; 
	}

	public void setConflictCost(int cost) {
		this.conflictCost = cost; 
	}

}
