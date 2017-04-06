package trainLocationProblem;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.NsspSolver;
import shuntingYard.Track;

/**
 * This class is used if tracks are not consistent after a solution anymore.
 * With consistent here we mean we cannot guarantee that: a) trains do not
 * overlap, b) trains start at coordinate 0, c) trains are put exactly next
 * to each other.
 */
public class TrackConsistentMaker {

	private static ArrayList<Track> inconsistenTracks;

	public static void addTrack(Track track) {
		if (null == inconsistenTracks) {
			inconsistenTracks = new ArrayList<>(); 
		}
		inconsistenTracks.add(track);
	}

	public static void makeConsistent(NsspSolver ns) throws Exception {
		if (null == inconsistenTracks) {
			inconsistenTracks = new ArrayList<>(); 
		}
		if (!inconsistenTracks.isEmpty()) {
			TlpSolver tlp = ns.getTlp(); 
			tlp.createInitialSchedule();
			inconsistenTracks.clear();
		}
	}

	public static boolean isNotConsistent() {
		if (null == inconsistenTracks) {
			inconsistenTracks = new ArrayList<>(); 
		}
		return !inconsistenTracks.isEmpty();
	} 
	
	
	
	
	
	
	
}
