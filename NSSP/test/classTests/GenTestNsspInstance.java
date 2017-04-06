package classTests;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import shuntingYard.ShuntingYard;

public class GenTestNsspInstance {
	
	public static NsspInstance gen1() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard1(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t0 = new Train("t0", 4); 
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 2); 
		Train t3 = new Train("t3", 4); 
		Train t4 = new Train("t4", 2); 
		Train t5 = new Train("t5", 2); 
		Train t6 = new Train("t6", 3); 
		
		/*
		 * 		t1:	a		?		out
		 * 		t2:		?		out
		 * 			___________________
		 * 			0	1	2	3	4	
		 */

		tusp.addNextMoment(t0, yard.getParkTracks());
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());
		tusp.addNextMoment(t3, yard.getParkTracks());
		tusp.addNextMoment(t4, yard.getParkTracks());
		tusp.addNextMoment(t5, yard.getParkTracks());
		tusp.addNextMoment(t6, yard.getParkTracks());
//		tusp.addNextMoment(t2);
//		tusp.addNextMoment(t4);
//		tusp.addNextMoment(t5);
//		tusp.addNextMoment(t1);
//		tusp.addNextMoment(t6);
//		tusp.addNextMoment(t3);
//		tusp.addNextMoment(t0);
		
		return tusp; 
	}
	
	public static NsspInstance gen2() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard1(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t0 = new Train("t0", 4); 
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 2); 
		Train t3 = new Train("t3", 4); 
		Train t4 = new Train("t4", 2); 
		Train t5 = new Train("t5", 2); 
		Train t6 = new Train("t6", 3); 
		
		/*
		 * 		t0:	?													out
		 * 		t1:		?									out
		 * 		t2:			?					out
		 * 		t3:				?									out
		 * 		t4:					?				out
		 * 		t5:						?				out
		 * 		t6:							?					out
		 * 			________________________________________________________
		 * 			0	1	2	3	4	5	6	7	8	9	10	11	12	13	
		 */

		tusp.addNextMoment(t0, yard.getParkTracks());
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());
		tusp.addNextMoment(t3, yard.getParkTracks());
		tusp.addNextMoment(t4, yard.getParkTracks());
		tusp.addNextMoment(t5, yard.getParkTracks());
		tusp.addNextMoment(t6, yard.getParkTracks());
		tusp.addNextMoment(t2);
		tusp.addNextMoment(t4);
		tusp.addNextMoment(t5);
		tusp.addNextMoment(t1);
		tusp.addNextMoment(t6);
		tusp.addNextMoment(t3);
		tusp.addNextMoment(t0);
		
		return tusp; 
	}

}
