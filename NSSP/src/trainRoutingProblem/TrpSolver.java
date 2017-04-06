package trainRoutingProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.Subproblem;
import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import shuntingYard.Side;
import shuntingYard.Track;
import solutionSpace.Solution;
import solutionSpace.SolutionSet;
import solutionSpace.SpaceSolution;
import solutionSpace.SpaceSolutionAssembly;
import solutionSpace.SpaceTrackSolution;
import solutionSpace.TrackSolution;

public class TrpSolver implements Subproblem{
	
	private TrpInstance 				instance;
	private Dijkstra 					dijk; 
	private RouteCost					routeCost;
	private ArrayList<RouteConflict> 	routeConflicts; 
	
	private boolean DEBUG; 
	
	private static final Search search = Search.normal; 

	public TrpSolver(NsspInstance ti, RouteCost routeCost) throws Exception {
		this.routeCost 	= routeCost; 
		this.instance 	= new TrpInstance(ti, this.routeCost); 
		this.dijk 		= new Dijkstra(ti.getShuntingYard(), this.routeCost); 
		
		this.removeUnrealisticLocationOptions(); 
		
		
		this.DEBUG		= ti.debugOutput(); 
		this.routeCost.debugPrintInit();
	}

	public TrpSolver(NsspInstance ti) throws Exception {
		this(ti, new RouteCost()); 
		this.routeCost.setStraightCost(1); 
		this.routeCost.setTurnCost(1000); 
		this.routeCost.setObstructionCost(1000000); 
	}
	
	/**
	 * This method removes all location options which do not have an edge
	 * leading to that option.
	 */
	private void removeUnrealisticLocationOptions() {
		TreeSet<TrpMoment> moments 	= instance.getMoments(); 
		Collection<Track> tracks 	= instance.getShuntingYard().getAllTracks(); 
		tracks.remove(instance.getShuntingYard().getInverseExit().getTrack()); 
		for (TrpMoment m : moments) {
			TrackSolutionSpace options = m.getOptions(); 
			for (int i=0; i<options.size(); ) {
				Side option = options.get(i); 
				boolean remove = true; 
				for (Track t : tracks) {
					if (t.getSideA().isConnectedTo(option)) {
						remove = false; 
						break; 
					} 
					if (t.getSideB().isConnectedTo(option)) {
						remove = false; 
						break; 
					} 
				}
				if (remove) {
					options.removeFromInitialOptions(i); 
				} else {
					i++; 
				}
			}
		}
	}

	public void solveTRP() throws Exception {
		this.findConflicts(); 
		while(!(0==routeConflicts.size())) {
			RouteConflict c = this.selectRouteConflict(routeConflicts); 
			SolutionSet s 	= this.solve(c); 
			boolean solved 	= ! s.isEmpty(); 
			
			
			if (solved) {
				this.assignCosts(s);
				
				Solution sol		= s.getBestSolution(); 
				int expectedCost 	= this.instance.getCost() + sol.getCostTrp(); 
				
				if (DEBUG) {
					System.out.print(s.toString() + "\n");
					System.out.println("Selected Option:" + s.bestToString());
				}
				
				sol.execute(search); 
				
				int cost = this.instance.getCost(); 
				if (cost != expectedCost) {
					this.debugPrintSchedule("Terror Schedule:");
					throw new Exception("Expected cost of TRP = " + expectedCost + " but was " + cost ); 
				}
			}
			else {
				return; 
			}
			this.debugPrintSchedule("ADJUSTED SCHEDULE");
			
			this.findConflicts();  
		}
	}

