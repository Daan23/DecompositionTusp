package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;

import shuntingYard.Side;
import solutionSpace.Solution;
import solutionSpace.SolutionSet;
import solutionSpace.SpaceTrackSolution;
import trainLocationProblem.LocationCost;
import trainLocationProblem.TlpSolver;
import trainLocationProblem.TrackConsistentMaker;
import trainRoutingProblem.RouteCost;
import trainRoutingProblem.TrpSolver;

public class NsspSolver extends AlgSettings{
	
	protected NsspInstance 			instance; 
	
	/**
	 * The initial nssp instance is a copy of the initial instance, used as
	 * input for the solve method. The NsspSolver class does not use this copy
	 * of the initial instance. It is stored for testing purposes to check the
	 * influence of an initial schedule for the final results. The instance
	 * Executer class is the class which requires the initial instance.
	 */
	protected NsspInstance 			initialInstance; 
	
	protected TrpSolver 	trp; 			// Train Routing Problem, assigns routes and new locations to moments so that route conflicts are resolved. 
	protected TlpSolver 	tlp; 			// Train Location Problem, assigns locations and coordinates to moments so that location conflicts are resolved. 

	private ArrayList<Subproblem> 	subproblems; 	// A list of all Solvers defined above. 
	
	protected boolean 				DEBUG;
	private boolean 				CHECKCOST = false; 

	private AlgIteration it;
	
	protected int iterations; 

	/**
	 * This method may not be used anymore! The method still exist because we
	 * don't want to re-write the test already used.
	 * 
	 * @param instance
	 * @throws Exception
	 */
	public NsspSolver (NsspInstance instance) throws Exception {
		super(); 
		this.iterations		= 0; 
		this.instance 		= instance; 
		DEBUG				= this.instance.debugOutput(); 
		this.instance.setDebugging(false);
		
		trp 				= new TrpSolver(instance, super.getRouteCost()); 
		tlp 				= new TlpSolver(instance, super.getLocationCost()); 
		
		this.initialInstance = null; 
		
		this.subproblems 	= new ArrayList<>(); 
		this.subproblems.add(trp);
		this.subproblems.add(tlp);
	}
	
	public NsspSolver(NsspInstance instance, AlgSettings settings) throws Exception {
		super(settings); 
		this.iterations		= 0; 
		this.instance 		= instance; 
		DEBUG				= this.instance.debugOutput(); 
		this.instance.setDebugging(false);
		
		trp 				= new TrpSolver(instance, super.getRouteCost()); 
		tlp 				= new TlpSolver(instance, super.getLocationCost()); 
		
		this.subproblems 	= new ArrayList<>(); 
		this.subproblems.add(trp);
		this.subproblems.add(tlp);
	}

	public void createInitialSchedule() throws Exception {
		
		Subproblem sp = null;
		
		switch (super.subProblemInit) {
		case trpInit : 		trp.createInitialSchedule(); 
							tlp.createInitialSchedule();
							break; 
		case tlpInit : 		tlp.createInitialSchedule();
 							trp.createInitialSchedule(); 
							break; 
		case trpProposes : 	sp = this.trp; 
							break; 
		case tlpProposes : 	sp = this.tlp; 
							break; 
		default : 			sp = this.trp; 
							break; 
		}
		
		if (null != sp) {
			for (Moment m : this.instance.getMoments()) {
				SolutionSet ss = sp.schedule(m); 
				this.assignCost(sp, ss);
				Solution s = ss.getBestSolution(); 
				if (s instanceof SpaceTrackSolution) {
					SpaceTrackSolution t = (SpaceTrackSolution) s; 
					t.executeFirst(super.search); 
				} else {
					ss.executeBest(super.search); 
				}
			}
		}
		this.tlp.createFreeRelocations();
		this.debugPrintSchedule();
	}

