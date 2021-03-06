package trainRoutingProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import shuntingYard.ShuntingYard;
import shuntingYard.Side;
import shuntingYard.Track;

public class TrpInstance {

	private NsspInstance 				instance; 		
	private ShuntingYard 				yard; 			// The shunting yard 

	// We now have three data structures where Trp moments are stored. This
	// should be refactored to one. The treeSet is to keep an ordered set of the
	// moments, this is only need to be ordered for printing methods. Printing
	// methods should sort the moments themselves if needed. The same holds for
	// the firstMoments. The only one we should keep is the momentsHashMap. We
	// need to get TrpMoments belonging to a given ObservedMoment in order to
	// add routes to solutions where a track location is changed.
	
	private TreeSet<TrpMoment> 			moments; 		// All moments for all trains, stored in order of their occurance
	private HashMap <Moment, TrpMoment>	momentsHashMap;	// All moments with their ObservedMoment as key. 
	private HashMap<Train, TrpMoment> 	firstMoments; 	// The first moment for each train. 
	
	public TrpInstance(NsspInstance instance, RouteCost routeCost) throws Exception {
		
		this.instance		= instance; 
		this.firstMoments	= new HashMap<>(); 
		this.yard			= instance.getShuntingYard(); 
		
		this.moments 		= new TreeSet<>(this.comparator());  
		this.momentsHashMap = new HashMap<>(); 
		
		TrpMoment.init(this.yard, routeCost);
		
		for (ObservedMoment m : instance.getMoments()) {
			TrpMoment t = new TrpMoment(m); 
			this.addMoment(t);
		}

		this.initBlocked(); 

	}
	
	private void initBlocked() throws Exception {
		HashMap<Train, TrpMoment> currentMoments = new HashMap<>(); 
		for (TrpMoment m :this.getMoments()) { 
			currentMoments.remove(m.getTrain()); 
			m.setBlockedTracks(currentMoments); 
			if (!this.leavesYard(m)) {
				currentMoments.put(m.getTrain(), m); 
			}
		}
	}

	public boolean leavesYard(TrpMoment m) throws Exception {
		boolean b = false; 
		// only if it is the last moment it will leave the shuntingyard
		if (m.getBaseMoment().getNext().isEndMoment()) {
			// if the moment has more than one location option it will not leave the yard. 
			if (m.getBaseMoment().getOptions().size() == 1) {
				Side s = m.getBaseMoment().getFirstPossibleSide(); 
				if (s.equals(this.yard.getExit())) {
					b = true; 
				}
			}
		}
		return b;
	}
	
	private Comparator<TrpMoment> comparator() {
		Comparator<TrpMoment> comp = new Comparator<TrpMoment>() {
			@Override
			public int compare(TrpMoment x, TrpMoment y) {
				return compareMoments(x, y); 
			}
		}; 
		return comp; 
	}
	
	private int compareMoments(TrpMoment x, TrpMoment y) {
		int xVal=x.getTimePoint(); 
		int yVal=y.getTimePoint(); 
	    if (xVal < yVal) {
	        return -1;
	    }
	    if (xVal > yVal) {
	        return 1;
	    }
	    return 0;
	}
	
	/**
	 * Returns the first moment of each train in the schedule. The list is
	 * sorted, the moment which happens first in time, occurs first in the list.
	 * 
	 * @return
	 */
	public ArrayList<TrpMoment> getFirstMoments() {
		ArrayList<TrpMoment> list = new ArrayList<>( this.firstMoments.values()); 
		Collections.sort(list, this.comparator()); 
		return list; 
	}
	
	public TreeSet<TrpMoment> getMoments(){
		return this.moments; 
	}

	public void addMoment(TrpMoment m) throws Exception {
		this.moments.add(m); 
		this.momentsHashMap.put(m.getBaseMoment(), m); 
		
		if (this.firstMoments.containsKey(m.getTrain())) {
			TrpMoment first 		= this.firstMoments.get(m.getTrain());  
			TrpMoment predecessor 	= first.getLastMoment(); 
			predecessor.setNext(m);
		}
		else {
			this.firstMoments.put(m.getTrain(), m); 
		}
	}
	
	public TrpMoment getMoment(Moment m) {
		return this.momentsHashMap.get(m);
	}
	
	public ShuntingYard getShuntingYard() {
		return this.yard;
	}

	public int numberOfMoments() {
		return this.momentsHashMap.size();
	}

	public Track getTrack(String name) {
		return this.yard.getTrack(name);
	}

	public String toStringExtra() {
		StringBuilder sb = new StringBuilder(); 
		try {
		sb.append("TRP instance: \n"); 
		sb.append("cost=" + this.getCost() + "\n"); 
		
		ArrayList<Integer> xAxis = this.getAllTimePoints();  
		
		//print a line for each train
		for(TrpMoment m : this.getFirstMoments()) {
			sb.append(ConsoleOut.trainToString(m, xAxis) + "\n"); 
 			sb.append("-" + ConsoleOut.trainOptionsToString(m, xAxis) + "\n"); 
		}
		
		// Add index
		sb.append("\t" + this.line(this.momentsHashMap.size()) + "\n"); 
		for (int x : xAxis) {
			sb.append("\t" + x); 
		}
		sb.append("\n"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		try {
		sb.append("TRP instance: \n"); 
		sb.append("cost=" + this.getCost() + "\n"); 
		
		ArrayList<Integer> xAxis = this.getAllTimePoints();  
		
		//print a line for each train
		for(TrpMoment m : this.getFirstMoments()) {
			sb.append(ConsoleOut.trainToString(m, xAxis) + "\n"); 
		}
		
		// Add index
		sb.append("\t" + this.line(this.momentsHashMap.size()) + "\n"); 
		for (int x : xAxis) {
			sb.append("\t" + x); 
		}
		sb.append("\n"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}

	private ArrayList<Integer> getAllTimePoints() {
		return this.instance.getAllTimePoints();
	}

	private String line(int length) {
		StringBuilder sb = new StringBuilder(); 
		for (int i=0; i<length; i++) {
			sb.append("______\t"); 
		}
		return sb.toString();
	}

	/** 
	 * Return the sum of the route cost for each moment.  
	 * @return
	 * @throws Exception 
	 */
	public int getCost() throws Exception {
		int cost = 0; 
		for (TrpMoment m : this.getMoments()) {
			if (m.getBaseMoment().hasRoute()) {
				int routeCost 	= m.getBaseMoment().getRouteCost(); 
				int newCost 	= cost + routeCost; 
				if(routeCost>0 && cost>0 && newCost<0) {
					// We exceed the max size of integers
					return Integer.MAX_VALUE; 
				}
				cost = newCost; 
			}
		}
		if (cost <0) throw new Exception("The cost of a TRP may never be smaller than 0"); 
		return cost;
	}

}
