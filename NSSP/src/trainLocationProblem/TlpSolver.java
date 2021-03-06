package trainLocationProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.Subproblem;
import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import shuntingYard.Side;
import shuntingYard.Track;
import solutionSpace.Solution;
import solutionSpace.SolutionSet;
import solutionSpace.SpaceSolution;
import solutionSpace.SpaceSolutionAssembly;
import solutionSpace.SpaceTrackSolution;
import solutionSpace.TrackSolution;

public class TlpSolver implements Subproblem {
	
	/**
	 * The instance containing all moments within the TLP, with needed TLP data.
	 */
	private TlpInstance instance; 
	
	/**
	 * Conflicts within the TLP instance. 
	 */
	private ArrayList<LocationConflict> conflicts; 
	
	/**
	 * The class used to (re)locate moemnts within the instance. 
	 */
	private MomentLocator locator; 
	
	/**
	 * The momentLocator uses a locationCost class which determines the cost of
	 * given locations for given moments.
	 */
	private LocationCost locationCost; 
	
	private static final Search search = Search.normal; 
	
	private boolean DEBUG; 

	public TlpSolver(NsspInstance instance, LocationCost locationCost) throws Exception {
		this.locationCost	= locationCost; 
		this.instance		= new TlpInstance(instance, this.locationCost); 
		this.locationCost.setMomentsMap(this.instance);
		this.conflicts 		= new ArrayList<>(); 
		this.locator		= new MomentLocator(); 
		this.DEBUG 			= instance.debugOutput(); 
		this.removeUnrealisticLocationOptions();
	}
	
	public TlpSolver(NsspInstance instance) throws Exception {
		this(instance, new LocationCost()); 
	}

	/**
	 * Removes all location options for which it holds that the train is longer
	 * than the track length of the locaiotn option.
	 */
	private void removeUnrealisticLocationOptions() {
		for (TlpMoment m : this.instance.getMoments()) {
			TrackSolutionSpace optionList = m.getSideOptions();
			for (int i = 0; i<optionList.size(); ) {
				Side s = optionList.get(i); 
				if (m.getTrainLength() > s.getTrack().getLength()) {
					optionList.removeFromInitialOptions(i); 
				} else {
					i++; 
				}
			}
		}
	}

	@Override
	public SolutionSet genSolutions() throws Exception {
		findConflicts(); 
		LocationConflict c	= this.selectConflict(); 
		SolutionSet ss 		= solve(c); 
		return ss;
	}
	
	public SolutionSet solve(LocationConflict c) throws Exception {
		SolutionSet ss 		= new SolutionSet();
		for (TlpMoment m : c.getContributingMoments()) {
			for (Side option : m.getSideOptions()) {
				// We currently cannot create solutions such that the moment is
				// relocated to the same track from the other Side.
				if (!m.getTrack().equals(option.getTrack())) {
					SpaceSolutionAssembly s1 = locator.relocateMomentAt(m, option); 
					TrackSolution s2 		= new TrackSolution(m.getObservedMoment(), option); 
					SpaceTrackSolution s	= new SpaceTrackSolution(s1, s2); 
					ss.add(s);
				}
			}
		}
		return ss; 
	}

	/**
	 * Get the first, and smallest conflict (pretty arbitrary, can be improved).
	 * 
	 * @return
	 */
	private LocationConflict selectConflict() {
		LocationConflict save = this.conflicts.get(0); 
		for (LocationConflict c : this.conflicts) {
			if (save.get(0).getTimePoint() > c.get(0).getTimePoint()) {
				save = c; 
			} else if (save.get(0).getTimePoint() == c.get(0).getTimePoint()) {
				if (save.getContributingMoments().size() > c.getContributingMoments().size()) {
					save = c; 
				}
			}
		}
		return save;
	}
	
	private ArrayList<TlpMoment> constructContributingMoments(TlpMoment m) throws Exception {
		ArrayList<TlpMoment> contributing = new ArrayList<>(); 
		this.constructContributingMoments(m, contributing);
		return contributing; 
	}

	private void constructContributingMoments(TlpMoment m, ArrayList<TlpMoment> contributing) throws Exception {
		contributing.add(m); 
		if (0 != m.getCoordinate()) {
			ArrayList<TlpMoment> aSide 	= m.getMomentsAtSameTimeAtSameTrack(); 
			ArrayList<TlpMoment> bSide 	= new ArrayList<>(); 
			MomentLocator.splitMoments(aSide, bSide, m.getTimePoint(), m.getArrivingSide());
			for (TlpMoment q : aSide) {
				if (q.getCoordinate() + q.getTrainLength() == m.getCoordinate()) {
					this.constructContributingMoments(q, contributing);
				}
			}
		}
	}

