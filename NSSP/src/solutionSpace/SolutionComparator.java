package solutionSpace;

/**
 * This class is for comparing solutions of possible different types to obtain
 * the best solutions.
 * 
 * @author Daan
 *
 */
public final class SolutionComparator {
	
	/**
	 * Returns a negative number if the first solution (s1) is better than the
	 * second. Zero if equal and positive if the second solution is better.
	 * @throws Exception 
	 */
	public static int compare (Solution s1, Solution s2) throws Exception {
		String types = SolutionComparator.solutionTypes(s1, s2); 
		switch (types) {
			case "TrackTrack" 	: 
				return compareTrackTrack(s1, s2); 
			case "TrackRoute" 	: 
				return compareTrackRoute(s1, s2); 
			case "RouteTrack"	:
				return inverse(compareTrackRoute(s2, s1)); 
			case "RouteRoute"	:
				return compareRouteRoute(s1, s2); 
			case "SpaceSpace" :
				return s1.getCost() - s2.getCost(); 
			case "SpacetrackSpacetrack" :
				return compareSpaceTrackSpaceTrack(s1, s2); 
				//return s1.getCost() - s2.getCost(); // compareTrackTrack(((SpaceTrackSolution) s1).track, ((SpaceTrackSolution) s2).track); 
/*			case "SpacetrackTrack" :
				// We have to think about this one, may this exist is a relevant question...
				return compareTrackTrack(((SpaceTrackSolution) s1).track, s2); 
			case "TrackSpacetrack" :
				// We have to think about this one, may this exist is a relevant question...
				return compareTrackTrack(s1, ((SpaceTrackSolution) s2).track); 
*/			default				: 
				throw new Exception("The solutionTypes where not defined: " + types); 
				//return s1.getCost() - s2.getCost(); 
		}
		
	}

	private static int compareRouteRoute(Solution s1, Solution s2) throws Exception {
		RouteSolution t1 = (RouteSolution) s1; 
		RouteSolution t2 = (RouteSolution) s2; 	
		int s1Cost = t1.getCost(); 
		int s2Cost = t2.getCost(); 
		return s1Cost - s2Cost; 
	}

	private static int compareTrackRoute(Solution s1, Solution s2) throws Exception {
		TrackSolution t2 = (TrackSolution) s2; 
		// We only check on this cost since if a routeSolution enforces less
		// total cost we always want to take that solution.
		return inverse(t2.getCost()); 
	}

	private static int compareSpaceTrackSpaceTrack(Solution s1, Solution s2) throws Exception {	
		if (!s1.isFeasible()) {
			return 1; 
		}
		if (!s2.isFeasible()) {
			return -1; 
		}
		return s1.getCost() - s2.getCost();
	}

	private static int compareTrackTrack(Solution s1, Solution s2) throws Exception {	
			if (!s1.isFeasible()) {
				return 1; 
			}
			if (!s2.isFeasible()) {
				return -1; 
			}
		TrackSolution t1 = (TrackSolution) s1; 
		TrackSolution t2 = (TrackSolution) s2; 	
		int s1cost = t1.getCost(); // The costs:  first route + extra cost and the optional last route cost. 
		int s2cost = t2.getCost(); 		
		if (s1cost == s2cost) { 
			if (t1.getOption().getTrack().equals(t2.getOption().getTrack())) {
				// If the tracks are the same, we prefer the Aside option. 
				s1cost = (t1.getOption().isSideA()) ? -1 : 1; 
				s2cost = 0; 
			}
			else if (t1.hasLastRoute() && t2.hasLastRoute()) {
				// If both solutions contain a route to a next moment, we prefer
				// the option closest to the destination.
				s1cost = t1.getLastRouteCost(); 
				s2cost = t2.getLastRouteCost(); 
			}
		}
		return s1cost - s2cost;
	}

	private static String solutionTypes(Solution s1, Solution s2) {
		return solutionType(s1) + solutionType(s2);
	}

	private static String solutionType(Solution s1) {
		if (s1 instanceof TrackSolution) {
			return "Track"; 
		}
		if (s1 instanceof RouteSolution) {
			return "Route"; 
		}
		if (s1 instanceof SpaceTrackSolution) {
			return "Spacetrack"; 
		}
		if (s1 instanceof  SpaceSolution) {
			return "Space"; 
		}
		return s1.getClass().getName();
	}

	private static int inverse(int number) {
		return -number;
	}

}