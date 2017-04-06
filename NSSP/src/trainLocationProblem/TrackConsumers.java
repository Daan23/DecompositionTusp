package trainLocationProblem;

import java.util.ArrayList;
import java.util.HashMap;

import shuntingYard.Side;
import shuntingYard.Track;

public class TrackConsumers {
	
	/**
	 * The two sides to which other moments at the same time at the same track
	 * are stored relative to this moment.
	 */
	HashMap<Track, OccupiedSides> 	occupied; 
	
	
	public TrackConsumers() {
		this.occupied = new HashMap<>(); 
	}
	
	class OccupiedSides {
		
		
		/**
		 * The Side on which the moment arrives. 
		 */
		Side 							arrivingSide; 
		
		private ArrayList<TlpMoment> aSideMoments; 
		private ArrayList<TlpMoment> bSideMoments; 
		
		private OccupiedSides() {
			this.aSideMoments = new ArrayList<>(); 
			this.bSideMoments = null; 
		}

		public void add(TlpMoment m) {
			this.aSideMoments.add(m); 
		}

		public OccupiedSides getCopy() {
			OccupiedSides  s = new OccupiedSides(); 
			for (TlpMoment m : this.aSideMoments) {
				s.aSideMoments.add(m); 
			}
			if (null != this.bSideMoments) {
				for (TlpMoment m : this.bSideMoments) {
					s.bSideMoments.add(m); 
				}
			}
			return s;
		}

		public ArrayList<TlpMoment> getASideMoments() {
			if (null == this.bSideMoments) {
				this.constructBSideMoments(); 
			}
			return this.aSideMoments;
		}

		private void constructBSideMoments() {
			this.bSideMoments = new ArrayList<>(); 
		}

		public ArrayList<TlpMoment> getAllMoments() {
			ArrayList<TlpMoment> list = new ArrayList<>(); 
			list.addAll(this.aSideMoments); 
			if (null != this.bSideMoments) {
				list.addAll(this.bSideMoments);
			}
			return list;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder(); 
			sb.append(this.aSideMoments.toString()); 
			return sb.toString(); 
		}

		public void remove(TlpMoment m) {
			this.aSideMoments.remove(m); 
			if (null != this.bSideMoments) {
				this.bSideMoments.remove(m); 
			}
		}
	}

	public void add(TlpMoment m) throws Exception {
		this.add(m, m.getTrack());
	}

	private void add(TlpMoment m, Track newTrack) throws Exception {
		if(!this.occupied.containsKey(newTrack)) {
			this.occupied.put(newTrack, new OccupiedSides()); 
		}
		this.occupied.get(newTrack).add(m); 
	}

	public void remove(TlpMoment m, Track t) {
		OccupiedSides o1 = this.occupied.get(t); 
		if (null != o1) {
			o1.remove(m); 
		}
	}

	public void remove(TlpMoment m)  throws Exception{
		this.remove(m, m.getTrack());
	}

	public TrackConsumers getCopy() {
		TrackConsumers tc = new TrackConsumers(); 
		for (Track s : this.occupied.keySet()) {
			OccupiedSides sm = this.occupied.get(s).getCopy(); 
			tc.occupied.put(s, sm);
		}
		return tc;
	}

	public ArrayList<TlpMoment> getASideMoments(Track track) {
		OccupiedSides o = this.occupied.get(track); 
		return o.getASideMoments();
	}

	public OccupiedSides get(Track track) {
		OccupiedSides os 	= this.occupied.get(track); 
		if (null == os) {
			os = new OccupiedSides(); 
		}
		return os;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		for (Track key : this.occupied.keySet()) {
			if (!key.getName().equals("dummy")) {
				sb.append("\t" + key.getName() + ": "); 
				sb.append(this.occupied.get(key).toString()); 
			}
		}
		return sb.toString(); 
	}

	public ArrayList<TlpMoment> getAllMoments() {
		ArrayList<TlpMoment> moments = new ArrayList<>(); 
		for (OccupiedSides o : this.occupied.values()) {
			for (TlpMoment m : o.getAllMoments()) {
				moments.add(m); 
			}
		}
		return moments;
	}

	public void relocate(TlpMoment m, Track oldTrack, Track newTrack) throws Exception {
		this.remove(m, oldTrack);
		this.add(m, newTrack);
	}

}
