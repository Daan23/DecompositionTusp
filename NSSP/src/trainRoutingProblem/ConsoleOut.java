package trainRoutingProblem;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.Moment;
import shuntingYard.Side;
import shuntingYard.Track;

public class ConsoleOut {

	public static String trainOptionsToString(TrpMoment m,
			ArrayList<Integer> xAxis) throws Exception {
		StringBuilder sb = new StringBuilder(); 
		sb.append("opt" + ": \t"); 
		String s = ConsoleOut.trainDataToString(m, xAxis, new TrainData() {
			@Override
			public String trainData(TrpMoment m) throws Exception {
				StringBuilder sb = new StringBuilder(); 
				for (Side s : m.getBaseMoment().getOptions()) {
					try {
						sb.append(s.getTrack().getName());
						sb.append(",");
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				return sb.toString();
			}
		});
		sb.append(s); 
		return sb.toString();
	}

	public static String trainToString(TrpMoment m, ArrayList<Integer> xAxis) throws Exception {
		StringBuilder sb = new StringBuilder(); 
		sb.append(m.getTrain().getName() + ":\t"); 
		
		String s = ConsoleOut.trainDataToString(m, xAxis, new TrainData() {
			@Override
			public String trainData(TrpMoment m) throws Exception {
				StringBuilder sb = new StringBuilder(); 
				if(m.getBaseMoment().hasRoute()) {
					// If a conflict occurs at this route
					if (m.getBaseMoment().getRoute().getCost() > 1000000) {
						sb.append("!"); 
					}
					String s = m.getRoute().toString();
					sb.append(s); 
				}
				return sb.toString();
			}
		}); 
		
		sb.append(s); 
		
	/*	
		
		
			TrpMoment current 	= m; 
			int printedLength = 0; 
			
			for (int x : xAxis) {
				if (current.getTimePoint() == x) {

					if(current.getBaseMoment().hasRoute()) {
						// If we did not yet empty the printedLength yet
						if (printedLength > 0) {
							sb.append("-"); 
							printedLength++; 
						}
						
						// If a conflict occurs at this route
						if (current.getBaseMoment().getRoute().getCost() > 1000000) {
							sb.append("!"); 
							printedLength += 1; 
						}
						String s = current.getRoute().toString();
						sb.append(s); 
						printedLength += s.length(); 
					}
					
					if (!current.isLast()) {
						if(current.getBaseMoment().hasRoute()) {
							//sb.append("."); 
							//printedLength ++; 
						}
						//String s = ConsoleOut.possibleLocationToString(current.getBaseMoment()); 
						//sb.append(s); 
						//printedLength += s.length(); 
						current = current.getNext(); 
					}
					else {
						break; 
					}
				} 
				
				if (x < current.getTimePoint()) {
					if (printedLength >= 8) {	// 8 is tabLength: "\t" 
						printedLength -= 8; 
					} 
					else {
						printedLength = 0; 
						sb.append("\t"); 
					}
				}
			}
			
			
			
			*/ 
			
			
			
		return sb.toString();
	}
	
	private static String trainDataToString(TrpMoment m, ArrayList<Integer> xAxis, TrainData data) throws Exception {
		StringBuilder sb = new StringBuilder(); 
		TrpMoment current 	= m; 
		int printedLength = 0; 
		for (int x : xAxis) {
			if (current.getTimePoint() == x) {
				String s = data.trainData(current); 
				
				// If we did not yet empty the printedLength yet
				if (printedLength > 0 && s.length() > 0) {
					sb.append("-"); 
					printedLength++; 
				}
				
				printedLength += s.length(); 
				sb.append(s); 					
				
				if (!current.isLast()) {
					current = current.getNext(); 
				} else {
					break; 
				}
			} 
			if (x < current.getTimePoint()) {
				if (printedLength >= 8) {	// 8 is tabLength: "\t" 
					printedLength -= 8; 
				} 
				else {
					printedLength = 0; 
					sb.append("\t"); 
				}
			} 
		}
		return sb.toString();
		
	}
	
	private interface TrainData {
		String trainData(TrpMoment m) throws Exception; 
	}
	
	public static String possibleLocationToString(Moment m) {
		String s = null; 
		if (m.hasRoute()) {
			try {
				s = m.getLocation().getArrivingSide().toString();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		else if (1 > m.getOptions().size()) {
			s = "!"; 
		} 
		else {
			if (1 == m.getOptions().size()) {
				Side s1 = m.getOptions().get(0);
				Track t;
				try {
					t = s1.getTrack();
					s = t.getName(); 
				} catch (Exception e) {
					if (e.getMessage().equals("No track was added")) {
						s = "0";
					}
					else {
						e.printStackTrace();
					}
				} 
			} 
			else {
				s = "?"; 
			}
		}
		return s;
	}

}
