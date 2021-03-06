package nsspAlgorithmVariations;

import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.NsspSolver;
import trainLocationProblem.LocationCost;
import trainRoutingProblem.RouteCost;

public class NsspSequentialSolver extends NsspSolver{
	
	public static enum SubProblem {TLP, TRP} 
	private SubProblem currentlyBeingSolved; 
	
	private LocationCost savedLocationCost; 
	private LocationCost ignoreLocationCost; 
	private RouteCost savedRouteCost; 
	private RouteCost ignoreRouteCost; 
	
	private int swaps; 

	public NsspSequentialSolver(NsspInstance instance, AlgSettings settings, SubProblem startWith)
			throws Exception {
		super(instance, settings);
		
		this.savedLocationCost		= settings.getLocationCost(); 
		this.savedRouteCost			= settings.getRouteCost(); 
		
		this.ignoreLocationCost		= new LocationCost(); 
		this.ignoreLocationCost.setFreeRelocationCost(0);
		this.ignoreLocationCost.setSpaceConsumedCost(0); 
		this.ignoreLocationCost.setConflictCost(0); 
		this.ignoreLocationCost.setMomentsMap(super.tlp.getInstance());
		
		this.ignoreRouteCost		= new RouteCost(); 
		this.ignoreRouteCost.setObstructionCost(0);
		this.ignoreRouteCost.setStraightCost(0);
		this.ignoreRouteCost.setTurnCost(0);
		
		this.setSolver(startWith);
		
		if (super.search.equals(Search.tabu)) {
			throw new Exception("Cannot use tabu yet, due to deadlock"); 
		}
	}
	
	@Override
	public boolean solve() throws Exception {
		super.iterations	= 0; 
		this.swaps			= 0; 
		this.setSolver(SubProblem.TRP); 
		while (super.conflictsExist()) {
			if (this.currentProblemIsSolved()) {
				this.swapSolvers(); 
				this.swaps++; 
			}
			AlgIteration it = super.algIteration(); 
			super.iterations++; 
			if (!it.succes) {
				// If conflict could not be solved, instance is unsolvable from current state. 
				return false; 
			}
		}
		return true;
	}

	private void swapSolvers() throws Exception {
		switch (this.currentlyBeingSolved) {
		case TRP : 
			this.setSolver(SubProblem.TLP);
			break; 
		case TLP :
			this.setSolver(SubProblem.TRP);
			break; 
		default :
			throw new Exception("Unknown type"); 
		}
	}

	private boolean currentProblemIsSolved() throws Exception {
		switch (this.currentlyBeingSolved) {
		case TRP : 
			return (0 == super.trp.getProblemSeverity()); 
		case TLP :
			return (0 == super.tlp.getProblemSeverity()); 
		default :
			throw new Exception("Unknown type"); 
		}
	}

	private void setSolver(SubProblem p) throws Exception {
		this.currentlyBeingSolved = p; 
		switch (p) {
		case TRP : 
			super.setLocationCost(this.ignoreLocationCost);
			super.setRouteCost(this.savedRouteCost);
			super.subProblemPriority	= Prio.trpBeforeTlp;
			super.subProblemInit 		= Init.trpProposes; 
			break; 
		case TLP : 
			super.setLocationCost(this.savedLocationCost);
			super.setRouteCost(this.ignoreRouteCost);
			super.subProblemPriority	= Prio.tlpBeforeTrp;
			super.subProblemInit 		= Init.tlpProposes; 
			break; 
		default :
			throw new Exception("Unknown type"); 
		}
	}

	public int getSwaps() {
		return swaps;
	}

}
