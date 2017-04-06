package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import shuntingYard.Side;
import shuntingYard.Track;

public class TrackSolutionSpace implements Iterable<Side>{

	private ArrayList<Side> options; 
	
	private ArrayList<Side> initialTracks; 
	
	public TrackSolutionSpace() {
		this.options		= new ArrayList<>(); 
		this.initialTracks 	= new ArrayList<>(); 
	}
	
	public boolean add (Side s) {
		boolean b = this.options.add(s); 
		boolean c = initialTracks.add(s); 
		return b && c; 
	}

	public Side remove(int index) {
		Side b = this.options.remove(index); 
		if (AlgSettings.removeBothSidesOfOption) {
			try {
				this.options.remove(b.getOtherSide());
			} catch (Exception e) {
				e.printStackTrace();
				throw new NullPointerException(); 
			}
		}
		return b;
	}

	public void remove(Side s) throws Exception {
		boolean b = this.options.remove(s); 
		if (AlgSettings.removeBothSidesOfOption || !b) {
			b = b || this.options.remove(s.getOtherSide());
		}
		if (!b) {
			throw new Exception("the option was not given in the options of : " + s.toString() + " not in " + this.toString());
		}
	}
	
	public void removeFromInitialOptions(int s) {
		this.initialTracks.remove(s);
		this.options.remove(s); 
	}
	
	public boolean addAll(Collection<? extends Side> set) {
		boolean b = this.options.addAll(set); 
		boolean c = this.initialTracks.addAll(set); 
		return b && c;
	}
	
	public void reset() {
		this.options.clear();
		this.options.addAll(initialTracks); 
	}

	public boolean isEmpty() {
		return this.options.isEmpty();
	}

	public boolean contains(Side s) {
		return this.options.contains(s);
	}

	public Side get(int i) {
		return this.options.get(i);
	}

	public int size() {
		return this.options.size();
	}

	@Override
	public Iterator<Side> iterator() {
		return this.options.iterator();
	}

	/**
	 * Restores the location options to the original initialization. 
	 */
	@SuppressWarnings("unchecked")
	public void restore() {
		this.options = (ArrayList<Side>) this.initialTracks.clone(); 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("{"); 
		for (Side s : this.options) {
			sb.append(s.getTrack().getName()); 
			sb.append(", "); 
		}
		sb.append("}"); 
		return sb.toString(); 
	}

	/**
	 * This method is ugly and should never be used. It exists because with
	 * reading out the database, we first construct the moments, and then want
	 * to retreive the data from the moments and construct new moments within
	 * the NSSP instance.
	 * 
	 * @return
	 */
	public ArrayList<Track> getCollectionOfTracks() {
		ArrayList<Track> tracks = new ArrayList<>(); 
		for (Side s : this.initialTracks) {
			if(!tracks.contains(s.getTrack())) {
				tracks.add(s.getTrack()); 
			}
		}
		return tracks;
	}

	public void permanentlyRemoveOption(Track t) {
		this.initialTracks.remove(t.getSideA()); 
		this.initialTracks.remove(t.getSideB()); 
		this.options.remove(t.getSideA()); 
		this.options.remove(t.getSideB()); 
	}

}
