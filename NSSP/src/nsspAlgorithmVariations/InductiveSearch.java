package nsspAlgorithmVariations;

import java.util.ArrayList;
import java.util.Collection;

import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.NsspSolver;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.Subproblem;
import shuntingYard.Side;
import solutionSpace.Solution;
import solutionSpace.SolutionSet;
import solutionSpace.SpaceTrackSolution;
import trainLocationProblem.TrackConsistentMaker;

public class InductiveSearch extends NsspSolver{
	
	/**
	 * The maximum amount of scheduled Moments for the given instance.
	 */
	private int maxScheduledMoments;
	
	/**
	 * The order in which this inductive search schedules the moments.
	 */
	private Order order; 
	public static enum Order {OCCURANCE, TRAINARRIVAL}
	
	public InductiveSearch(NsspInstance instance, AlgSettings settings, Order order) throws Exception {
		super(instance, settings); 
		this.maxScheduledMoments 	= -1; 
		this.order					= order; 
	}
	
	@Override
	public void createInitialSchedule() {
		// Do nothing
	}
	
	@Override 
	public boolean solve() throws Exception {
		NsspInstance previousState; 
		NsspInstance current = new NsspInstance(super.getInstance().getShuntingYard()); 
		current.setDebugging(DEBUG);
		
		Collection<ObservedMoment> moments = this.momentsInOrder(); 
		for (ObservedMoment m : moments) {
			previousState 	= current; 
			
			if (m.isFirstMoment()) { 
				super.initialInstance	= previousState; 		// Store current initial state for testing purposes
			}
			
			current			= previousState.copy(); 
			current.addMoment(m.copy());
			current.setDebugging(DEBUG);
			NsspSolver ns 	= initialize(current, previousState, this); 
			boolean b 		= ns.solve(); 
			this.debugPrint(ns);
			if (!b) {
				this.maxScheduledMoments = previousState.getMoments().size(); 
				this.setSolution(previousState);
				return false; 
			}
		}
		this.maxScheduledMoments = current.getMoments().size(); 
		this.setSolution(current);
		return true; 
	}
	
	public static NsspSolver initialize(NsspInstance current, NsspInstance previousState, AlgSettings settings) throws Exception {
		current.restoreInitialLocationOptions();
		NsspSolver ns 	= new NsspSolver(current, settings); 
		
		Subproblem sp = ns.getTrp(); 
		for (Moment m : current.getMoments()) {
			
			SolutionSet ss = sp.schedule(m); 
			ns.assignCost(sp, ss);
			
			Solution s = null; 
			if (null != previousState.getMoment(m.getTimePoint())) {
				s = getCorrectSolution(ss, previousState); 
			} 
			else {
				s = ss.getBestSolution(); 
			}
			
			if (s instanceof SpaceTrackSolution) {
				SpaceTrackSolution t = (SpaceTrackSolution) s; 
				t.executeFirst(settings.getSearch()); 
			} else {
				ss.executeBest(settings.getSearch()); 
			}

			if (TrackConsistentMaker.isNotConsistent()) {
				TrackConsistentMaker.makeConsistent(ns);
			}
		}
		
		ns.getTlp().createFreeRelocations();
		
		return ns;
	}

	private static Solution getCorrectSolution(SolutionSet ss,
			NsspInstance previousState) throws Exception {
		ArrayList<Solution> solutions	= ss.getSolutions(); 
		Moment m 				= previousState.getMoment(ss.getSolutions().get(0).getMoment().getTimePoint()); 
		Side scheduledAt		= m.getArrivingSide(); 
		
		Solution secondOption	= null; 
		
		for (Solution t : solutions) {
			if (t instanceof SpaceTrackSolution) {
				SpaceTrackSolution sts 	= (SpaceTrackSolution) t; 
				Side option 			= sts.getOption(); 
				if (scheduledAt.equals(option)) {
					return sts; 
				}
				if (scheduledAt.getOtherSide().equals(option)) {
					secondOption		= sts; 
				}
			} 
			else {
				throw new Exception("We only expect space track solutions, not " + t.getClass()); 
			}
		}
		if (null != secondOption) {
			return secondOption; 
		}
		throw new Exception("At least one solution should have been executed. " 
						+ "The option " + scheduledAt.toString() + " of " + m.toString() + " could not be found in " 
						+ ss.toString()); 
	}

	private Collection<ObservedMoment> momentsInOrder() throws Exception {
		switch (this.order) {
		case OCCURANCE : 
			return this.momentsInOrderOfOccurance();
		case TRAINARRIVAL : 
			return this.momentsInOrderOfTrainArrival(); 
		}
		throw new Exception("Unknown order type " + this.order); 
	}

	private Collection<ObservedMoment> momentsInOrderOfOccurance() {
		return super.getInstance().getMoments();
	}
	
	private Collection<ObservedMoment> momentsInOrderOfTrainArrival() {
		ArrayList<ObservedMoment> order = new ArrayList<>(); 
		for (ObservedMoment m : super.instance.getFirstMoments()) {
			while (!m.isEndMoment()) {
				order.add(m); 
				Moment moment = m.getNext(); 
				m = super.instance.getMoment(moment.getTimePoint()); 
				if (null == m) {
					break; 
				}
			}
		}
		return order;
	}

	private void setSolution(NsspInstance schedule) throws Exception {
		for (ObservedMoment m : super.instance.getMoments()) {
			Moment copy = schedule.getMoment(m.getTimePoint()); 
			if(null == copy) {
				// The total amount of moments in the schedule might be less then the amount of input moments. 
				// In this case no solution to the complete schedule is found. 
				return; 
			}
			
			this.instantiateMoment(m, copy); 
		}
	}

	private void instantiateMoment(ObservedMoment m, Moment copy) throws Exception {
		m.setRoute(copy.getRoute());
		m.setRouteCost(copy.getRouteCost());
		m.makeConsistent();
	}

	public int getMaxScheduledMoments() {
		return this.maxScheduledMoments; 
	}
	
	public void debugPrint(NsspSolver ns) {
		if (DEBUG) {
			System.out.println("____________________________________________Inductive Search Iteration____________________________________________");
			System.out.println(ns.toString());
		}
	}
	
}