	public boolean solve() throws Exception {
		iterations = 0; 
		while (this.conflictsExist()) {
			AlgIteration it = this.algIteration(); 
			iterations++; 
			if (!it.succes) {
				// If conflict could not be solved, instance is unsolvable from current state. 
				return false; 
			}
		}
		return true; 
	}
	
	public AlgIteration algIteration() throws Exception {
		this.it = new AlgIteration(); 
		it.selectedProblem 		= this.selectSubProblem(); 
		it.conflicts			= this.subProblemToString(it.selectedProblem);
		it.solutions			= it.selectedProblem.genSolutions();  

		if (DEBUG) System.out.println(it.conflicts);
		
		// Vote about options
		this.assignCost(it.selectedProblem, it.solutions);
		//if (DEBUG) System.out.println(it.solutions.toString());
		
		if (it.solutions.isEmpty()) {
			// No solutions exist for at least one conflict within p. 
			it.succes = false; 
			return it; 
		}
		
		it.selectedSolution	= it.solutions.getBestSolution(); 
		if (DEBUG) System.out.println(it.selectedSolution.toString());
		
		int trpCost 		= this.trp.getCost(); 
		int tlpCost 		= this.tlp.getCost(); 
		int previousTotal	= trpCost + tlpCost; 
		trpCost		+= it.selectedSolution.getCostTrp();
		tlpCost		+= it.selectedSolution.getCostTlp(); 
		
		Side oldSide = it.selectedSolution.getMoment().getArrivingSide(); 
		
		it.succes = it.solutions.executeBest(super.search); 
		
		if (TrackConsistentMaker.isNotConsistent()) {
			TrackConsistentMaker.makeConsistent(this);
			
			int currentTotal = this.trp.getCost() + this.tlp.getCost(); 
			if (previousTotal <= currentTotal && super.search.equals(Search.tabu)) {
				if (it.selectedSolution instanceof SpaceTrackSolution) {
					SpaceTrackSolution sts = (SpaceTrackSolution) it.selectedSolution; 
					sts.setCostTlp(1);
					sts.setCostTrp(1);
					try {
						sts.getTrackSolution().removeOption(oldSide);
					} catch (Exception e) {
						if (e.getMessage().startsWith("the option was not given in the options of")) {
							// do nothing
						} else {
							throw e; 
						}
					}
				} 
			}
			this.debugPrintSchedule();
		} else {
			this.debugPrintSchedule();
			if (this.tlp.getCost() != tlpCost 
					&& this.tlp.getCost() != tlpCost + super.getLocationCost().getFreeRelocationCost()
					// if the option is not removed because of the tabu search, the costs may be 2000 less
					&& this.tlp.getCost() != tlpCost + (2* super.getLocationCost().getFreeRelocationCost())) {
				if (this.CHECKCOST) {
					throw new Exception("Expected tlp cost is " + tlpCost + " but it was " + this.tlp.getCost()); 
				}
			}
			int cost = this.trp.getCost(); 
			if (cost != trpCost 
					&& cost != trpCost - this.trp.getRouteCostObject().getTurnCost()
					&& cost != Integer.MAX_VALUE
					&& trpCost - it.selectedSolution.getCostTrp() != Integer.MAX_VALUE) { 
				// The - turnCost is for allowing solutions with a turn on the end to remove the turn before execution. 
				if (this.CHECKCOST) {
					throw new Exception("Expected trp cost is " + trpCost + " but it was " + cost); 
				}
			}
		}
		return it;
	}

	public class AlgIteration {
		public Subproblem 		selectedProblem; 
		public String 			conflicts;
		public SolutionSet		solutions; 
		public Solution		selectedSolution; 
		public boolean 		succes; 
		
		public AlgIteration() {
			this.succes = true; 
		}

		public boolean succes() {
			return this.succes;
		}
	}
	