	public ArrayList<LocationConflict> findConflicts() throws Exception {
		if (null == this.conflicts) {
			this.conflicts = new ArrayList<>(); 
		} else {
			this.conflicts.clear();
		}
		Collection<TlpMoment> list = instance.getMoments(); 
		for (TlpMoment m : list) {
			this.addConflictIfMomentExceedsTrackLength(m); 
		}
		return this.conflicts; 
	}
	
	private void addConflictIfMomentExceedsTrackLength(TlpMoment m) throws Exception {
		if (m.exceedsTrackLength()) {
			ArrayList<TlpMoment> conflictingMoments = this.constructContributingMoments(m); 
			LocationConflict c = new LocationConflict(conflictingMoments); 
			this.conflicts.add(c); 
		} 
	}

	/**
	 * This method assigns the TLP costs to a solution of type Route- or
	 * Track-Solution. If a TrackSolution has no coordinates assigned, this
	 * method also assigns a track coordinate to it.
	 * @throws Exception 
	 */
	@Override
	public void assignCosts(SolutionSet ss) throws Exception {
		for (int i=0; i<ss.getSolutions().size(); i++) {
			Solution s = ss.getSolutions().get(i); 
			if (this.freeRelocationsAreNotUpdated(s)) {
				throw new Exception("The freeRelocations of " + s.getMoment().toString() + " must be updated before calculating new solutions"); 
			} 
			if (s instanceof TrackSolution) {
				TrackSolution t 			= (TrackSolution) s; 
				
				boolean removed = this.removeSolutionIfInfeasible(ss, t, i); 
				if (removed) {
					i--; 
				} else {
					TlpMoment m 				= this.instance.getMoment(t.getMoment()); 
					SpaceSolutionAssembly ssa 	= locator.relocateMomentAt(m, t.getOption()); 
					s 							= new SpaceTrackSolution(ssa, t);
					ss.replace(i, s); 
				}				
			}
			if (s instanceof SpaceTrackSolution) {
				SpaceTrackSolution t = (SpaceTrackSolution) s; 
				boolean removed = this.removeSolutionIfInfeasible(ss, t.getTrackSolution(), i);
				if (removed) {
					i--; 
				} else {
					this.assignCosts(t);
				}
			}
		}
	}

	private boolean removeSolutionIfInfeasible(SolutionSet ss, TrackSolution t, int i) throws Exception {
		if (null == t) {
			return false; 
		}
		boolean removed = false; 
		if (this.solutionRelocatesToSameTrack(t)) {
			// We curently do not support to relocate to the same track. 
			ss.getSolutions().remove(i); 
			removed = true; 
		} else if (this.hasTurnAtEnd(t)) {
			// We do not allow a turn to be made on the node on which we arrive. 
			ss.getSolutions().remove(i); 
			removed = true; 
		}
		else if (this.locatesToSameTrackAsPredecessor(t)) {
			ss.getSolutions().remove(i); 
			removed = true; 
		} else if (this.locatesToSameTrackAsSuccessor(t)) {
			ss.getSolutions().remove(i); 
			removed = true; 
		}
		return removed;
	}

	private boolean hasTurnAtEnd(TrackSolution t) throws Exception {
		if (!t.hasRouteAssigned()) {
			return false; 
		}
		return t.hasTurnAtEnd();
	}

	private boolean locatesToSameTrackAsSuccessor(TrackSolution t) throws Exception {
		ObservedMoment current 	= t.getMoment(); 
		Moment next 			= current.getNext(); 
		boolean b; 
		if (!next.isEndMoment()) {
			if (next.hasLocation()) {
				b = next.getTrack().equals(t.getOption().getTrack()); 
			}
			else if (next.getOptions().size() == 0) {
				b = false; 
			}
			else if (next.getOptions().size() <= 2) {
				b = true; 
				for (Side s : next.getOptions()) {
					if (!t.getOption().getTrack().equals(s.getTrack())) {
						b = false; 
					}
				}
			} else {
				b = false; 
			}
		} else {
			b = false; 
		}
		return b;
	}

	private boolean locatesToSameTrackAsPredecessor(TrackSolution t) throws Exception {
		return t.getMoment().getPrevious().getTrack().equals(t.getOption().getTrack());
	}

	private boolean freeRelocationsAreNotUpdated(Solution t) {
		TlpMoment tm 	= this.instance.getMoment(t.getMoment()); 
		return tm.freeRelocationsAreNotUpdated();
	}

