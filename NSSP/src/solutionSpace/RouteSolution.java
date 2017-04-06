package solutionSpace;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import shuntingYard.Route;
import shuntingYard.Side;
import trainLocationProblem.TrackConsistentMaker;
import trainRoutingProblem.DijkstraGraph;
import trainRoutingProblem.RouteCost;
import trainRoutingProblem.TrpMoment;

public class RouteSolution extends Solution{

	private Route	route; 
	
	public RouteSolution (TrpMoment m, DijkstraGraph firstRoute, Side option) throws Exception {
		super(m.getBaseMoment());
		Route r = null;
		r = firstRoute.traverseRoute(option);
		if (null == r) {
			throw new Exception("The route solution is set to null"); 
		}
		this.route 	= r;
		
		int cost = this.calcRouteCost(m, r); 
		cost 		-= m.getBaseMoment().getRouteCost(); 
		super.setCostTrp(cost);
	}
	
	private int calcRouteCost(TrpMoment m, Route r) throws Exception {
		RouteCost rc = TrpMoment.getRouteCostObject(); 
		int cost = rc.totalCost(r.getTracks(), m.getBlocked(), m.getPrevious().getCoordinate()); 
		return cost;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("Route m" + super.getMoment().getTimePoint() + ": "); 
		try {
			sb.append(super.getMoment().getLocation().toString());
		} catch (Exception e) {
			if (e.getMessage().equals("No location assigned")) {
				sb.append("noLocation"); 
			}
			else {
				e.printStackTrace();
			}
		} 
		sb.append("-->"); 
		sb.append(this.route.toString()); 
		try {
			sb.append(" cost=" + this.getCost());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}

	@Override
	public void execute(Search search) throws Exception {
		super.getMoment().setRoute(this.route);
		super.getMoment().makeConsistent();
		
		TrackConsistentMaker.addTrack(this.route.getDestination().getTrack());
	}

	/**
	 * If the current moment does not yet has a route assigned, this solution
	 * may always be executed. Otherwise, this solution may only be executed if
	 * the total cost is negative.
	 * @throws Exception 
	 */
	@Override
	public boolean isFeasible() throws Exception {
		if (!super.getMoment().hasRoute()) {
			return true;
		}
		return (this.getCostTrp() < 0);
	}

}