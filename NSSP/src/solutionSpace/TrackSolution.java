package solutionSpace;

import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.Side;
import shuntingYard.Track;
import trainLocationProblem.TrackConsistentMaker;
import trainRoutingProblem.DijkstraGraph;
import trainRoutingProblem.DijkstraNode;
import trainRoutingProblem.DijkstraOutput;

public class TrackSolution extends Solution{
	
	private Side 			option; 
	private DijkstraGraph 	firstRoute; 
	
	private DijkstraGraph 	lastRoute; 		// This may be null, if the next Moment is not given or the next Route should not yet be determined. 
	private ObservedMoment	lastMoment; 	// The moment to which the lastRoute should be assigned. 
	
	/**
	 * The cheapest side to leave the track to get to the next destination. 
	 */
	private Side leaveOption; 
	
	/**
	 * Indicates if the costs of otherBlockedRoutes is already taken into account. 
	 */
	private boolean 		hasOtherBlockedRoutesAssigned;
	
	public TrackSolution(
			ObservedMoment firstM, 
			ObservedMoment lastM, 
			Side option, 
			DijkstraGraph firstRoutes, 
			DijkstraGraph lastRoutes) throws Exception {
		super(firstM); 
		this.option				= option; 
		this.firstRoute			= firstRoutes; 
		this.lastRoute			= lastRoutes; 
		this.lastMoment			= lastM; 
		this.leaveOption		= null; 
		
		this.hasOtherBlockedRoutesAssigned 	= false; 
		
		if (!((lastM == null && lastRoutes == null) || (lastM != null && lastRoutes != null))) {
			throw new Exception("lastM may not be null if lastRoutes is not null");
		}
		int cost = this.calcCost(); 
		super.setCostTrp(cost);
	}	

	/**
	 * This constructor set's the graph to the next location to null. This
	 * constructor should only be used if no next moment exists, or the next
	 * route should not yet be calculated.
	 * 
	 * @param m
	 * @param option
	 * @param firstRoutes
	 * @param extraCost
	 * @param routeCost
	 * @throws Exception 
	 */
	public TrackSolution(ObservedMoment m, 
			Side option, 
			DijkstraGraph firstRoutes, 
			int trpCost) throws Exception {
		this(m, null, option, firstRoutes, null); 
	}
	/**
	 * This constructor only determines the track option for that track. If no
	 * DijkstraGraphs containing the route to this option are added later, only
	 * the location of the observed moment will be assigned.
	 * 
	 * @param m
	 * @param option
	 * @throws Exception 
	 */
	public TrackSolution(ObservedMoment m, 
			Side option) throws Exception {
		this(m,option, null, 0); 
	}
	
	public TrackSolution (DijkstraOutput out, Side option) throws Exception {
		super(out.firstM); 
		
		this.option				= option; 
		this.firstRoute			= out.firstRoutes; 
		this.lastRoute			= out.lastRoutes; 
		this.lastMoment			= out.lastM; 

		int cost = this.calcCost(); 
		super.setCostTrp(cost);
	}
	
	public void setOtherRouteCost(int cost) throws Exception{
		if (this.hasOtherBlockedRoutesAssigned) throw new Exception("the otherBlockedRouteCost are already set of " + this.toString()); 
		this.hasOtherBlockedRoutesAssigned = true; 
		int newCost = super.getCostTrp() + cost; 
		super.setCostTrp(newCost);
	}
	
	public boolean hasOtherRouteCostAssigned() {
		return this.hasOtherBlockedRoutesAssigned; 
	}
	
	
	private int calcCost() throws Exception {
		int cost = 0; 
		if (super.getMoment().hasRoute()) {
			cost -= super.getMoment().getRouteCost(); 
		}
		// We see the side where the moment arrives as the option to take since
		// this will influence the problem to put moment at specific coordinates
		// of a track. We therefore don't pick the minimum of the two sides. 
		if (this.hasRouteAssigned()) {
			cost += this.firstRoute.get(option).getCost();
		}
		return cost;
	}

	void assignFirstRoute() throws Exception {
		if (!this.hasOtherBlockedRoutesAssigned) 
			throw new Exception("The other blocked routes are not yet assigned to " + this.getMoment().toString());
		if (null != this.lastRoute && null == this.leaveOption) 
			throw new Exception("The lastRoute is calculated, but the costs are not yet assigned to the totalCost for " + this.toString()); 
		
		if (null != this.firstRoute) {
			Route first = this.firstRoute.traverseRoute(this.option);
			if(null != first) {
				super.getMoment().setRoute(first);
			}
		} 
		else {
			Route r = new Route(Location.dummy(), new Location(this.option)); 
			this.getMoment().setRoute(r);
		}
	}
	