	/**
	 * Solve a single conflict u-v, with u the moment which occurs first. If
	 * u.location == v.location we have options u'-v and u-v' to solve the
	 * conflict. If u.location occurs in v.route, we additionally have the
	 * options pre(v)'-u-v and taking an alternative route from pre(v) to v.
	 * 
	 * @param c
	 * @return 
	 * @throws Exception 
	 */
	public SolutionSet solve(RouteConflict c) throws Exception {
		SolutionSet s = new SolutionSet(); 
		if(!c.hasFlexibility()) {
			return s; 
		}
		ArrayList<TrpMoment> solutionOptions = c.getContributingMoments(); 
		for(TrpMoment m : solutionOptions) {
			SolutionSet s_temp = dijk.doubleDijkstra(m); 
			s.join(s_temp);
		}
		if (s.isEmpty()) throw new Exception("No route was found");
		return s; 
	}

	/**
	 * This method selects a conflict within the Route problem to solve. The
	 * current implementation selects the first conflict which occurs in time.
	 * This is not really based on something, we should do some empirical
	 * research to approve this method.
	 * 
	 * @param routeConflicts
	 * @return
	 * @throws Exception 
	 */
	private RouteConflict selectRouteConflict(ArrayList<RouteConflict> routeConflicts) throws Exception {
		if (0==routeConflicts.size()) throw new Exception("No conflicts left");
		RouteConflict c = routeConflicts.get(0); 
		this.debugPrintConflict(c); 
		return c;
	}

	/**
	 * Finds all conflicts within the current schedule. The list of the
	 * conflicts is saved in this.conflicts. Example: If the route of train x
	 * uses track t on which both trains q and r are stored, then two
	 * routeConflicts q-x and r-x are created.
	 * 
	 * @return
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<RouteConflict> findConflicts() throws Exception {
		ArrayList<RouteConflict> conflicts = new ArrayList<>(); 
		HashMap<Train, TrpMoment> currentMoments = new HashMap<>(); 
		for (TrpMoment m : this.instance.getMoments()) {
			currentMoments.remove(m.getTrain());
			for(TrpMoment current : currentMoments.values()) {
				if(m.getBaseMoment().isBlockedBy(current.getBaseMoment())) {
					conflicts.add(new RouteConflict(current, m)); 
				}
			}
			if (!m.getBaseMoment().hasLocation(instance.getShuntingYard().getExit().getTrack())) {
				currentMoments.put(m.getTrain(), m); 
			}
		}
		this.debugPrintConflicts(conflicts); 
		this.routeConflicts = conflicts; 
		
		return this.routeConflicts; 
	}
	
	/**
	 * Locates each moment on a track which is given in the trackOptions for
	 * that track. Conflicts may exist in the created schedule. Multiple moments
	 * can be scheduled on the same track at the same time.
	 * @throws Exception 
	 */
	@Override
	public void createInitialSchedule() throws Exception {
		
		this.debugPrintSchedule("PRE SCHEDULE");
		
		HashMap<Train, TrpMoment> currentMoments = new HashMap<>(); 
		for (TrpMoment m :this.instance.getMoments()) { 
			currentMoments.remove(m.getTrain()); 
			//m.setBlockedTracks(currentMoments); 
			
			SolutionSet s 	= this.schedule(m.getBaseMoment()); 
			this.assignCosts(s);
			if (DEBUG) System.out.println(s.toString());
			Solution ts 	= s.getBestSolution(); 
			
			if (ts instanceof TrackSolution) {
				((TrackSolution) ts).executeFirst(search);
			} else {
				ts.execute(search);
			}
			
			if (!m.getBaseMoment().hasLocation(instance.getShuntingYard().getExit().getTrack())) {
				currentMoments.put(m.getTrain(), m); 
			}
		}
		this.debugPrintSchedule("INITIAL SCHEDULE");
	}

	public SolutionSet schedule(Moment q) throws Exception {
		TrpMoment m = this.instance.getMoment(q); 
		SolutionSet s 	= null; 
		boolean isLast	= m.isLast();
		if (!isLast) {
			s = this.scheduleInBetween(m);
		} else {
			s = scheduleLast(m); 
		}
		return s;
	}

