package trainRoutingProblem;

import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import shuntingYard.Side;
import solutionSpace.SolutionSet;
import solutionSpace.TrackSolution;

/**
 * This class constructs a solutionSet which consists of only TrackSolutions. 
 * 
 * @author daan.vandenheuvel
 *
 */
public class TrpSolutionSet extends SolutionSet{


	public TrpSolutionSet(TrpMoment m, DijkstraOutput out) throws Exception {
		TrackSolutionSpace options = m.getBaseMoment().getOptions(); 
		for (Side option : options){
			TrackSolution s = new TrackSolution(out, option); 
			this.add(s);
		}
	}

	public TrpSolutionSet (TrpMoment m, 
			DijkstraGraph firstRoute,
			DijkstraGraph lastRoute) throws Exception {
		super(); 
		DijkstraOutput d = new DijkstraOutput(m.getBaseMoment(), m.getNext().getBaseMoment()); 
		d.firstRoutes = firstRoute; 
		d.lastRoutes = lastRoute; 
		TrackSolutionSpace options = m.getBaseMoment().getOptions(); 
		for (Side option : options){
			TrackSolution s = new TrackSolution(d, option); 
			this.add(s);
		}
	}
	
	/**
	 * The constructor for all solutions of the previous to the current
	 * location. Not taking into account the next route or the observers.
	 * 
	 * @param m
	 * @param first
	 * @param routeCost
	 * @throws Exception 
	 */
	public TrpSolutionSet(TrpMoment m, DijkstraGraph first) throws Exception {
		this(m, first, null); 
	}
	
}