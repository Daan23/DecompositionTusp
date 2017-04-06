package classTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import shuntingYard.ShuntingYard;
import shuntingYard.Track;

public class TestShuntingYard {
	
	ShuntingYard yard; 
	Track a, b, c, d, in, out; 
	
	/*
	 * 						a		b	
	 * 					 ---------------
	 * 			in		/	c	\	d	\	out
	 * 			-->	-----------------------	-->
	 */

	public void createShuntingYard() {
		try {
			this.yard = GenTestShuntingYard.genYard1();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 

		this.a 		= yard.getTrack("a"); 
		this.b 		= yard.getTrack("b"); 
		this.c 		= yard.getTrack("c"); 
		this.d 		= yard.getTrack("d"); 
		this.in		= yard.getTrack("in");
		this.out 	= yard.getTrack("out");
	}
	
	@Test
	public void testAddingConnection() {
		this.createShuntingYard();
		
//		assertTrue(this.a.getSideB().getConnectedSides().contains(this.b.getSideA()));
//		assertTrue(this.b.getSideA().getConnectedSides().contains(this.a.getSideB()));
	}
	
	@Test
	public void testNotAddedConnection() {
		this.createShuntingYard();
		
		assertFalse(this.a.getSideA().getConnectedSides().contains(this.b.getSideA()));
		assertFalse(this.a.getSideB().getConnectedSides().contains(this.b.getSideB()));
	}
	
	@Test
	public void testGetOtherSide() {
	try{
		this.createShuntingYard();
		assertEquals(a.getSideB(), a.getSideA().getOtherSide());
		assertEquals(c.getSideA(), c.getSideA().getOtherSide().getOtherSide());
		assertNotEquals(d.getSideA(), d.getSideA().getOtherSide());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
		
	}

}
