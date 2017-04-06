package nedtrainSchedulingShuntingProblem;

import trainLocationProblem.LocationCost;
import trainRoutingProblem.RouteCost;

public class AlgSettings {

	/**
	 * If a new location is determined for a given moment, we have two options:
	 * 1. 	We can only remove the side of the track where it arrives, then in a
	 * 		later iteration we can still relocate this same moment to the other side
	 * 		of the track. 
	 * 2. 	We can remove both sides of the track as option such
	 * 		that the moment will never be relocated to that track from any side.
	 */
	public static boolean removeBothSidesOfOption = true;
	

	/**
	 * This determines how the initial schedule will be created. 
	 */
	protected Init subProblemInit = Init.trpInit; 
	public static enum Init {trpInit, tlpInit, trpProposes, tlpProposes}
	
	public void setSubProblemInit(Init set) {
		this.subProblemInit = set; 
	}
	
	/**
	 * This determines which subProblem may suggest solutions if both problems
	 * have a conflict.
	 */
	protected Prio subProblemPriority = Prio.trpBeforeTlp;
	public static enum Prio {trpBeforeTlp, tlpBeforeTrp, highestCost} 
	
	/**
	 * The costs used for turns, obstructions and going straight. 
	 */
	private RouteCost routeCost; 
	
	/**
	 * The costs for locating moments on specific tracks. 
	 */
	private LocationCost locationCost; 
	
	/**
	 * Determine how the solution space will be consumed; 
	 * 1. 	Remove an option if we tried that option once. 
	 * 2. 	Remove an option only of the total cost increase (Tabu-search)
	 */
	protected Search search = Search.normal;

	public static enum Search {normal, tabu}
	
	public AlgSettings() {
		this.init();
	}
	
	public AlgSettings(AlgSettings settings) {
		this.copySettings(settings);
	}

	public void init() {
		removeBothSidesOfOption = true;
		this.subProblemInit 			= Init.trpInit; 
		this.subProblemPriority 		= Prio.trpBeforeTlp; 
		this.search 					= Search.tabu; 
		
		this.locationCost		= new LocationCost(); 
		this.locationCost.setSpaceConsumedCost(		-1		);
		this.locationCost.setFreeRelocationCost(	-1000	);
		this.locationCost.setConflictCost(			1000000	);

		this.routeCost = new RouteCost(); 
		this.routeCost.setStraightCost(				1		);
		this.routeCost.setTurnCost(					1000	);
		this.routeCost.setObstructionCost(			1000000	);
	}

	private void copySettings(AlgSettings settings) {
		this.search				= settings.search; 
		this.subProblemInit		= settings.subProblemInit;
		this.subProblemPriority	= settings.subProblemPriority; 
		this.locationCost		= settings.locationCost; 
		this.routeCost			= settings.routeCost; 
	}

	public void setRouteCost(RouteCost rc) {
		this.routeCost = rc;
	}
	
	public void setTurnsCost(int i){ 
		this.routeCost.setTurnCost(i);
	}
	
	public void setSubProblemPrio(Prio set) {
		this.subProblemPriority = set; 
	}

	public void setLocationCost(LocationCost locationCost) {
		this.locationCost	= locationCost; 
	}

	public RouteCost getRouteCost() {
		return this.routeCost;
	}

	public LocationCost getLocationCost() {
		return this.locationCost;
	}

	public void setSearch(Search search) {
		this.search = search; 
	}

	public Search getSearch() {
		return this.search; 
	}

}
