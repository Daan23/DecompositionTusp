package trainLocationProblem;

import java.util.Collection;
import java.util.HashMap;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.ObservedMoment;

public class TlpInstance {
	
	/**
	 * When another problem suggest a solution to which we need to add
	 * coordinates, we need to be able to efficiently find the TlpMoment to
	 * which the to-be-changed ObservedMoment belongs. 
	 */
	private HashMap<Moment, TlpMoment> 	moments; 
	
	/**
	 * The original instance on which this instance of this subproblem is based.
	 * We keep a reference to the original instance since it contains the
	 * shunting yard layout which we do not need to copy.
	 */
	private NsspInstance instance; 
	
	private LocationCost locationCost; 
	
	public TlpInstance (NsspInstance instance, LocationCost locationCost) throws Exception {
		this.instance		= instance; 
		this.moments		= new HashMap<>(); 
		this.locationCost	= locationCost; 
		this.createMoments(); 
	}

	/**
	 * The TlpMomentFactories which are created are set as observers to the
	 * baseMoments. These factories add or remove the TlpMoments within this
	 * TlpInstance.
	 * @throws Exception 
	 */
	private void createMoments() throws Exception {
		HashMap<Train, TlpMoment> lastMoments = new HashMap<>(); 
		for (Moment m : this.instance.getMoments()) {
			// Be careful, we do assume that they are added in order of their occurrence. 
			TrackConsumers previous = this.getLastAddedTrackConsumers(); 
			
			if (lastMoments.containsKey(m.getTrain())) {
				TlpMoment del = lastMoments.get(m.getTrain());
				previous.remove(del); 
				lastMoments.remove(m.getTrain()); 
			}
			
			TlpMoment lastAdded = this.getLastAddedMoment(); 
			if (null != lastAdded) {			
				if (!m.getTrain().equals(lastAdded.getTrain())) {
					this.addMomentToTrackConsumers(previous, lastAdded); 
				}
			}

			
			// A reference of this new factory is kept as listener of ObeservedMoment m. 
			// This constructor adds the needed TlpMoments to this instance. 
			new TlpMomentFactory(this, (ObservedMoment) m, previous, this.locationCost);
			
			lastMoments.put(m.getTrain(), this.getLastAddedMoment()); 
			

			// Add new moment to relevant old track consumers 
			for (TlpMoment q : previous.getAllMoments()) {
				q.addMomentToTrackConsumers(this.getLastAddedMoment()); 
			}
		}
	}

	/**
	 * This method adds the moment which was added last to the instance to the
	 * TrackConsumers object. So that the new TrackConsumers object can be used
	 * for the next moment to be added.
	 * 
	 * @param tc
	 * @throws Exception 
	 */
	private void addMomentToTrackConsumers(TrackConsumers tc, TlpMoment m) throws Exception {
		if (0 < this.moments.size()) {
			if (!m.getTrack().equals(this.instance.getShuntingYard().getExit())) {
				tc.add(this.getLastAddedMoment()); 
			}
		}
	}

	private TrackConsumers getLastAddedTrackConsumers() {
		if (0 < this.moments.size()) {
			return this.getLastAddedMoment().getTrackConsumers().getCopy(); 
		}
		return new TrackConsumers(); 
	}

	private TlpMoment getLastAddedMoment() {
		TlpMoment last = null; 
		for (TlpMoment m : this.moments.values()) {
			if (null == last || last.getTimePoint() < m.getTimePoint()) {
				last = m; 
			}
		}
		return last;
	}

	public void addMoment(TlpMoment m) {
		this.moments.put(m.getObservedMoment(), m); 
	}

	public Collection<TlpMoment> getMoments() {
		return this.moments.values();
	}
	
	public HashMap<Moment, TlpMoment> getMomentsHashMap() {
		return this.moments; 
	}

	public TlpMoment getMoment(Moment m) {
		return this.moments.get(m);
	}

	public NsspInstance getBaseInstance() {
		return this.instance;
	}

	public int getCost() throws Exception {
		int cost = 0; 
		for (TlpMoment m : this.moments.values()) {
			cost += m.getCost(); 
		}
		return cost;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
		sb.append("TlpInstance: \n"); 
		for (TlpMoment m : this.moments.values()) {
			sb.append(m.toString()); 
			sb.append("  cost:" + m.getCost() + "\t"); 
			sb.append("free:" + m.getFreeRelocations().toString()); 
			//sb.append("consumers: " + m.getTrackConsumers().toString()); 
			sb.append("\n"); 
		}
		sb.append("\n"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString(); 
	}

}
