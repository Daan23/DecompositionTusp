package trainLocationProblem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class TlpInstancePrinter {

	public static String TrackConsumers(TlpInstance instance) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("Track consumers of which a given moment knows \n\n"); 
		for (TlpMoment m : instance.getMoments()) {
			sb.append(m.toString() + "\n"); 
			TrackConsumers tc = m.getTrackConsumers(); 
			sb.append(tc.toString()); 
		}
		return sb.toString();
	}

	/**
	 * This method has as input a list of TlpMoments which are all stored on the
	 * same track. It outputs a graph with on the x-axis the time points on
	 * which the occupation of this track changes. On the y-axis the coordinates
	 * of each train are given.
	 * 
	 * @param listForSingleTrack
	 * @return
	 */
	public static String TrackToString(ArrayList<TlpMoment> list) {
		StringBuilder sb = new StringBuilder(); 
		
		ArrayList<Integer> yAxis = genYAxis(list); 
		for (int i=0; i<yAxis.size()-1; i++) {
			int y = yAxis.get(i); 
			sb.append(y); 
			y = yAxis.get((i<yAxis.size()-1)?i+1:0); 
			sb.append(momentsOnCoordinateToString(list, y)); 
			sb.append("\n"); 
		}	
		sb.append(yAxis.get(yAxis.size()-1) + "\n\t"); 
		
		ArrayList<Integer> xAxis = genXAxis(list); 
		for (int x : xAxis) {
			sb.append(x + "\t"); 
		}
		
		return sb.toString();
	}

	private static String momentsOnCoordinateToString(ArrayList<TlpMoment> list, int y) {
		StringBuilder sb = new StringBuilder(); 
		ArrayList<Integer> xAxis = genXAxis(list); 
		try {
			ArrayList<TlpMoment> momentsOnCoordinate = getMomentsOnCoordinate(list, y);
			int mIndex = 0; 
			sb.append("\n\t"); 
			for (int x : xAxis) {
				TlpMoment current = momentsOnCoordinate.get(mIndex); 
				
				if (happensBefore(current, x)) {
					if (mIndex < momentsOnCoordinate.size()-1) {
						mIndex ++; 
					}
					current = momentsOnCoordinate.get(mIndex); 
				}				
				
				if (happendOn(current, x)) {
					if (startsOn(current, x)) {
						sb.append(FirstTrainBlock(current)); 
					} 
					else {
						sb.append(MiddleTrainBlock(current)); 
					}
				}
				else  {
					sb.append("\t"); 
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return sb.toString(); 
	}

	private static Object MiddleTrainBlock(TlpMoment current) {
		return "__" + trainBlock(current);
	}

	private static String FirstTrainBlock(TlpMoment current) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("|_"); 
		sb.append(trainBlock(current)); 
		return sb.toString(); 
	}

	private static String trainBlock(TlpMoment current) {
		StringBuilder sb = new StringBuilder(); 
		String name = current.getTrainName(); 
		int length = name.length(); 
		for (int i=0;i<6; i++) {
			if (i<length) {
				sb.append(name.charAt(i)); 
			} else {
				sb.append("_"); 
			}
		}
		return sb.toString(); 
	}

	private static boolean startsOn(TlpMoment m, int x) {
		int begin = m.getTimePoint(); 
		return (begin == x);
	}

	private static boolean happendOn(TlpMoment m, int x) {
		int begin = m.getTimePoint(); 
		int end = m.getEndTime(); 
		return ((begin <= x) && (x < end));
	}

	private static boolean happensBefore(TlpMoment m, int x) {
		int end = m.getEndTime(); 
		return (end <= x);
	}

	private static ArrayList<Integer> genYAxis(ArrayList<TlpMoment> list) {
		ArrayList<Integer> l = new ArrayList<>(); 
		for (TlpMoment m : list) {
			int i = m.getCoordinate(); 
			int j = i + m.getTrainLength();			
			if (!l.contains(i)) {
				l.add(i); 
			}
			if (!l.contains(j)) {
				l.add(j); 
			}
		}
		
		Collections.sort(l, TlpInstancePrinter.inverseComparator());
		
		return l;
	}

	private static ArrayList<TlpMoment> getMomentsOnCoordinate(
			ArrayList<TlpMoment> list, int y) throws Exception {
		if (list.size() < 1) throw new Exception("The input list was empty"); 
		ArrayList<TlpMoment> l = new ArrayList<>(); 
		for (TlpMoment m : list) {
			if (m.isOnCoordinate(y)) {
				l.add(m); 
			}
		}
		Collections.sort(l, TlpInstancePrinter.TlpMomentTimeComparator()); 
		if (l.size()<1) throw new Exception("No moments on coordinate " + y + " in list " + list.toString() + " for track " + list.get(0).getTrack().toString()); 
		return l;
	}
	
	/**
	 * This method creates a list of all time points on which the track
	 * composition changes on the Track on which all moments in the input list
	 * are shunted.
	 * 
	 * @param list
	 * @return
	 */
	private static ArrayList<Integer> genXAxis(ArrayList<TlpMoment> list) {
		
		ArrayList<Integer> l = new ArrayList<>(); 
		
		for (TlpMoment m : list) {
			int begin = m.getTimePoint(); 
			if (!l.contains(m.getTimePoint())) {
				l.add(begin); 
			}
			int end = m.getEndTime();
			if (!l.contains(end)) {
				l.add(end); 
			} 
		}
		Collections.sort(l, TlpInstancePrinter.comparator()); 
		return l;
	}

	private static Comparator<TlpMoment> TlpMomentTimeComparator() {
		Comparator<TlpMoment> comp = new Comparator<TlpMoment>() {
			@Override
			public int compare(TlpMoment x, TlpMoment y) {
				return x.getTimePoint()-y.getTimePoint(); 
			}
		}; 
		return comp; 
	}
	
	private static Comparator<Integer> comparator() {
		Comparator<Integer> comp = new Comparator<Integer>() {
			@Override
			public int compare(Integer x, Integer y) {
				return x-y; 
			}
		}; 
		return comp; 
	}

	private static Comparator<? super Integer> inverseComparator() {
		Comparator<Integer> comp = new Comparator<Integer>() {
			@Override
			public int compare(Integer x, Integer y) {
				return y-x; 
			}
		}; 
		return comp; 
	}

	public static String freeRelocationsToString(TlpSolver solver) {
		StringBuilder sb = new StringBuilder(); 
		TlpInstance instance 			= solver.getInstance(); 
		Collection<TlpMoment> moments 	= instance.getMoments(); 
		sb.append("FreeRelocations: \n"); 
		for (TlpMoment m : moments) {
			sb.append(m.toString()); 
			sb.append(" can be located for free on: "); 
			sb.append(m.getFreeRelocations().toString()); 
			sb.append("\n"); 
		}
		sb.append("\n"); 
		return sb.toString();
	}

}
