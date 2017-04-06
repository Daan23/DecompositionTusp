package trainLocationProblem;

import java.util.ArrayList;

public class LocationConflict {
	
	/**
	 * A path from a moment (index 0) located further than the length of the
	 * track (coordinate c > trackLength - trainLength) to one of the moments
	 * located at the beginning of the track (coordinate 0, index n).
	 */
	private ArrayList<TlpMoment> contributingMoments; 
	
	public LocationConflict(ArrayList<TlpMoment> contributing) throws Exception {
		this.contributingMoments = contributing; 
	}


	public TlpMoment get(int i) {
		return this.contributingMoments.get(i);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append("LocationConflict: {"); 
		for (TlpMoment m : this.contributingMoments) {
			sb.append(m.toString()); 
			sb.append(", "); 
		}
		sb.append("}\n"); 
		return sb.toString(); 
	}

	public ArrayList<TlpMoment> getContributingMoments() {
		return this.contributingMoments;
	}
	
}
