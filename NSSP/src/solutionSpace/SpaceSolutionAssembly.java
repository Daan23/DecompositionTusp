package solutionSpace;

import java.util.Collection;
import java.util.HashMap;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.ObservedMoment;

/**
 * A space Solution consists of one observed moment which is assigned a new
 * coordinate. But to prevent overlapping moments on a single track, more
 * moments might have to be relocated. The super SpaceSolution is the solution
 * for the first moment. The SpaceSolution in the list are to prevent
 * overlapping moments.
 * 
 * @author Daan
 *
 */
public class SpaceSolutionAssembly extends SpaceSolution {
	
	HashMap<ObservedMoment, SpaceSolution> otherSolutions; 

	public SpaceSolutionAssembly(ObservedMoment m, int coordinate, int cost) throws Exception {
		super(m, coordinate, cost); 
		super.setCostTlp(cost);
		this.otherSolutions = new HashMap<>(); 
	}
	
	/**
	 * Add an extra space solution to this assembly. Note that if a solution for
	 * the same observed moment is already in this assembly, that only the one
	 * with the largest coordinate is kept in this list.
	 * 
	 * @param s
	 * @throws Exception 
	 */
	public void add(SpaceSolution s) throws Exception {
		
		if (this.otherSolutions.containsKey(s.getMoment())) {
			SpaceSolution t = this.otherSolutions.get(s.getMoment()); 
			if (t.getFirstCoordinate() < s.getFirstCoordinate()) {
				int tlpCost = super.getCostTlp() + t.getCostTlp() - s.getCostTlp(); 
				super.setCostTlp(tlpCost);
				int trpCost = super.getCostTrp() + t.getCostTrp() - s.getCostTlp(); 
				super.setCostTrp(trpCost);
				this.otherSolutions.remove(s.getMoment()); 
				this.otherSolutions.put(s.getMoment(), s); 
			}
		}
		else {
			int tlpCost = super.getCostTlp() + s.getCostTlp(); 
			super.setCostTlp(tlpCost);
			int trpCost = super.getCostTrp() + s.getCostTrp(); 
			super.setCostTrp(trpCost);
			this.otherSolutions.put(s.getMoment(), s); 
		}
	}
	
	@Override
	public void execute(Search search) throws Exception {
		this.executeDoNotMakeConsistent();
		this.makeConsistent();
	}
	
	protected void makeConsistent() throws Exception {
		super.makeConsistent();
		for (SpaceSolution s : this.otherSolutions.values()) {
			s.makeConsistent(); 
		}
	}
	
	protected void executeDoNotMakeConsistent() throws Exception {
		super.executeDoNotMakeConsistent();
		for (SpaceSolution s : this.otherSolutions.values()) {
			s.executeDoNotMakeConsistent(); 
		}
	}

	@Override
	public boolean isFeasible() {
		return super.isFeasible();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(super.toString()); 
		try {
			sb.append(" cost=" + super.getCostTlp() + " ");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		sb.append("{"); 
		for (SpaceSolution s : this.otherSolutions.values()) {
			sb.append("\t"); 
			sb.append(s.toStringSimple()); 
		}
		sb.append("}"); 
		return sb.toString(); 
	}

	public int getOtherSolutionCost() throws Exception {
		int cost = 0; 
		for (ObservedMoment m : this.otherSolutions.keySet()) {
			if (m.getTrackLength() < m.getLastCoordinate()) {
				cost -= 1; 
			}
			if (m.getTrackLength() < this.otherSolutions.get(m).getFirstCoordinate()) {
				cost += 1; 
			}
		}
		return cost;
	}

	public Collection<SpaceSolution> getOtherSolutions() {
		return this.otherSolutions.values();
	}

	public HashMap<ObservedMoment, SpaceSolution> getOtherSolutionsHashMap() {
		return this.otherSolutions;
	}
	
}