	private String subProblemToString(Subproblem p) throws Exception {
		if (p instanceof TlpSolver) {
			return "TLP has conflicts" + 
					this.tlp.findConflicts().toString(); 
		}
		if (p instanceof TrpSolver) {
			return "TRP has conflicts: " + 
					this.trp.findConflicts().toString(); 
		}
		return "cannot determine the problem";
	}

	/**
	 * This method calculates the costs for given solutions within other
	 * subProblems. The ordering in which these costs are assigned is important.
	 * Solutions may exists without a route, this would then be assigned by TRP.
	 * For solutions with a new route, the TLP calculates the impact of those
	 * route within the TLP. Solutions may also not contain coordinate where to
	 * store the moment on the track, these coordinates are then calculated
	 * within TLP but these do not affect the TRP. 
	 * @param p 
	 * 
	 * @param ss
	 * @throws Exception 
	 */
	public void assignCost(Subproblem proposedBy, SolutionSet ss) throws Exception {
		if (proposedBy instanceof TlpSolver) {
			trp.assignCosts(ss);	
			tlp.assignCosts(ss);
		} 
		else if (proposedBy instanceof TrpSolver) {
			tlp.assignCosts(ss);
			trp.assignCosts(ss);	
		}
		else {
			throw new Exception("The Subproblem is not known"); 
		}
	}

	private Subproblem selectSubProblem() throws Exception {
		int i = 0; 
		Subproblem mostImportant = null; 
		
		
		switch (super.subProblemPriority) {
			case trpBeforeTlp : 
				mostImportant = this.trp; 
				if (mostImportant.getProblemSeverity() > 0) {
					return mostImportant; 
				}
				break; 
			case tlpBeforeTrp :
				mostImportant = this.tlp; 
				if (mostImportant.getProblemSeverity() > 0) {
					return mostImportant; 
				}
				break; 
			case highestCost :
			default : 
				mostImportant = this.subproblems.get(i); 
		}
		
		for (Subproblem sp : this.subproblems) {
			if (mostImportant.getProblemSeverity() < sp.getProblemSeverity()) {
				mostImportant = sp; 
			}
		}
		if (mostImportant.getProblemSeverity() > 0) {
			return mostImportant;
		}
		throw new Exception("No conflictSpace with conflicts in it exists"); 
	}

	public NsspInstance getInstance() {
		return this.instance; 
	}

	public boolean conflictsExist() throws Exception {
		boolean b = false; 
		for(Subproblem p : this.subproblems) {
			if (p.getProblemSeverity() >0) {
				b = true; 
			}
		}
		return b;
	}
	
	public int getTrpScheduleCost() throws Exception {
		return this.trp.getCost();
	}

	public TlpSolver getTlp() {
		return this.tlp;
	}

	public int getTlpScheduleCost() throws Exception {
		return this.tlp.getCost();
	}

	public void setDebug(boolean b) {
		this.DEBUG = b; 
	}

	public int getNofIterations(){
		return this.iterations; 
	}

	public void setSearch(Search search) {
		this.search = search; 
	}
	
	public void debugPrintSchedule(){
		if (DEBUG) {
			StringBuilder sb = new StringBuilder(); 
			sb.append("NsspInstance: \n\n"); 
			sb.append(this.trp.toStringExtra()); 
			sb.append("\n"); 
			sb.append(this.tlp.toString()); 
			System.out.println(sb.toString());
		}
	}
	
	@Override
	public void setLocationCost(LocationCost locationCost) {
		super.setLocationCost(locationCost);
		this.tlp.setLocationCost(locationCost); 
	}

	@Override
	public void setRouteCost(RouteCost rc) {
		super.setRouteCost(rc);
		this.trp.setRouteCost(rc); 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("NsspInstance: \n\n"); 
		sb.append(this.trp.toString()); 
		sb.append("\n"); 
		//sb.append(this.tlp.toString()); 
		return sb.toString();
	}

	public Subproblem getTrp() {
		return this.trp;
	}

	public NsspInstance getInitialInstance() {
		return this.initialInstance;
	}

}
