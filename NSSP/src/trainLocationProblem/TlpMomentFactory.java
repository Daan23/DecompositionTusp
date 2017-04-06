package trainLocationProblem;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.MomentObserver;
import nedtrainSchedulingShuntingProblem.ObservedMoment;
import nedtrainSchedulingShuntingProblem.TrackSolutionSpace;
import shuntingYard.Location;
import shuntingYard.Route;
import shuntingYard.Side;
import shuntingYard.Track;
import shuntingYard.TrackConnection;

public class TlpMomentFactory implements MomentObserver{

	private ArrayList<TlpMoment> 	createdMoments; 	// All the moments which are created by this factory. 
	private ArrayList<Track>		oldRoute; 			// A copy of the route of the observed moment. 
	private TlpInstance 			instance; 			// The instance to which the moments are added. 
	private TrackConsumers 			trackConsumers; 	// Per track we store the moments on that share a timepoint with this moment. 
	private ObservedMoment			moment; 			// The moment which is observed by this factory. 
	
	public TlpMomentFactory (TlpInstance instance, ObservedMoment m, TrackConsumers trackConsumers, LocationCost locationCost) throws Exception {
		this.instance 		= instance; 
		this.moment			= m; 
		this.assignRoute(); 
		this.assignTrackConsumers(trackConsumers); 
		this.createInitialMoments(m, locationCost); 
		m.addObserver(this);
	}
		private void assignRoute() throws Exception {
		if (this.moment.hasRoute()) {
			this.setRoute(this.moment.getRoute()); 
		}
	}

	private void assignTrackConsumers(TrackConsumers consumers) throws Exception {
		this.trackConsumers = consumers; 
		this.invariant(); 
	}

	private void setRoute(Route route) {
		this.oldRoute		= new ArrayList<>(); 
		for (Track t : route.getTracks()) {
			this.oldRoute.add(t); 
		}
	}

	private void createInitialMoments(ObservedMoment om, LocationCost locationCost) throws Exception {
		
		// Code below for assigning route only works with a single moment to be added per route
		int coordinate = 0; 
		if (om.hasLocation()) {
			coordinate = om.getCoordinate(); 
		}
		
		this.createdMoments	= new ArrayList<>(); 
		ArrayList<Side> stops 	= this.getStops(om);
		for(Side stop : stops) {
			Location l = new Location(stop);
			l.setCoordinate(coordinate);
			TlpMoment m = new TlpMoment(l, this, locationCost); 
			this.createdMoments.add(m); 
			this.instance.addMoment(m); 
		}
	}

	/**
	 * This method returns all sides where a train arrives to make a turn, plus
	 * the last side where the moment will arrive to be shunted. If the route is
	 * null, this method returns a dummy location representing the location on
	 * which the moment will be shunted.
	 * 
	 * @return
	 * @throws Exception 
	 */
	private ArrayList<Side> getStops(ObservedMoment om) throws Exception {
		ArrayList<Side> stops = new ArrayList<>(); 
		if (om.hasRoute()) {
			if (om.getRoute().getTracks().size() > 1) {
				Track ppTrack = null; 
				Track pTrack = null; 
				for (Track t : om.getRoute().getTracks()) {
					// If a track occurs two times in a row, this indicates a turn is made on that track. 
					if (t.equals(pTrack)) {
						Side side = this.getArrivingSide(ppTrack, pTrack); 
						stops.add(side); 
					}
					ppTrack = pTrack; 
					pTrack = t; 
				}
				// The last track is the track on which the moment is stored. 
				Side side = this.getArrivingSide(ppTrack, pTrack); 
				stops.add(side); 
			}
			else {
				/*
				 * This else statement is difficult, a route of length 1
				 * means it stays on the same track. Therefore we cannot say
				 * anymore that it arrived at either one of the sides. Consider
				 * the case below. Let t2 be the moment belonging to the same
				 * train as t1 and staying on the same track. Saying that t2
				 * arrives at either of the two sides would place it above b or
				 * below c, enforcing a route conflict (one train jumping over
				 * the other). We cannot just couple the two moments, since they
				 * have a different location which is enforced by a and d.
				 */
				
				/* 
				 * ___	_______
				 * | |	|__b__|
				 * |a|		________
				 * |_|		|__t2__|
				 * ________		___
				 * |__t1__|		| |
				 * 		_______	|d|
				 * 		|__c__|	|_|
				 */
				
				// We cannot deal with this currently
				throw new Exception("The current implementation cannot deal with relocating a train on the same track."); 
			}
		} 
		else {
			// Add dummy track to the stops to indicate a location which still needs to be determined. 
			stops.add(Side.dummy());
		}
		return stops;
	}

	private Side getArrivingSide(Track from, Track to) throws Exception{
		
		TrackConnection tc = from.getTrackConnectionTo(to); 
		return tc.getSink(); 
	}

	@Override
	public void momentIsChanged(Moment m) throws Exception {
		// We currently assume only one moment to be created by this factory. 
		TlpMoment changedMoment = this.createdMoments.get(this.createdMoments.size()-1); 
		
		if (!m.hasLocation()) {
			return; 
		}
		
		if (!changedMoment.getArrivingSide().equals(m.getArrivingSide())) {
			for (TlpMoment q : this.trackConsumers.getAllMoments()) {
				Track newTrack 			= m.getTrack(); 
				q.relocateTrackConsumer(changedMoment, changedMoment.getTrack(), newTrack); 
			}
			changedMoment.baseMomentIsChanged(m.getLocation());
		} else if (changedMoment.getCoordinate() != m.getLocation().getCoordinate()) {
			changedMoment.baseMomentIsChanged(m.getLocation());
		}
	}

	public TrackConsumers getTrackConsumers() {
		return this.trackConsumers;
	}

	public ObservedMoment getMoment() {
		return this.moment;
	}

	public void addMomentToTrackConsumers(TlpMoment m) throws Exception {
		this.trackConsumers.add(m);
		this.invariant(); 
	}

	private void invariant() throws Exception {
		for (TlpMoment m : this.trackConsumers.getAllMoments()) {
			if (m.getTimePoint() == this.moment.getTimePoint()) {
				throw new Exception ("A moment with the same id is present in the trackConsumers: " + m.getTimePoint()); 
			}
			if (m.getTrainName().equals(this.moment.getTrain().getName())) {
				throw new Exception ("Moment " + m.getTimePoint() + " may not be added as trackConsumer of Moment " + this.moment.getTimePoint() + ", since both belong to train " + m.getTrainName());
			}
		}
	}

	public int getEndTime(TlpMoment m) {
		if (isLastCreatedMoment(m)) {
			if (null != this.moment.getNext()) {
				return this.moment.getNext().getTimePoint(); 
			} 
			else {
				return Integer.MAX_VALUE; 
			}
		}
		return this.moment.getTimePoint();
	}

	public TrackSolutionSpace getSideOptions(TlpMoment m) {
		if (isLastCreatedMoment(m)) {
			return this.getMoment().getOptions(); 
		}
		return new TrackSolutionSpace();
	}

	private boolean isLastCreatedMoment(TlpMoment m) {
		return (m.equals(this.createdMoments.get(this.createdMoments.size()-1)));
	}

	@Override
	public void makeConsistent() throws Exception {
		TlpMoment m = this.createdMoments.get(0); 
		if (m.hasToBeMadeConsistent()) {
			m.calculateFreeRelocations(); 
			for (TlpMoment q : this.trackConsumers.getAllMoments()) {
				q.calculateFreeRelocations();
			}
		}
	}
	
	public String toString() {
		return "TlpMomentFactory of " + this.getMoment().toString(); 
	}

}
