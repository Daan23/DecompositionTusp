package solutionSpace;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.ObservedMoment;

public class SpaceSolution extends Solution {
	
	int coordinate; 

	public SpaceSolution(ObservedMoment m, int coordinate, int cost) throws Exception {
		super(m);
		this.coordinate = coordinate; 
		super.setCostTlp(cost);
	}

	@Override
	public void execute(Search search) throws Exception {
		this.executeDoNotMakeConsistent();
		this.makeConsistent();
	}
	
	protected void executeDoNotMakeConsistent() throws Exception {
		super.getMoment().setTrackCoordinate(this.coordinate); 
	}

	@Override
	public boolean isFeasible() {
		return true;
	}

	public int getFirstCoordinate() {
		return this.coordinate;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("SpaceSol " + super.toString()); 
		sb.append("->" + this.coordinate); 
		return sb.toString(); 
	}

	public String toStringSimple() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(super.toString() + "->"); 
		sb.append(this.coordinate); 
		return sb.toString(); 
	}

	public int getLastCoordinate() {
		return this.coordinate + this.getMoment().getTrainLength();
	}

	protected void makeConsistent() throws Exception{
		this.getMoment().makeConsistent();
	}

}
