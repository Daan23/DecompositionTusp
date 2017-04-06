package solutionSpace;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;

/**
 * This is a container class to store a set of solutions. This class is used by
 * algoritmhs to produce a set of solutions, then check how the proposed
 * solutions influence the other problems. After this is done, the best solution
 * can be picked.
 * 
 * @author daan.vandenheuvel
 *
 */
public class SolutionSet {
	
	protected ArrayList<Solution> solutions; 
	
	public SolutionSet(){
		this.solutions = new ArrayList<Solution>(); 
	}
	
	public void add(Solution s) {
		this.solutions.add(s); 
	}
	
	/**
	 * The input solution set is joined with this solution set
	 * 
	 * @param s
	 */
	public void join(SolutionSet s) {
		this.solutions.addAll(s.getSolutions()); 
	}
	
	public boolean executeBest(Search search) throws Exception{
		if(1 > this.solutions.size()) {
			return false; 
		}
		Solution best = this.getBestSolution(); 
		
		if (!best.isFeasible()) {
			return false; 
		}
		
		best.execute(search);
		return true; 
	}

	public int getCost() throws Exception {
		return this.getBestSolution().getCost(); 
	}
	
	/**
	 * This method calculates the best possible solution within this soluionSet.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Solution getBestSolution() throws Exception {	
		if (this.solutions.isEmpty()) {
			return null; 
		}
		Solution best = this.solutions.get(0); 
		for(Solution s : this.solutions){
			int temp = SolutionComparator.compare(best, s);  
			if (temp > 0) {
				best = s; 
			}
		}
		return best; 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		
		sb.append("SolutionSet: {\n"); 
		
		for (Solution s : this.solutions) {
			sb.append("\t(" + s.toString() + "), \n"); 
		}
		
		sb.append("}"); 
		return sb.toString(); 
	}

	public boolean isEmpty() {
		return (0 == this.solutions.size());
	}
	
	/**
	 * Checks if a solution within this set is better than the current state of
	 * the instance. 
	 * 
	 * @return
	 * @throws Exception 
	 */
	public Solution containsBetterThanCurrent() throws Exception {
		Solution s				= this.getBestSolution();
		int costDifference 		= s.getCost();
		if (0 > costDifference) {
			return s; 
		}
		return null; 
	}

	public String bestToString() throws Exception {
		return this.getBestSolution().toString();
	}
	
	public ArrayList<Solution> getSolutions() {
		return this.solutions; 
	}

	public void replace(int i, Solution st) {
		this.solutions.remove(i); 
		this.solutions.add(i, st);; 
	}

}
