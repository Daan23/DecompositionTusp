package classTests;

import java.util.ArrayList;
import java.util.Collection;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import shuntingYard.ShuntingYard;
import shuntingYard.Track;

public class GenTestTrpInstance {
	
	public static NsspInstance gen1() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard6(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	a		?		out
		 * 		t2:		?		out
		 * 			___________________
		 * 			0	1	2	3	4	
		 */

		tusp.addMoment(0, t1, yard.getTrack("a"));
		tusp.addMoment(1, t2, yard.getParkTracks());
		tusp.addMoment(2, t1, yard.getParkTracks());
		tusp.addMoment(3, t2);
		tusp.addMoment(4, t1);
		
		return tusp; 
	}

	public static NsspInstance gen2() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard5(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	?	out
		 * 			________
		 * 			0	1	
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t1);
		
		return tusp; 
	}

	public static NsspInstance gen3() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard6(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	?	out
		 * 			________
		 * 			0	1	
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t1);
		
		return tusp; 
	}

	public static NsspInstance gen4() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard6(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	a		?		out
		 * 		t2:		a		out
		 * 			___________________
		 * 			0	1	2	3	4	
		 */

		tusp.addMoment(0, t1, yard.getTrack("a"));
		tusp.addMoment(1, t2, yard.getTrack("a"));
		tusp.addMoment(2, t1, yard.getTrack("b"));
		tusp.addMoment(3, t2);
		tusp.addMoment(4, t1);
		
		return tusp; 
	}
	
	public static NsspInstance gen5() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	a		
		 * 		t2:		out	
		 * 			________
		 * 			0	1		
		 */

		tusp.addMoment(0, t1, yard.getTrack("a"));
		tusp.addMoment(1, t2);
		
		return tusp; 
	}

	public static NsspInstance gen6() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	out		
		 * 			____
		 * 			0		
		 */

		tusp.addMoment(0, t1);
		
		return tusp; 
	}

	public static NsspInstance gen7() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard1(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t1 = new Train("t0", 1); 
		Train t2 = new Train("t1", 1); 
		Train t3 = new Train("t2", 1); 
		Train t4 = new Train("t3", 1); 
		
		/*
		 * 		t1:	b				out
		 * 		t2:		?						out
		 * 		t3:			?				out	
		 * 		t4:				?		out			
		 * 			________________________________	
		 * 			0	1	2	3	4	5	6	7	
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t2, yard.getParkTracks());
		tusp.addMoment(2, t3, yard.getParkTracks());
		tusp.addMoment(3, t4, yard.getParkTracks());
		tusp.addMoment(4, t1);
		tusp.addMoment(5, t4);
		tusp.addMoment(6, t3);
		tusp.addMoment(7, t2);
		
		tusp.initMomentRoute(0, yard.getEntrance(), yard.getTrack("b").getSideA(), Search.tabu);
		
		return tusp; 
	}

	public static NsspInstance gen8() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard7(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t1 = new Train("t1", 1); 
		Train t2 = new Train("t2", 1); 
		Train t3 = new Train("t3", 1); 
		
		/*
		 * 		t1:	bd				
		 * 		t2:		c	out					
		 * 		t3:				a				
		 * 		t4:							
		 * 			________________	
		 * 			0	1	2	3	
		 */

		Collection<Track> c = new ArrayList<>(); 
		c.add(yard.getTrack("d")); 
		c.add(yard.getTrack("b")); 
		
		tusp.addMoment(0, t1, c);
		tusp.addMoment(1, t2, yard.getTrack("c"));
		tusp.addMoment(2, t2);
		tusp.addMoment(3, t3, yard.getTrack("a"));
		
		tusp.initMomentRoute(0, yard.getEntrance(), yard.getTrack("b").getSideA(), Search.tabu); 
		
		return tusp; 
	}

	public static NsspInstance gen9() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard8(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	?	out
		 * 			________
		 * 			0	1	
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t1);
		
		return tusp; 
	}

	public static NsspInstance gen10() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard9(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	?	out
		 * 			________
		 * 			0	1	
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t1);
		
		return tusp; 
	}
	
	
	public static NsspInstance gen11() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard10();
		NsspInstance tusp = new NsspInstance (yard);

		Train t0 = new Train("t0", 1); 
		Train t1 = new Train("t1", 1); 
		Train t2 = new Train("t2", 1); 
		Train t3 = new Train("t3", 1); 
		Train t4 = new Train("t4", 1); 
			
		
		/*
		 * 		t0:	?									out
		 * 		t1:		?						out
		 * 		t2:			?			out			
		 * 		t3:				?					out
		 * 		t4:					?		out		
		 * 			________________________________________
		 * 			0	1	2	3	4	5	6	7	8	9
		 */
		
		tusp.addMoment(0, t0, yard.getParkTracks());
		tusp.addMoment(1, t1, yard.getParkTracks());
		tusp.addMoment(2, t2, yard.getParkTracks());
		tusp.addMoment(3, t3, yard.getParkTracks());
		tusp.addMoment(4, t4, yard.getParkTracks());
		tusp.addMoment(5, t2);
		tusp.addMoment(6, t4);
		tusp.addMoment(7, t1);
		tusp.addMoment(8, t3);
		tusp.addMoment(9, t0);
		
		return tusp;
	}

	public static NsspInstance gen12() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard11(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 2); 
		
		/*
		 * 		t1:	c	b	out
		 * 			____________
		 * 			0	1	2	
		 */

		tusp.addMoment(0, t1, yard.getTrack("c"));
		tusp.addMoment(1, t1, yard.getTrack("b"));
		tusp.addMoment(2, t1);
		
		return tusp; 
	}
}
