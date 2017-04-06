package solutionSpace;

import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import shuntingYard.Side;


public class SpaceTrackSolution extends Solution {
	
	SpaceSolutionAssembly	space; 
	TrackSolution			track; 

	public SpaceTrackSolution(SpaceSolutionAssembly s1, TrackSolution s2) throws Exception { 
		super(s1.getMoment()); 
		this.setSpaceSolution(s1);
		this.setTrackSolution(s2);
	}

	@Override
	public void execute(Search search) throws Exception {
		this.executeDoNotMakeConsistent(search);
		this.space.makeConsistent();
	}

	public void executeFirst(Search search) throws Exception {
		this.invariant();
		this.track.removeOption(search); 
		this.track.assignFirstRoute();
		this.space.executeDoNotMakeConsistent();
		this.makeConsistent();
	}

	private void executeDoNotMakeConsistent(Search search) throws Exception {
		this.invariant(); 
		this.track.executeDoNotMakeConsistent(search);
		this.space.executeDoNotMakeConsistent();
	}

	@Override
	public boolean isFeasible() throws Exception {
		return this.track.isFeasible() && this.space.isFeasible();
	}
	
	private void setSpaceSolution(SpaceSolutionAssembly ss) throws Exception {
		this.invariant();
		super.setCostTlp(ss.getCostTlp());
		ss.setCostTrp(super.getCostTrp());
		if (null != this.track) {
			this.track.setCostTlp(ss.getCostTlp());
		}
		this.space 	= ss; 
		this.invariant();
	}
	
	public void setTrackSolution(TrackSolution ts) throws Exception {
		this.invariant();
		super.setCostTrp(ts.getCostTrp());
		ts.setCostTlp(super.getCostTlp());
		if (null != this.space) {
			this.space.setCostTrp(ts.getCostTrp());
		}
		this.track 	= ts; 
		this.invariant();
	}

	public Side getOption() {
		return this.track.getOption();
	}

	@Override
	public int getCostTrp() throws Exception {
		this.invariant();
		return super.getCostTrp();
	}

	@Override
	public int getCostTlp() throws Exception {
		this.invariant();
		return super.getCostTlp();
	}
	
	@Override
	public int getCost() throws Exception {
		this.invariant();
		return super.getCost(); 
	}

	public boolean hasFirstRouteAssigned() {
		return this.track.hasRouteAssigned();
	}

	protected void makeConsistent() throws Exception{
		this.track.makeConsistent();
		this.space.makeConsistent();
	}

	public SpaceSolutionAssembly getSpaceSolution() {
		return this.space;
	}
	
	public void setCostTlp(int costTlp) throws Exception {
		this.invariant();
		super.setCostTlp(costTlp);
		if (null != this.track) {			
			this.track.setCostTlp(costTlp);
		}
		this.space.setCostTlp(costTlp);
		this.invariant();
	}
	
	public void setCostTrp(int costTrp) throws Exception {
		this.invariant();
		super.setCostTrp(costTrp);
		if (null != this.space) {
			this.space.setCostTrp(costTrp);
		}
		this.track.setCostTrp(costTrp);
		this.invariant();
	}

	public TrackSolution getTrackSolution() {
		return this.track;
	}

	public void ignoreOtherRouteCost() {
		this.track.ignoreOtherRouteCost(); 
	}

	public void setTrackOtherRouteCost(int cost) throws Exception {
		this.invariant();
		this.getTrackSolution().setOtherRouteCost(cost);
		int total = getTrackSolution().getCostTrp(); 
		
		super.setCostTrp(total);
		if (null != this.space) {
			this.space.setCostTrp(total);
		}
		
		this.invariant();
	}

	public int getCostLeaveSideA() throws Exception {
		return this.track.getCostLeaveSideA();
	}

	public int getCostLeaveSideB() throws Exception {
		return this.track.getCostLeaveSideB();
	}
	
	public void invariant() throws Exception {
		int throwE = 0; 
		int superL 	= super.getCostTlp(); 
		int superR	= super.getCostTrp(); 
		int spaceL	= 0; 
		int spaceR	= 0; 
		int trackL	= 0; 
		int trackR	= 0; 
		if (null != this.space) {
			spaceL 	= this.space.getCostTlp(); 
			spaceR	= this.space.getCostTrp();
			if (superL != spaceL) {
				throwE = 1; 
			}
			if (superR != spaceR) {
				throwE = 2; 
			}
		}
		if (null != this.track) {
			trackL 	= this.track.getCostTlp(); 
			trackR	= this.track.getCostTrp();
			if (superL != trackL) {
				throwE = 3; 
			}
			if (superR != trackR) {
				throwE = 4; 
			}
		}
		if (0 != throwE) {
			trackL 	= this.track.getCostTlp(); 
			trackR	= this.track.getCostTrp();
			Exception e = new Exception("The costs are not consistent " + throwE + " (super, space, track): \n"
					+ "space: (" + superL + ", " + spaceL + ", " + trackL + ") \n"
					+ "track: (" + superR + ", " + spaceR + ", " + trackR + ") \n"
					+ this.toString()
					); 
			throw e;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.track.toString() + "\t" + this.space.toString()); 
		return sb.toString(); 
	}

}
