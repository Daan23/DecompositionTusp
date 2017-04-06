package trainRoutingProblem;

import java.util.PriorityQueue;

import shuntingYard.Location;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.TrackConnection;
import solutionSpace.RouteSolution;
import solutionSpace.SolutionSet;
import trainRoutingProblem.RouteCost.DijkstraRouteCost;

public class Dijkstra {
	private ShuntingYard yard; 
	private RouteCost routeCost; 
	
	private static final boolean DEBUG = false; 
	
	public Dijkstra(ShuntingYard yard, RouteCost routeCost) {
		this.yard = yard; 
		this.routeCost = routeCost; 
	}

	/**
	 * This method returns the best possible route given a fixed source and
	 * destination. The TrpSolutionSet will therefore only contain one solution.
	 * This method expects the TrpMoment to have a location already assigned. It
	 * returns a SolutionSet with a single solution.
	 * 
	 * @param m
	 * @return One or two route solutions leading to the location which is
	 *         already assigned.
	 * @throws Exception
	 */
	public SolutionSet singleFixedLocation(TrpMoment m) throws Exception {
		DijkstraGraph nodes1 = new DijkstraGraph(yard, false); 
		nodes1.initStartLocation(m.getPreviousLocation().getHead(), 0);
		nodes1.initStartLocation(m.getPreviousLocation().getTail(), 0);
		this.routeCost.init(nodes1, m.getBlocked(), m.getPrevious().getCoordinate()); 
		this.dijkstraTurns(nodes1, m.getPreviousLocation(), this.routeCost.normalDijkstra(), m.getTrainLength());
		
		SolutionSet tss 	= new SolutionSet(); 
		Side a				= m.getLocation().getTrack().getSideA();
		if  (m.getBaseMoment().getOptions().contains(a)) {
			RouteSolution r1	= new RouteSolution(m, nodes1, a); 
			tss.add(r1);
		}
		
		if (!a.equals(yard.getExit())) {
			Side b				= m.getLocation().getTrack().getSideB();
			if  (m.getBaseMoment().getOptions().contains(b)) {
				RouteSolution r2	= new RouteSolution(m, nodes1, b); 
				tss.add(r2);
			}
		}
		
		return tss;
	}

	/**
	 * Calculates the optimal location to store the input moment, given the
	 * moments influenced by the location change of this moment. Note that the
	 * calculated cost for the route are not the cost of the route, but the
	 * difference in total route cost for the whole schedule. The route cost
	 * should therefore be updated if the route of the input TrpMoment is changed
	 * to the route that is returned by this method.
	 * 
	 * @param m
	 * @return
	 * @throws Exception 
	 */
	public SolutionSet doubleDijkstra(TrpMoment m) throws Exception {
		DijkstraOutput out = this.doubleDijkstraRawOutput(m); 
		return new TrpSolutionSet(m, out); 
	}
	

	public DijkstraOutput doubleDijkstraRawOutput(TrpMoment m) throws Exception {
		DijkstraOutput  out = new DijkstraOutput(m.baseMoment, m.getNext().baseMoment); 
		if(DEBUG) {
			System.out.println("==========================dd1========================");
		}
		out.firstRoutes = new DijkstraGraph(yard, false); 
		out.firstRoutes.initStartLocation(m.getPreviousLocation().getHead(), 0);
		out.firstRoutes.initStartLocation(m.getPreviousLocation().getTail(), 0);
		this.routeCost.init(out.firstRoutes, m.getBlocked(), m.getPrevious().getCoordinate()); 
		this.dijkstraTurns(out.firstRoutes, m.getPreviousLocation(), this.routeCost.normalDijkstra(), m.getTrainLength());

		if(DEBUG) {
			System.out.println("==========================dd2========================");
		}
		Side destination = m.getArrivingSideOfNextMoment(); 
		if (destination.equals(yard.getExit())) {
			destination = yard.getInverseExit(); 
		}
		out.lastRoutes = new DijkstraGraph(yard, true); 
		out.lastRoutes.initStartLocation(destination, 0);
		out.lastRoutes.initStartLocation(destination.getOtherSide(), 0);
		
		int coordinate = m.getNext().getBaseMoment().hasLocation() ? m.getNext().getCoordinate() : 0; 
		this.routeCost.init(out.lastRoutes, m.getNext().getBlocked(), coordinate); 
		Location nextLocation = this.getNextLocation(m); 
		this.dijkstraTurns(out.lastRoutes, nextLocation, this.routeCost.InvertedDijkstra(), m.getTrainLength());
		
		return out; 
	}
	
