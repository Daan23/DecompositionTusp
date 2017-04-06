package trainRoutingProblem;

import nedtrainSchedulingShuntingProblem.ObservedMoment;

public class DijkstraOutput {
	public DijkstraGraph firstRoutes; 
	public DijkstraGraph lastRoutes; 
	public ObservedMoment firstM; 
	public ObservedMoment lastM; 
	
	public DijkstraOutput(ObservedMoment firstM, ObservedMoment lastM) {
		this.firstM			= firstM; 
		this.lastM			= lastM; 
	}
}