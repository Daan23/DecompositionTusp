package nsspAlgorithmVariations;

import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.AlgSettings.Init;
import nedtrainSchedulingShuntingProblem.AlgSettings.Prio;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.NsspSolver;
import nsspAlgorithmVariations.InductiveSearch.Order;
import nsspAlgorithmVariations.NsspSequentialSolver.SubProblem;
import trainLocationProblem.LocationCost;
import trainRoutingProblem.RouteCost;

public class SolverFactory {
	
	AlgSettings settings; 
	
	public static enum AlgType {voting, sequentialTrpFirst, sequentialTlpFirst, inductiveSearchTrainArrival, inductiveSearchOccurance} 
	private AlgType alg; 
	
	public SolverFactory(AlgSettings settings, AlgType type) {
		this.settings = settings; 
		this.alg = type; 
	}
	
	public NsspSolver genSolver(NsspInstance in) throws Exception {
		switch (this.alg) {
		case voting : 
			return new NsspSolver(in, this.settings); 
		case sequentialTlpFirst : 
			return new NsspSequentialSolver(in, this.settings, SubProblem.TLP); 
		case sequentialTrpFirst : 
			return new NsspSequentialSolver(in, this.settings, SubProblem.TRP);
		case inductiveSearchOccurance : 
			return new InductiveSearch(in, this.settings, Order.OCCURANCE); 
		case inductiveSearchTrainArrival:
			return new InductiveSearch(in, settings, Order.TRAINARRIVAL); 
		default:
			break; 
	}
		throw new Exception("Unknown algorithm type");
	}

	public boolean isSetSequential() {
		return this.alg.equals(AlgType.sequentialTlpFirst) || this.alg.equals(AlgType.sequentialTrpFirst);
	}

	
	
	
	
	
	

	
	public static AlgSettings algSettings() {
		AlgSettings settings	= new AlgSettings(); 
		
		RouteCost routeCost		= new RouteCost(); 
		routeCost.setStraightCost(			1);
		routeCost.setTurnCost(				8);
		routeCost.setObstructionCost(		10000);
		settings.setRouteCost(routeCost);
		
		LocationCost locationCost = new LocationCost(); 
		locationCost.setSpaceConsumedCost(	-1); 
		locationCost.setFreeRelocationCost(	-1); 
		locationCost.setConflictCost(		10000);
		settings.setLocationCost(locationCost); 
		
		settings.setSubProblemInit(Init.trpProposes);
		settings.setSubProblemPrio(Prio.highestCost);
		
		return settings; 
	}
	
	public static SolverFactory votingTabu() throws Exception {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.tabu); 
		SolverFactory sf = new SolverFactory(settings, AlgType.voting);
		return sf; 
	}
	
	public static SolverFactory votingNormal() throws Exception {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.normal); 
		SolverFactory sf = new SolverFactory(settings, AlgType.voting);
		return sf; 
	}
	
	public static SolverFactory sequentialTrpFirst() throws Exception {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.normal); 
		settings.setSubProblemInit(Init.trpInit);
		SolverFactory sf = new SolverFactory(settings, AlgType.sequentialTrpFirst);
		return sf; 
	}

	public static SolverFactory sequentialTlpFirst() {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.normal); 
		settings.setSubProblemInit(Init.tlpInit);
		SolverFactory sf = new SolverFactory(settings, AlgType.sequentialTlpFirst);
		return sf; 
	}

	public static SolverFactory inductiveSearchOccurance() {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.tabu); 
		SolverFactory sf = new SolverFactory(settings, AlgType.inductiveSearchOccurance);
		return sf;
	}

	public static SolverFactory inductiveSearchTrainArrival() {
		AlgSettings settings	= algSettings(); 
		settings.setSearch(Search.tabu); 
		SolverFactory sf = new SolverFactory(settings, AlgType.inductiveSearchTrainArrival);
		return sf;
	}

}