	/**
	 * The last moment does not have a destination for the next location.
	 * Therefore DoubleDijkstra can not be used to schedule that moment.
	 * 
	 * @param m
	 * @return 
	 * @throws Exception 
	 */
	private SolutionSet scheduleLast(TrpMoment m) throws Exception {
		SolutionSet s = null; 
			if (m.getBaseMoment().hasLocation()) {
				// If moment m was initialized (for debugging purposes) at a
				// specific location, only calculate the route.
				s = dijk.singleFixedLocation(m);
			}
			else {
				s = dijk.singleWithTurns(m);
			}
		return s; 
	}

	/**
	 * Schedule a single moment such that the costs to locate the moment are
	 * minimal and it is located closest to it's destination, without taking a
	 * detour considering its previous and next location. 
	 * 
	 * @param m
	 * @throws Exception 
	 */
	private SolutionSet scheduleInBetween(TrpMoment m) throws Exception {
		if (m.getBaseMoment().hasNoLocationOption() && !m.getBaseMoment().hasLocation()) {
			throw new Exception("No options to schedule " + m.toString()); 
		}
		SolutionSet s = null;
		if (m.getBaseMoment().hasLocation()) {
			// If moment m was initialized (for debugging purposes) at a
			// specific location, only calculate the route.
			s = dijk.singleFixedLocation(m);
		} 
		else {
			s = dijk.doubleDijkstra(m); 
		}
		return s; 
	}
	
	private void debugPrintSchedule(String s) {
		if (DEBUG) {
			System.out.println(s + "\n\n");
			System.out.println(this.instance.toStringExtra()); 
			System.out.println("\n");
		}
	}	
	
	private void debugPrintConflict(RouteConflict c) {
		if (DEBUG) {
			System.out.println("Selected conflict: " + c.toString());
		}
	}


	private void debugPrintConflicts(ArrayList<RouteConflict> conflicts) {
		if (DEBUG) {
			System.out.println(this.conflictsToString(conflicts));
		}
	}

