package classTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import nedtrainSchedulingShuntingProblem.AlgSettings.Init;
import nedtrainSchedulingShuntingProblem.AlgSettings.Prio;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.NsspSolver;

import org.junit.Test;

import shuntingYard.Location;

public class TestNsspSolver {
	
	private static final boolean DEBUG 				= false; 
	private static final boolean DEBUG_ALG_STEPS 	= false; 

	@Test
	public void test1() {
		NsspSolver ns = null; 
		try {
		
		if(DEBUG) System.out.println("========================test1========================");
		NsspInstance instance 			= GenTestTrpInstance.gen1(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 

		ns = new NsspSolver(instance); 
		ns.setSubProblemInit(Init.trpProposes); 
			
		ns.createInitialSchedule(); 
		boolean b = ns.solve(); 
		assertTrue(b); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		Location expected 	= new Location(instance.getTrack("c").getSideB()); 
		Location m1			= instance.getMoment(1).getLocation(); 
		assertEquals(expected, m1);
		
		expected 		= new Location(instance.getTrack("d").getSideB()); 
		m1				= instance.getMoment(2).getLocation(); 
		assertEquals(expected, m1);
		} catch (Exception e) {
			if (DEBUG) System.out.println(ns.toString());
			e.printStackTrace();
			fail(); 
		}
	}
	
	@Test
	public void test2() {
		try {
		
		if(DEBUG) System.out.println("========================test2========================");
		NsspInstance instance 			= GenTestTlpInstance.gen1(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		NsspSolver ns = new NsspSolver(instance); 
		ns.createInitialSchedule(); 
		boolean solved = ns.solve(); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		assertTrue(solved);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	@Test
	public void test3() {
		try {
		if(DEBUG) System.out.println("========================test3========================");
		NsspInstance instance 			= GenTestNsspInstance.gen1(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		NsspSolver ns = new NsspSolver(instance); 
		ns.createInitialSchedule(); 
		boolean solved = ns.solve(); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		assertTrue(solved);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	/**
	 * Test if the trp schedule cost is updated correctly.
	 */
	@Test
	public void test4() {
		try {
		if(DEBUG) System.out.println("========================test4========================");
		NsspInstance instance 			= GenTestTlpInstance.gen1(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		NsspSolver ns = new NsspSolver(instance); 
		ns.createInitialSchedule(); 
		if (DEBUG) System.out.println(ns.toString());
		
		int trpScheduleCost = ns.getTrpScheduleCost(); 
		
		assertEquals(4, trpScheduleCost);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	/**
	 * Test if the trp schedule cost is updated correctly.
	 */
	@Test
	public void test5() {
		try {
		if(DEBUG) System.out.println("========================test5========================");
		NsspInstance instance 			= GenTestNsspInstance.gen2(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		NsspSolver ns = new NsspSolver(instance); 
		ns.setSubProblemInit(Init.trpProposes);
		ns.setSubProblemPrio(Prio.trpBeforeTlp);
		ns.setSearch(Search.tabu); 
		ns.createInitialSchedule(); 
		boolean solved = ns.solve(); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		assertTrue(solved);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
}
