package classTests;

import mspInstance.Train;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import shuntingYard.ShuntingYard;

public class GenTestTlpInstance {
	
	public static NsspInstance gen1() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	?		out
		 * 		t2:		?		out
		 * 			________________
		 * 			0	1	2	3		
		 */

		/* 
		 * 		7:	________
		 * 			|__t1__|
		 * 		4:		________
		 * 				|__t2__|
		 * 		0:	________________
		 * 			0	1	2	3		
		 */
		
		

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t2, yard.getParkTracks());
		tusp.addMoment(2, t1);
		tusp.addMoment(3, t2);
		
		return tusp; 
	}

	public static NsspInstance gen2() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard11(); 
		NsspInstance nssp = new NsspInstance(yard); 

		Train t1 = new Train("t1", 1); 
		Train t2 = new Train("t2", 2); 
		Train t3 = new Train("t3", 1); 
		Train t4 = new Train("t4", 1); 
		Train t5 = new Train("t5", 2); 
		/*
		 * This else statement is a pain in the ass, a route of length 1
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
		 * 	4:	|	|		________________
		 * 		|	|		|  ||____t1____|
		 * 	3:	|	|		|t2|		________________
		 * 		|	|		|__|		|______t3______|
		 * 	2:	t1	t2	________________			____
		 * 		|	|	|______t3______|			|  |
		 *	1:	|	|				____________	|t5|
		 * 		|	|				|____t4____|	|__|
		 * 	0:	____________________________________________
		 * 		0	1	2	3	4	5	6	7	8	9	10		
		 */
		
		
		/*
		 * 	t1:	c				b			out
		 * 	t2:		c		out	
		 * 	t3:			b				b				out
		 * 	t4:						b			out
		 * 	t5:										a
		 * 		____________________________________________
		 * 		0	1	2	3	4	5	6	7	8	9	10		
		 */

		nssp.addMoment(0, 	t1, yard.getTrack("c"));
		nssp.addMoment(1, 	t2, yard.getTrack("c"));
		nssp.addMoment(2, 	t3, yard.getTrack("b"));
		nssp.addMoment(3, 	t2);
		nssp.addMoment(4, 	t1, yard.getTrack("b"));
		nssp.addMoment(5, 	t4, yard.getTrack("b"));
		nssp.addMoment(6, 	t3, yard.getTrack("b"));
		nssp.addMoment(7, 	t1);
		nssp.addMoment(8, 	t4);
		nssp.addMoment(9, 	t5, yard.getTrack("a"));
		nssp.addMoment(10, 	t3);
		
		return nssp; 
	}
	
	
	public static NsspInstance gen3() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		
		/*
		 * 		t1:	?		out
		 * 			________________
		 * 			0	1	2	3		
		 */

		/* 
		 * 		3:	____
		 * 			|t1|
		 * 		0:	_________
		 * 			0	1	
		 */
		
		

		tusp.addMoment(0, t1, yard.getTrack("a"));
		tusp.addMoment(1, t1);
		
		return tusp; 
	}
	
	public static NsspInstance gen5() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 4); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	?		out
		 * 		t2:		?		out
		 * 			________________
		 * 			0	1	2	3		
		 */

		/* 
		 * 		7:	________
		 * 			|__t1__|
		 * 		4:		________
		 * 				|__t2__|
		 * 		0:	________________
		 * 			0	1	2	3		
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t2, yard.getParkTracks());
		tusp.addMoment(2, t1);
		tusp.addMoment(3, t2);
		
		return tusp; 
	}


	public static NsspInstance gen6() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard11(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t1 = new Train("t1", 1); 
		Train t2 = new Train("t2", 1); 
		Train t3 = new Train("t3", 2); 
		Train t4 = new Train("t4", 1); 
		Train t5 = new Train("t5", 1); 
		Train t6 = new Train("t6", 3); 
		
		/*
		 * 		t1:		?		out
		 * 		t2:	?		out
		 * 			________________
		 * 			0	1	2	3		
		 */

		/* 
		 * 		5:								____________
		 * 										|____t5____|
		 * 		4:	________		________________
		 * 			|__t1__|		|______t4______|	
		 * 		3:		________________				____________
		 * 				|______t2______|				|		   |
		 * 		2:				____________			|		   |
		 * 						|____t3____|			|____t6____|
		 * 		0:	____________________________________________
		 * 			2	3	4	5	6	7	8	9	10		
		 */

		tusp.addMoment(0, t5, yard.getTrack("c"));
		tusp.addMoment(1, t4, yard.getTrack("c"));

		tusp.addMoment(2, t1, yard.getTrack("b"));
		tusp.addMoment(3, t2, yard.getTrack("b"));
		tusp.addMoment(4, t1);
		tusp.addMoment(5, t3, yard.getTrack("b"));
		
		tusp.addMoment(6, t4, yard.getTrack("b"));
		tusp.addMoment(7, t2);
		tusp.addMoment(8, t3);
		tusp.addMoment(9, t5, yard.getTrack("b"));
		tusp.addMoment(10, t4);
		
		tusp.addMoment(11, t6, yard.getTrack("b"));
		tusp.addMoment(12, t5);
		tusp.addMoment(13, t6);
		
		return tusp; 
	}
	

	public static NsspInstance gen7() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard2(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 3); 
		Train t2 = new Train("t2", 4); 
		
		/*
		 * 		t1:	?			out
		 * 		t2:		?	out
		 * 			________________
		 * 			0	1	2	3		
		 */

		/* 
		 * 		7:	____________
		 * 			|____t1____|
		 * 		4:		____
		 * 				|t2|
		 * 		0:	________________
		 * 			0	1	2	3		
		 */

		tusp.addMoment(0, t1, yard.getParkTracks());
		tusp.addMoment(1, t2, yard.getParkTracks());
		tusp.addMoment(2, t2);
		tusp.addMoment(3, t1);
		
		return tusp; 
	}

	public static NsspInstance gen8() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard12(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 2); 
		Train t2 = new Train("t2", 3); 
		Train t3 = new Train("t3", 2); 
		
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());
		tusp.addNextMoment(t3, yard.getParkTracks());
		tusp.addNextMoment(t1);
		tusp.addNextMoment(t2);
		tusp.addNextMoment(t3);

		return tusp; 
	}

	public static NsspInstance gen9() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard13(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t1 = new Train("t1", 1); 
		Train t2 = new Train("t2", 1); 
		Train t3 = new Train("t3", 2); 
		
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());
		tusp.addNextMoment(t3, yard.getParkTracks());
		tusp.addNextMoment(t1);
		tusp.addNextMoment(t2);
		tusp.addNextMoment(t3);

		return tusp; 
	}

	public static NsspInstance gen10() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard14(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t0 = new Train("t0", 1); 
		Train t1 = new Train("t1", 2); 
		Train t2 = new Train("t2", 2); 
		Train t3 = new Train("t3", 3); 
		
		tusp.addNextMoment(t0, yard.getParkTracks());
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());
		tusp.addNextMoment(t3, yard.getParkTracks());
		tusp.addNextMoment(t0);
		tusp.addNextMoment(t1);
		tusp.addNextMoment(t2);
		tusp.addNextMoment(t3);

		return tusp; 
	}

	public static NsspInstance gen11() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard14(); 
		NsspInstance tusp = new NsspInstance(yard); 
		
		Train t0 = new Train("t0", 5); 
		
		tusp.addNextMoment(t0, yard.getParkTracks());
		tusp.addNextMoment(t0);

		return tusp; 
	}

	public static NsspInstance gen12() throws Exception {
		ShuntingYard yard = GenTestShuntingYard.genYard1(); 
		NsspInstance tusp = new NsspInstance(yard); 

		Train t0 = new Train("t0", 4); 
		Train t1 = new Train("t1", 2); 
		Train t2 = new Train("t2", 3); 

		tusp.addNextMoment(t0, yard.getParkTracks());
		tusp.addNextMoment(t1, yard.getParkTracks());
		tusp.addNextMoment(t2, yard.getParkTracks());

		return tusp; 
	}
}