	private String conflictsToString(ArrayList<RouteConflict> conflicts) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("Conflicts: {"); 
		for (RouteConflict c : conflicts) {
			sb.append(c.toString()); 
			sb.append(", "); 
		}
		sb.append("}"); 
		return sb.toString();
	}

	@Override
	public SolutionSet genSolutions() throws Exception {
		RouteConflict c = this.selectRouteConflict(this.routeConflicts); 
		SolutionSet ss = this.solve(c); 
		return ss;
	}

	/**
	 * This method assigns the routeCost of the proposed solutions. If
	 * SpaceTrackSolutions are within the solutionSet, a route is assigned if no
	 * route was already assigned.
	 * @throws Exception 
	 */
	@Override
	public void assignCosts(SolutionSet ss) throws Exception {
		HashMap<ObservedMoment, DijkstraOutput> calculatedRoutes = new HashMap<>(); 
		
		ArrayList<Solution> solutions = ss.getSolutions(); 
		
		for (int i=0; i<solutions.size(); i++) {
			Solution s1 = solutions.get(i); 
			if (s1 instanceof SpaceTrackSolution) {
				SpaceTrackSolution s = (SpaceTrackSolution) s1; 
				if (!s.hasFirstRouteAssigned()) { 
					TrpMoment m = this.instance.getMoment(s.getMoment());  
					if (!calculatedRoutes.containsKey(s.getMoment())) {
						DijkstraOutput routes = dijk.doubleDijkstraRawOutput(m); 
						if (m.isLast()) {
							// The last route and last moment do not exist, so should be null
							routes.lastM 		= null; 
							routes.lastRoutes	= null; 
						}
						calculatedRoutes.put(s.getMoment(), routes);
					}
					DijkstraOutput routes 	= calculatedRoutes.get(s.getMoment()); 
					Side option 			= s.getOption(); 
					TrackSolution ts 		= new TrackSolution(routes, option); 
					s.setTrackSolution(ts); 
				}
				// We also need to check if the other routeCost are already set
				if (!s.getTrackSolution().hasOtherRouteCostAssigned()) {
					int cost = this.calculateOtherRouteCost(s); 
					s.setTrackOtherRouteCost(cost);
				}
			}
			else if (s1 instanceof TrackSolution) {
				TrackSolution s = (TrackSolution) s1; 
				if (s.getOption().getTrack().equals(s.getMoment().getPrevious().getTrack())) {
					// We are currently not able to deal with two subsequent moments to have the same location. 
					// Should be added in the future. 
					solutions.remove(i); 
					i--;
				}
				else if (!s.hasOtherRouteCostAssigned()) {
					int cost = this.calculateOtherRouteCost(s, 0); 
					s.setOtherRouteCost(cost);
				}
			}
		}
	}

	private int calculateOtherRouteCost(TrackSolution s, int coordinate) throws Exception {
		Side option		 	= s.getOption(); 
		TrpMoment m 		= this.instance.getMoment(s.getMoment()); 
		int cost 			= 0; 
		
		// The cost for observers using the option track of this solution
		ArrayList<TrpMoment> observers = m.getObservers(); 
		for (TrpMoment observer : observers) {
			if (observer.hasRoute() && m.hasRoute()) {
				int ifChanged = observer.routeCostIfBlockedChanged(0, m, option, coordinate); 
				cost += ifChanged; 
			}
		}
		
		if (s.hasLastRoute()) {
			cost += this.calculateLastRouteStartCost(new SpaceTrackSolution(new SpaceSolutionAssembly(s.getMoment(), 0, 0), s)); 
		}
		
		return cost;
	}
	
	private int calculateOtherRouteCost(SpaceTrackSolution s) throws Exception {
		Side option		 	= s.getOption(); 
		TrpMoment m 		= this.instance.getMoment(s.getMoment()); 
		int coordinate		= s.getSpaceSolution().getFirstCoordinate(); 
		int cost 			= 0; 

		// The cost for observers using the option track of this solution
		ArrayList<TrpMoment> observers = m.getObservers(); 
		for (TrpMoment q : observers) {
			// If q.prev does not have a location, it does not have a starting
			// point for a route, so no route cost can be calculated.
			if (q.getPrevious().hasLocation()) { 
				int qStartCoordinate	= q.getPrevious().getCoordinate(); 
				ObservedMoment observedM 	= q.getPrevious().getBaseMoment();
				if (s.getSpaceSolution().getOtherSolutionsHashMap().containsKey(observedM)) {
					qStartCoordinate = s.getSpaceSolution().getOtherSolutionsHashMap().get(observedM).getFirstCoordinate(); 
				}
				if (m.getBaseMoment().hasLocation()) {
					int ifChanged = q.routeCostIfBlockedChanged(qStartCoordinate, m, option, coordinate); 
					cost += ifChanged; 
				} else {
				}
			}
		}
		
		if (s.getTrackSolution().hasLastRoute()) {
			cost += this.calculateLastRouteStartCost(s); 
		}
		return cost;
	}
	
	/**
	 * The cost to leave the track given in solution s, can only be calculated
	 * if we know the coordinate on which the moment is located. If this method
	 * is called, the TLP should have calculated how trains need to be located.
	 * We can now calculate if the moment needs to jump over other trains before
	 * it can leave this track. For each jump that needs to be made,
	 * obstructionCost are added to the return value.
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	private int calculateLastRouteStartCost(SpaceTrackSolution s) throws Exception { 

		HashMap<ObservedMoment, SpaceSolution> changed 	= s.getSpaceSolution().getOtherSolutionsHashMap(); 
		TrpMoment m										= this.instance.getMoment(s.getMoment()); 
		int coordinate									= s.getSpaceSolution().getFirstCoordinate(); 
		Side start										= s.getOption(); 
		
		int costA = s.getCostLeaveSideA(); 
		int costB = s.getCostLeaveSideB(); 
		// Calculate cost to leave from side a or side b. 
		if (m.getNext().getBlocked().containsKey(start.getTrack())) {
			HashMap<Integer, TrpMoment> blocked 	= m.getNext().getBlocked().get(start.getTrack()); 
			for (TrpMoment q : blocked.values()) {
				int qCoordinate = q.getCoordinate(); 
				if (changed.containsKey(q.getBaseMoment())) {
					qCoordinate = changed.get(q.getBaseMoment()).getFirstCoordinate(); 
				}
				if (coordinate >= qCoordinate) {
					costA += this.routeCost.getObstructionCost(); 
				}
				if (coordinate <= qCoordinate) {
					costB += this.routeCost.getObstructionCost(); 
				}	
			}
		}
		
		// get the cheapest route and tell the solution which side we have to leave from
		int cheapest = 0; 
		if (costA<costB) {
			s.getTrackSolution().setLeaveFromSideA(); 
			cheapest = costA; 
		}
		else {
			s.getTrackSolution().setLeaveFromSideB(); 
			cheapest = costB; 
		}
		
		// If a previous route was available, substract those cost since that route is not taken anymore. 
		if (m.getNext().getBaseMoment().hasRoute()) {
			int oldCost = m.getNext().getBaseMoment().getRouteCost(); 
			cheapest -= oldCost; 
		}
		
		// Calculate the cost for arriving at the track option. 
		// These costs should always be 0 if the TLP has assigned the correct locations
		
		
		
		Track arrivingAt = start.getTrack(); 
		if (m.getBlocked().containsKey(arrivingAt)) {
			HashMap<Integer, TrpMoment> blockers = m.getBlocked().get(arrivingAt); 
			for (TrpMoment q : blockers.values()) {
				int qCoordinate = q.getCoordinate(); 
				if (s.getSpaceSolution().getOtherSolutionsHashMap().containsKey(q.getBaseMoment())) {
					qCoordinate = s.getSpaceSolution().getOtherSolutionsHashMap().get(q.baseMoment).getFirstCoordinate(); 
				}
				// If arriving at side A
				if (start.isSideA()) {
					if (coordinate + m.getTrain().getLength() > qCoordinate) {
	// we will add these costs so that the initial schedule does not locate everything on a single track
						cheapest += this.routeCost.getObstructionCost(); 
						//"If this coordinate is not 0, the TLP has set the coordinate and this conflict should never exist:\n"
						if (coordinate != 0 || qCoordinate != 0) throw new Exception(
								"\n The coordinate of " + m.toString() + " is " + coordinate + 
								"\n but it overlaps with " + q.toString() + " with coordinate " + qCoordinate +
								"\n for " + s.toString()); 
					}
				} else {
					if (qCoordinate + q.getTrain().getLength() > coordinate) {
	// we will add these costs so that the initial schedule does not locate everything on a single track
						cheapest += this.routeCost.getObstructionCost();  
						//"If this coordinate is not 0, the TLP has set the coordinate and this conflict should never exist:\n"
						if (coordinate != 0 || qCoordinate != 0) throw new Exception(
								"\n The coordinate of " + m.toString() + " is " + coordinate + 
								"\n but it overlaps with " + q.toString() + " with coordinate " + qCoordinate +
								"\n for " + s.toString()); 
						}
				}
			}
		}
		
		return cheapest; 
	}

	/**
	 * We currently only communicate the amount of conflicts, a more complex
	 * method to determine the severity can be implemented later.
	 * @throws Exception 
	 */
	@Override
	public int getProblemSeverity() throws Exception {
		if (this.findConflicts().size() > 0) {
			int cost = this.instance.getCost(); 
			return (cost <= 0) ? 1 : cost; 
		}
		return 0; 
	}

	public RouteCost getRouteCostObject() {
		return this.routeCost;
	}

	public void removeAsObserver() throws Exception {
		for (TrpMoment m : this.instance.getMoments()) {
			m.removeAsObserver(); 
		}
	}

	public int getCost() throws Exception {
		return this.instance.getCost();
	}
	
	public String toString() {
		return this.instance.toString(); 
	}
	
	public String toStringExtra() {
		return this.instance.toStringExtra(); 
	}

	public void setRouteCost(RouteCost rc) {
		this.routeCost = rc; 
	}

}
