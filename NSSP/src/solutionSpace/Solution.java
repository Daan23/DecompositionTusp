package solutionSpace;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.ObservedMoment;

public abstract class Solution {
	// This class should be refactored to an interface. Multiple extentions of
	// this class do not use the variables.

	/**
	 * The costs below should always be relative to the current costs. 
	 */
	private int costTrp = 0; 
	private int costTlp = 0; 
	private ObservedMoment m; 
	
	public Solution (ObservedMoment m) {
		this.m = m; 
	}
	
	/**
	 * Execute the solution
	 */
	abstract public void execute(Search search) throws Exception;
	
	public int getCostTrp() throws Exception {
		return costTrp;
	}

	public void setCostTrp(int costTrp) throws Exception {
		this.costTrp = costTrp;
	}

	public int getCostTlp() throws Exception {
		return costTlp;
	}

	public void setCostTlp(int costTlp) throws Exception {
		this.costTlp = costTlp;
	}
	
	public int getCost() throws Exception{
		int cost =0; 
		cost += this.getCostTlp(); 
		cost += this.getCostTrp(); 
		return cost; 
	}

	public ObservedMoment getMoment() {
		return m;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		try {
			sb.append("m" + this.m.getTimePoint()); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return sb.toString(); 
	}

	/**
	 * Checks if it is allowed to execute this solution. A solution may not be
	 * allowed to be executed if for instance the cost of the solution is
	 * non-negative. This depends on the type of the solution.
	 * 
	 * @return
	 */
	public abstract boolean isFeasible() throws Exception;

	/**
	 * For some problems, after a set of solutions is executed, we need to make
	 * the data structure consistent again. This function is therefore called
	 * after all solutions are executed.
	 */
	//public abstract void makeConsistent() throws Exception; 
	
}