	/**
	 * If we want to use the exit of the shuntingYard as a starting point for
	 * dijkstra, we need the exit which contains all inverse edges.
	 * 
	 * @param moment
	 * @return
	 * @throws Exception 
	 */
	private Location getNextLocation(TrpMoment moment) throws Exception {
		Location l = new Location(moment.getArrivingSideOfNextMoment()); 
		if(l.getArrivingSide().equals(this.yard.getExit())) {
			l = yard.getExitAsStartingPoint(); 
		}
		return l;
	}

	public TrpSolutionSet singleWithTurns(TrpMoment m) throws Exception {		
		if(DEBUG) {
		System.out.println("========================single=======================");
		}

		DijkstraGraph nodes = new DijkstraGraph(yard, false); 
		nodes.initStartLocation(m.getPreviousLocation().getHead(), 0);
		nodes.initStartLocation(m.getPreviousLocation().getTail(), 0);
		this.routeCost.init(nodes, m.getBlocked(), m.getPrevious().getCoordinate()); 
		this.dijkstraTurns(nodes, m.getPreviousLocation(), this.routeCost.normalDijkstra(), m.getTrainLength());
		
		return new TrpSolutionSet(m, nodes); 
	}
	
	private void dijkstraTurns(DijkstraGraph nodes, Location from, DijkstraRouteCost drc, int trainLength) throws Exception {
		// Put the edges leaving from the initial location in the priority queue. 
		PriorityQueue<TrackConnection> edges = new PriorityQueue<TrackConnection>(2*yard.getAllTracks().size(), drc); 
		edges.addAll(from.getDepartures()); 
		
		// Add next location 
		while(!edges.isEmpty()) {
			TrackConnection edge = edges.poll(); 
if(DEBUG) {		
			System.out.println("old TrackConnection:" + edge.toString());
			System.out.println(nodes.get(edge.getSource()).toString());
			System.out.println(nodes.get(edge.getSink()).toString());
			System.out.println("Q: " + edges.toString() + "\n");
}			

			DijkstraNode source	= nodes.get(edge.getSource()); 
			DijkstraNode sink 	= nodes.get(edge.getSink()); 
			if(!sink.isReached()){
				if (edge.isConnectedToSameTrack()) {
					sink.setOrigin(source);
					sink.setCost(drc.value(edge));
					edges.addAll(sink.getTwinConnectedSides()); 
				}
				else {
					sink.setOrigin(source.getTwin());
					sink.setCost(drc.value(edge));
					edges.addAll(sink.getTwinConnectedSides()); 
					
					if(!sink.twinIsReached()) {
						if (sink.getTrack().getLength() >= trainLength) {
							edges.add(sink.CreateTrackConnectionToTwin()); 
						}
					}
				}
			}

if(DEBUG) {		
			System.out.println("new TrackConnection:" + edge.toString());
			System.out.println(nodes.get(edge.getSource()).toString());
			System.out.println(nodes.get(edge.getSink()).toString());
			System.out.println("Q: " + edges.toString() + "\n");
}			
		}
		
	}
		
	public String toString(DijkstraGraph nodes) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("nodes: \n"); 
		for(DijkstraNode node : nodes.values()) {
			sb.append(node.toString() + "\n");
		}
		return sb.toString(); 
	}
	
}