	/**
	 * Checks if the trackSolution proposes to locate a moment to the same
	 * track.
	 * 
	 * @param t
	 * @return true if proposed track is equal to the current track.
	 * @throws Exception 
	 */
	private boolean solutionRelocatesToSameTrack(TrackSolution t) throws Exception {
		if (!t.getMoment().hasLocation()) {
			return false; 
		}
		Track current 	= t.getMoment().getTrack(); 
		Track proposed	= t.getOption().getTrack(); 
		return proposed.equals(current);
	}

	/**
	 * Assigns the cost to the given spaceTrackSolution.
	 * 
	 * @param spaceTrack
	 * @throws Exception 
	 */
	private void assignCosts(SpaceTrackSolution spaceTrack) throws Exception {
		SpaceSolutionAssembly ss 	= spaceTrack.getSpaceSolution(); 
		TlpMoment changed			= instance.getMoment(spaceTrack.getMoment()); 
		Side newSide				= spaceTrack.getOption(); 
		Side oldSide				= changed.getArrivingSide(); 
		HashMap<TlpMoment, Integer> changedCoordinates 
									= constructChangedCoordinates(ss);
		int cost 					= this.locationCost.costSpaceSolutionAssembly(changed,	oldSide, newSide, changedCoordinates);
		spaceTrack.setCostTlp(cost);
	}

	/**
	 * Constructs a hashMap with all changed moments as keys and the coordinate
	 * to where they will be located as values.
	 * 
	 * @param ss
	 * @return
	 */
	public HashMap<TlpMoment, Integer> constructChangedCoordinates(SpaceSolutionAssembly ss) {
		HashMap<TlpMoment, Integer> changedCoordinates = new HashMap<>(); 
		TlpMoment m = this.instance.getMoment(ss.getMoment()); 
		changedCoordinates.put(m, ss.getFirstCoordinate()); 
		for (SpaceSolution s : ss.getOtherSolutions()) {
			m = this.instance.getMoment(s.getMoment()); 
			changedCoordinates.put(m, s.getFirstCoordinate()); 
		}
		return changedCoordinates;
	}

	@Override
	public int getProblemSeverity() throws Exception {
		if (this.findConflicts().size() > 0) {
			int cost = this.instance.getCost(); 
			return (cost <= 0) ? 1 : cost; 
		}
		return 0; 
	}

	/**
	 * This method assigns coordinates to every moment within the schedule. If
	 * no tracks are assigned to the moments yet, this will also assign a track
	 * to every moment.
	 * @throws Exception 
	 */
	@Override
	public void createInitialSchedule() throws Exception {
		this.debugPrintSchedule("PRE SCHEDULE"); 
		// With creating an initial schedule we do not have to take free Relocation into account. 
		this.ignoreFreeReolcationsUpdate(); 
		for (ObservedMoment q : this.instance.getBaseInstance().getMoments()) {
			TlpMoment m = this.getMoment(q); 
			SolutionSet ss = this.schedule(m.getObservedMoment()); 
			this.assignCosts(ss);
			this.ignoreOtherRouteCost(ss); 
			ss.executeBest(search); 
		}
		this.debugPrintSchedule("INITIAL SCHEDULE");
		this.createFreeRelocations(); 
	}
	
	private void ignoreFreeReolcationsUpdate() {
		for(TlpMoment m : this.instance.getMoments()) {
			m.ignoreFreeRelocationsUpdate(); 
		}
	}

	private void ignoreOtherRouteCost(SolutionSet ss) throws Exception {
		for (Solution s : ss.getSolutions()) {
			if (s instanceof SpaceTrackSolution) {
				s = ((SpaceTrackSolution) s).getTrackSolution(); 
			}
			if (s instanceof TrackSolution) {
				((TrackSolution) s).setOtherRouteCost(0); 
			}
		}
	}

	public void createFreeRelocations() throws Exception {
		for (TlpMoment m : this.instance.getMoments()) {
			m.initializeFreeRelocations(this.instance.getMomentsHashMap());
		}
	}

	private void debugPrintSchedule(String s) {
		if (DEBUG) {
			System.out.println(s);
			System.out.println(this.toString());
		}
	}

	/**
	 * Creates a set of options to locate a given moment.
	 * 
	 * @param m
	 * @return
	 * @throws Exception 
	 */
	public SolutionSet schedule(Moment q) throws Exception {
		TlpMoment m = this.instance.getMoment(q); 
		SolutionSet ss 		= new SolutionSet();
		if (m.hasTrackAssigned()) {
			SpaceSolution s = locator.locateMoment(m); 
			ss.add(s);
		}
		else {
			for (Side option : m.getSideOptions()) {
				SpaceSolutionAssembly s1 	= locator.locateMomentAt(m, option); 
				TrackSolution s2			= new TrackSolution(m.getObservedMoment(), option); 
				SpaceTrackSolution s 		= new SpaceTrackSolution(s1, s2); 
				ss.add(s);
			}
		}
		return ss;
	}
	
