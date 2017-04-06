package trainRoutingProblem;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.NsspInstance;
import solutionSpace.SolutionSet;

public class RouteConflict {

	private TrpMoment first; 
	private TrpMoment last;
	
	public RouteConflict(TrpMoment first, TrpMoment last) {
		this.first = first; 
		this.last = last; 
	}
	
	public TrpMoment getFirst() {
		return this.first; 
	}
	
	public TrpMoment getLast() {
		return this.last; 
	}
	
	public boolean hasFlexibility() {
		return (first.getBaseMoment().hasLocationOptions() || 
				last.getBaseMoment().hasLocationOptions() || 
				last.getPrevious().getBaseMoment().hasLocationOptions());
	} 
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(first.getTimePoint()); 
		sb.append("-"); 
		sb.append(last.getTimePoint()); 
		return sb.toString(); 
	}

	public SolutionSet calculateSolutionSet(NsspInstance in) throws Exception {
		TrpSolver rcs = new TrpSolver(in); 
		SolutionSet ss 			= rcs.solve(this); 
		return ss;
	}

	public ArrayList<TrpMoment> getContributingMoments() {
		//This is the set of moments which location can be changed to solve the conflict. 
		ArrayList<TrpMoment> solutionOptions = new ArrayList<>(); 
		// Change the location of the first moment 
		if (this.getFirst().getBaseMoment().hasLocationOptions()) {
			solutionOptions.add(this.getFirst()); 
		}
		// Change the location of the second moment
		if (this.getLast().getBaseMoment().hasLocationOptions()) {
			solutionOptions.add(this.getLast()); 
		}
		// Change the location the moment before last (of the same train) 
		if (this.getLast().getPrevious().getBaseMoment().hasLocationOptions()) {
			solutionOptions.add(this.getLast().getPrevious());
		}
		return solutionOptions;
		
	}
}