	private void assignSecondRoute() throws Exception {
		
		if(null != this.lastRoute) {
			
			if (null == this.leaveOption) throw new Exception("The option on which side we need to leave has to be calculated before executing " + this.toString()); 
		
			Route last = null;
			last = this.lastRoute.traverseRouteInverse(this.leaveOption);
			
			if (!this.lastMoment.getArrivingSide().equals(last.getDestination().getArrivingSide())) {
				// If the moment arrives at the other side, that track has to be made consistent as well. 
				TrackConsistentMaker.addTrack(this.lastMoment.getTrack()); 
			}
			last.getDestination().setCoordinate(this.lastMoment.getLocation().getCoordinate());
			if(null != last) {
				this.lastMoment.setRoute(last);
			}
		}
	}

	public void removeOption(Search search) throws Exception {
		if (search.equals(Search.normal)) {
			if (super.getMoment().hasLocation()) {
				this.removeOption(super.getMoment().getArrivingSide()); 
			}
		} 
		else if (search.equals(Search.tabu)) {
			if (this.getCost() >=0 && super.getMoment().hasLocation()) {
				this.removeOption(super.getMoment().getArrivingSide()); 
			}
		} else {
			throw new Exception("Unknown search type: " + search.toString()); 
		}
	}
	
	public void removeOption(Side opt) throws Exception {
		super.getMoment().removeOption(opt);
		if (AlgSettings.removeBothSidesOfOption) {
			if (super.getMoment().getOptions().contains(opt.getOtherSide())) {
				super.getMoment().removeOption(opt.getOtherSide());
			}
		}
	}

	@Override
	public void execute(Search search) throws Exception {
		this.executeDoNotMakeConsistent(search);
		this.makeConsistent();
	}
	
	protected void executeDoNotMakeConsistent(Search search) throws Exception {
		if (!this.hasOtherBlockedRoutesAssigned) throw new Exception("The other route cost are not yet set of " + this.toString()); 
		this.removeOption(search); 
		this.assignFirstRoute();
		this.assignSecondRoute(); 
	}

	public Side getOption() {
		return this.option;
	}
	
	public boolean hasLastRoute() {
		return (null != this.lastRoute);
	}

	public int getLastRouteCost() throws Exception {
		int a = this.lastRoute.get(this.getOption()).getCost(); 
		int b = this.lastRoute.get(this.getOption().getOtherSide()).getCost(); 
		return (a<b) ? a : b;
	}

	/**
	 * A track solution is always allowed to be executed. 
	 * @throws Exception 
	 */
	@Override
	public boolean isFeasible() throws Exception { 
		if(!super.getMoment().hasLocation()) {
			return true; 
		}
		return !this.option.equals(super.getMoment().getArrivingSide());
	}
	
	public void executeFirst(Search search) throws Exception {
		this.removeOption(search);
		this.assignFirstRoute();
		this.makeConsistent();
	}

	public boolean hasRouteAssigned() {
		return (null != this.firstRoute);
	}	
	
	protected void makeConsistent() throws Exception {
		this.getMoment().makeConsistent();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("TrackSol m" + super.getMoment().getTimePoint() + ": "); 
		try {
			sb.append(super.getMoment().getLocation().toString());
		} catch (Exception e) {
			if (e.getMessage().startsWith("No location assigned")) {
				sb.append("noLocation"); 
			}
			else {
				e.printStackTrace();
			}
		} 
		sb.append("-->"); 
		sb.append(this.option.toString()); 
		try {
			sb.append(" cost=" + this.getCostTrp() + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}

	public void ignoreOtherRouteCost() {
		this.hasOtherBlockedRoutesAssigned = true; 
	}

	public int getCostLeaveSideA() throws Exception {
		if (null == this.lastRoute) throw new Exception ("Last route was not calculated of " + this.toString()); 
		Side optionA = (this.option.isSideA()) ? this.option : this.option.getOtherSide(); 
		return this.lastRoute.get(optionA).getCost();
	}

	public int getCostLeaveSideB() throws Exception {
		if (null == this.lastRoute) throw new Exception ("Last route was not calculated of " + this.toString()); 
		Side optionB = (this.option.isSideB()) ? this.option : this.option.getOtherSide(); 
		return this.lastRoute.get(optionB).getCost();
	}

	public void setLeaveFromSideA() throws Exception {
		this.leaveOption = (this.option.isSideA()) ? this.option : this.option.getOtherSide(); 
	}

	public void setLeaveFromSideB() throws Exception {
		this.leaveOption = (this.option.isSideB()) ? this.option : this.option.getOtherSide();
	}

	public boolean hasTurnAtEnd() throws Exception {
		if (null == this.firstRoute) throw new Exception("The routes are not yet initialized"); 
		DijkstraNode d 	= this.firstRoute.get(this.option); 
		Track t1 		= d.getTrack(); 
		if (null == d.getOrigin()) {
			return false; 
		}
		Track t2		= d.getOrigin().getTrack();
		return t1.equals(t2);
	}

}