	private ArrayList<TlpMoment> getMomentsFromSameTrack(ArrayList<TlpMoment> list) throws Exception{
		ArrayList<TlpMoment> l = new ArrayList<>(); 
		l.addAll(this.trainsFromTrack(list.get(0).getTrack(), list)); 
		return l;
	}

	private Collection<? extends TlpMoment> trainsFromTrack(Track track, ArrayList<TlpMoment> list) throws Exception{
		ArrayList<TlpMoment> l = new ArrayList<>(); 
		for (TlpMoment m : list) {
			if (m.getTrack().equals(track)) {
				l.add(m); 
			}
		}
		return l;
	}

	public TlpMoment getMoment(ObservedMoment observedMoment) {
		return this.instance.getMoment(observedMoment);
	}

	public TlpInstance getInstance() {
		return this.instance;
	}

	public ArrayList<LocationConflict> getConflicts() {
		return this.conflicts;
	}

	public boolean solve() throws Exception {
		this.findConflicts();
		while (this.conflicts.size() != 0) {
			SolutionSet ss = this.genSolutions(); 
			this.assignCosts(ss);
			boolean b = false; 
			this.debugPrintSolution(ss);
			if (ss.getSolutions().size() > 0) {
				this.ignoreOtherRouteCost(ss);
				b = ss.executeBest(search); 
			}
			this.debugPrintSchedule("NEXT SOLVE STEP");
			if (!b) {
				return false; 
			}
			this.findConflicts(); 
		}
		return true; 
	}

	private void debugPrintSolution(SolutionSet ss) throws Exception {
		if (DEBUG) {
			if (null != ss) {
				System.out.println(ss.toString());
				if (ss.getSolutions().size() > 0) {
					System.out.println("Selected: " + ss.getBestSolution().toString());
				}
			}
		}
	}

	public int getProblemStateCost() throws Exception {
		return this.instance.getCost();
	}

	/**
	 * Method for debugging purposes. 
	 * @param i
	 * @param option
	 * @throws Exception
	 */
	public void initMomentAtLocation(int i, Side option) throws Exception {
		TlpMoment m 				= this.instance.getMoment(this.instance.getBaseInstance().getMoment(i)); 
		SpaceSolutionAssembly s1 	= null; 
		if (m.hasTrackAssigned()) {
			s1 = locator.relocateMomentAt(m, option); 
		} else {
			s1 	= locator.locateMomentAt(m, option); 
		}
		TrackSolution s2 			= new TrackSolution(m.getObservedMoment(), option); 
		SpaceTrackSolution s		= new SpaceTrackSolution(s1, s2); 
		s.ignoreOtherRouteCost(); 
		s.execute(search);
	}

	public int getCost() throws Exception {
		return this.instance.getCost();
	}

	public SolutionSet genAllSolutions() throws Exception {
		ArrayList<TlpMoment> contributing = new ArrayList<>(); 
		// Create a conflict containing all moments; 
		for (TlpMoment m : this.instance.getMoments()) {
			contributing.add(m); 
		}
		LocationConflict conflict = new LocationConflict(contributing); 
		SolutionSet ss = this.solve(conflict); 
		return ss;
	}

	public void setLocationCost(LocationCost locationCost) {
		this.locationCost = locationCost; 
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		try {
			sb.append("__________________________TLP-instance__________________________\n"); 
			sb.append("instanceStateCost = " + this.instance.getCost() + "\n");
			ArrayList<TlpMoment> list = new ArrayList<>(this.instance.getMoments()); 
			while (!list.isEmpty()) {
				ArrayList<TlpMoment> listForSingleTrack = this.getMomentsFromSameTrack(list); 
				list.removeAll(listForSingleTrack); 
				if (!listForSingleTrack.get(0).getTrack().toString().equals("->")) {
					sb.append("_____________Track: " + listForSingleTrack.get(0).getTrack().getName() + " " + listForSingleTrack.get(0).getTrack().getLength() + "_____________\n"); 
					sb.append(TlpInstancePrinter.TrackToString(listForSingleTrack)); 
					sb.append("\n"); 
				}
			}
			sb.append("\n"); 
			//sb.append(this.instance.toString()); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return sb.toString(); 
	}
	
	public static void main(String[] args) {
		System.out.println("Hi");
	}

}
