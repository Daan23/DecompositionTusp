package classTests;

import shuntingYard.ShuntingYard;
import shuntingYard.Track;

public class GenTestShuntingYard {
	
	static ShuntingYard genYard1() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c, d; 
		
		/*
		 * 						a		b	
		 * 					 ---------------
		 * 			in		/	c	\	d	\	out
		 * 			-->	-----------------------	-->
		 */

		a 	= yard.addParkTrack("a", 	6); 
		b 	= yard.addParkTrack("b", 	5); 
		c 	= yard.addParkTrack("c", 	7); 
		d 	= yard.addParkTrack("d", 	4); 

		yard.connectLeftToEntrance(a); 
		yard.connectLeftToEntrance(c); 
		yard.connectToExit(b); 
		yard.connectToExit(d); 
		
		a. getSideB().connectTo(b.	getSideA());
		a. getSideB().connectTo(d.	getSideA());
		c. getSideB().connectTo(d.	getSideA());
		
		return yard; 
	}
	
	static ShuntingYard genYard2() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a; 
		
		/*
		 * 			in		a	out
		 * 			-->	-------	-->
		 */

		a = yard.addParkTrack("a", 7); 
		yard.connectLeftToEntrance(a);
		yard.connectToExit(a);
		
		return yard; 
	}
	
	static ShuntingYard genYard3() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b; 
		
		/*
		 * 			in		a		b		out
		 * 			-->	-------------------	-->
		 */

		a = yard.addParkTrack("a", 1); 
		b = yard.addParkTrack("b", 1); 
		
		yard.connectLeftToEntrance(a);
		yard.connectToExit(b);
		a.connectLeftToRight(b);
		
		return yard; 
	}

	static ShuntingYard genYard4() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, c, b; 
		
		/*
		 * 							a	
		 * 						 -------
		 * 			in		b	/	c	
		 * 			-->	----------------
		 */

		a 	= yard.addParkTrack("a", 	1); 
		c 	= yard.addParkTrack("c", 	1); 
		b 	= yard.addParkTrack("b", 	1);
		
		yard.connectLeftToEntrance(b);
		b.connectLeftToRight(a);
		b.connectLeftToRight(c);
		
		return yard; 
	}
	
	static ShuntingYard genYard5() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c; 
				
		/*
		 * 						a		b	
		 * 					 ---------------
		 * 			in		/		c		\	out
		 * 			-->	-----------------------	-->
		 */

		a 	= yard.addParkTrack("a", 	1); 
		b 	= yard.addParkTrack("b", 	1); 
		c 	= yard.addParkTrack("c", 	1); 
		
		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(c);
		yard.connectToExit(b);
		yard.connectToExit(c);
		
		a.connectLeftToRight(b);
		
		return yard; 
	}

	public static ShuntingYard genYard6() throws Exception {
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c, d; 
				
		/*
		 * 				d
		 * 			|------.
		 * 				c	\		b		a	
		 * 			|--------.-.--------.------.
		 * 			in		  /					\	out
		 * 			-->		 .					 .	-->
		 */

		a 	= yard.addParkTrack("a", 	3); 
		b 	= yard.addParkTrack("b", 	4); 
		c 	= yard.addParkTrack("c", 	7); 
		d 	= yard.addParkTrack("d", 	8); 
		
		yard.connectLeftToEntrance(b);
		yard.connectToExit(a);
		
		d.connectLeftToRight(b);
		c.connectLeftToRight(b);
		b.connectLeftToRight(a);
		
		return yard; 
	}
	
	public static void main(String[] args) throws Exception {
		
		ShuntingYard yard1 = GenTestShuntingYard.genYard2(); 
		System.out.println(yard1.toString()); 
		
	}

	public static ShuntingYard genYard8() throws Exception {
		
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c; 
				
		/*
		 * 							|c
		 * 							|
		 * 					 ------/ \------
		 * 					/	a		b	\
		 * 				   /-----------------\	
		 * 			in	  /				 	  \	out
		 * 			-->							-->
		 */

		a 	= yard.addParkTrack("a", 	1); 
		b 	= yard.addParkTrack("b", 	1); 
		c 	= yard.addParkTrack("c", 	1); 

		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(c);
		
		yard.connectToExit(b);
		yard.connectLeftToExit(c); 

		a.connectLeftToRight(b);
		
		return yard;
	}

	public static ShuntingYard genYard9() throws Exception {
		
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c; 
				
		/*
		 * 							|
		 * 							|c
		 * 							|
		 * 						b  / \
		 * 					 -----/   \-----
		 * 					/		a		\
		 * 				   -------------------	
		 * 			in	  /				 	  \	out
		 * 			-->							-->
		 */

		a 	= yard.addParkTrack("a", 	1); 
		b 	= yard.addParkTrack("b", 	1); 
		c 	= yard.addParkTrack("c", 	1); 

		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(b);
		
		yard.connectToExit(a);
		yard.connectLeftToExit(c); 

		b.connectLeftToRight(c);
		
		return yard;
	}

	public static ShuntingYard genYard7() throws Exception {
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c, d; 
				
		/*
		 * 							d	
		 * 					 ---------------
		 * 					/	c		b	\	a
		 * 				   --------------------------	
		 * 			in	  /					  \	out
		 * 			-->							-->
		 */

		a 	= yard.addParkTrack("a", 	1); 
		b 	= yard.addParkTrack("b", 	1); 
		c 	= yard.addParkTrack("c", 	1); 
		d 	= yard.addParkTrack("d", 	1); 
		
		yard.connectLeftToEntrance(d);
		yard.connectLeftToEntrance(c);
		yard.connectToExit(b);
		yard.connectToExit(d);

		c.connectLeftToRight(b);
		b.connectLeftToRight(a);
		d.connectLeftToRight(a);
		
		return yard; 
	}
	
	
	
	/**
	 * This is Maaike's very first shunting yard
	 * @return
	 */
	public static ShuntingYard genYard10() throws Exception {
		
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c, d, e;
		
		/*					 
		 * 						a		
		 * 				   -----------	
		 * 			in	  /		b		\		c		out
		 * 			-->	--------------.--.-------------	-->
		 *				  		d	   \		e    /
		 *				   -------------.------------
		 */
		
		a 	= yard.addParkTrack("a", 	1); 
		b 	= yard.addParkTrack("b", 	1); 
		c 	= yard.addParkTrack("c", 	1); 
		d 	= yard.addParkTrack("d", 	1); 
		e 	= yard.addParkTrack("e", 	1);
		
		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(b);
		yard.connectToExit(c);
		yard.connectToExit(e);
		
		a.connectLeftToRight(c);
		b.connectLeftToRight(c);
		b.connectLeftToRight(e);
		d.connectLeftToRight(e);
		
		return yard;
	}
	
	public static ShuntingYard genYard11() throws Exception {
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c; 
				
		/*
		 * 				a		   b		c	
		 * 			-----------.--------.------.
		 * 			in		  /			\	out
		 * 			-->		 .			 .	-->
		 */

		a 	= yard.addParkTrack("a", 	2); 
		b 	= yard.addParkTrack("b", 	4); 
		c 	= yard.addParkTrack("c", 	2); 
		
		yard.connectLeftToEntrance(b);
		yard.connectToExit(b);
		
		a.connectLeftToRight(b);
		b.connectLeftToRight(c);
		
		return yard; 
	}
	
	static ShuntingYard genYard12() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b; 
				
		/*
		 * 							a	
		 * 					 ---------------
		 * 			in		/		b		\	out
		 * 			-->	-----------------------	-->
		 */

		a 	= yard.addParkTrack("a", 	3); 
		b 	= yard.addParkTrack("b", 	4); 
		
		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(b);
		yard.connectToExit(a);
		yard.connectToExit(b);
		
		return yard; 
	}
	
	static ShuntingYard genYard13() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b; 
				
		/*
		 * 							a	
		 * 					 ---------------
		 * 			in		/		b		\	out
		 * 			-->	-----------------------	-->
		 */

		a 	= yard.addParkTrack("a", 	2); 
		b 	= yard.addParkTrack("b", 	4); 
		
		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(b);
		yard.connectToExit(a);
		yard.connectToExit(b);
		
		return yard; 
	}
	
	static ShuntingYard genYard14() throws Exception{
		ShuntingYard yard = new ShuntingYard(); 
		Track a, b, c; 
				
		/*
		 * 							a	
		 * 					   -------------
		 * 					  /		b		\
		 * 					 -----------------
		 * 			in		/		c		  \		out
		 * 			-->	------------------------	-->
		 */

		a 	= yard.addParkTrack("a", 	2); 
		b 	= yard.addParkTrack("b", 	4); 
		c 	= yard.addParkTrack("c", 	6); 
		
		yard.connectLeftToEntrance(a);
		yard.connectLeftToEntrance(b);
		yard.connectLeftToEntrance(c);
		yard.connectToExit(a);
		yard.connectToExit(b);
		yard.connectToExit(c);
		
		return yard; 
	}
	
}
