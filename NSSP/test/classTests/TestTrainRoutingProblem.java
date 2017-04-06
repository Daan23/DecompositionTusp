package classTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.NsspInstance;

import org.junit.Test;

import shuntingYard.Location;
import shuntingYard.Route;
import trainRoutingProblem.RouteConflict;
import trainRoutingProblem.TrpSolver;
import trainRoutingProblem.TrpMoment;

public class TestTrainRoutingProblem {
	
	public boolean DEBUG = false; 

	@Test
	public void test() {
		try {
		if(DEBUG) System.out.println("========================test1========================");
		NsspInstance ti 			= GenTestTrpInstance.gen2(); 
		ti.setDebugging(this.DEBUG);
		
		TrpMoment.start(); 
		TrpMoment.end(); 
		
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Location expected 	= new Location(ti.getTrack("c").getSideA()); 
		Location m1			= ti.getMoment(0).getLocation(); 
		Route r				= ti.getMoment(0).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(1, r.getCost());
		
		expected		 	= new Location(ti.getExit()); 
		m1					= ti.getMoment(1).getLocation(); 
		r					= ti.getMoment(1).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(1, r.getCost());
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}

	@Test
	public void test2ShuntCloseToExit() {
		try {
		if(DEBUG) System.out.println("========================test2========================");
		NsspInstance ti 			= GenTestTrpInstance.gen3(); 
		ti.setDebugging(this.DEBUG);
		
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Location expected 	= new Location(ti.getTrack("a").getSideA()); 
		Location m1			= ti.getMoment(0).getLocation(); 
		Route r				= ti.getMoment(0).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(2, r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	/**
	 * Test the creation of an initial schedule
	 */
	@Test
	public void test3() {
		try {
		if(DEBUG) System.out.println("========================test3========================");
		NsspInstance ti 			= GenTestTrpInstance.gen1(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Location expected 	= new Location(ti.getTrack("b").getSideA()); 
		Location m1			= ti.getMoment(1).getLocation(); 
		Route r				= ti.getMoment(1).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(1, r.getCost());
		
		expected 		= new Location(ti.getTrack("b").getSideB()); 
		m1				= ti.getMoment(2).getLocation(); 
		r				= ti.getMoment(2).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(1, r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
		
	}

	@Test
	public void test4() {
		try {
		if(DEBUG) System.out.println("========================test4========================");
		NsspInstance ti 			= GenTestTrpInstance.gen1(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		ts.solveTRP(); 
		
		Location expected 	= new Location(ti.getTrack("c").getSideB()); 
		Location m1			= ti.getMoment(1).getLocation(); 
		Route r				= ti.getMoment(1).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(1002, r.getCost());
		
		expected 		= new Location(ti.getTrack("d").getSideB()); 
		m1				= ti.getMoment(2).getLocation(); 
		r				= ti.getMoment(2).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(2, r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
			fail();
	}
	}

	@Test
	public void test5IfAllConflictsAreFound() {
		try {
		
		if(DEBUG) System.out.println("========================test5========================");
		NsspInstance ti 			= GenTestTrpInstance.gen4(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		ArrayList<RouteConflict> conflicts = ts.findConflicts(); 
		assertEquals(2,  conflicts.size());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	@Test
	public void test6() {
		try {
		if(DEBUG) System.out.println("========================test6========================");
		NsspInstance ti 			= GenTestTrpInstance.gen5(); 
		ti.setDebugging(this.DEBUG);
		
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Route r						= ti.getMoment(1).getRoute(); 
		assertEquals((1000000)+(2*1), r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	@Test
	public void test7() {
		try {
		if(DEBUG) System.out.println("========================test7========================");
		NsspInstance ti 			= GenTestTrpInstance.gen6(); 
		ti.setDebugging(this.DEBUG);
		
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Route r						= ti.getMoment(0).getRoute(); 
		assertEquals(2, r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	// @Test
	public void test8() {
		try {
		if(DEBUG) System.out.println("========================test8========================");
		NsspInstance ti 			= GenTestTrpInstance.gen7(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		ts.solveTRP(); 
		
		assertEquals(12, ti.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	// @Test 
	public void test9TakeAlternativeRoute() {
		try {
		if(DEBUG) System.out.println("========================test9========================");
		NsspInstance ti 			= GenTestTrpInstance.gen8(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		ts.solveTRP(); 
		assertTrue(ts.findConflicts().size() == 0); 
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	@Test 
	public void test10TurningFreeOnShunt() {
		try {
		if(DEBUG) System.out.println("========================test10=======================");
		NsspInstance ti 			= GenTestTrpInstance.gen9(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();

		assertEquals(2, ti.getCost());
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	@Test 
	public void test11NotTurningFreeOnShunt() {
		try {
		if(DEBUG) System.out.println("========================test11=======================");
		NsspInstance ti 			= GenTestTrpInstance.gen10(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();

		assertEquals(2, ti.getCost());
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	// @Test 
	public void test12() {
		try {
		if(DEBUG) System.out.println("========================test12========================");
		NsspInstance ti 			= GenTestTrpInstance.gen11(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		ts.solveTRP(); 
		assertEquals(1017, ti.getCost());
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}

	@Test 
	public void test13() {
		try {
		if(DEBUG) System.out.println("========================test13=======================");
		NsspInstance ti 			= GenTestTrpInstance.gen8(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Location expected 	= new Location(ti.getTrack("b").getSideA()); 
		Location m1			= ti.getMoment(0).getLocation(); 
		Route r				= ti.getMoment(0).getRoute(); 
		assertEquals(expected, m1);
		assertEquals(2, r.getCost());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	/**
	 * Test if a train does not make a turn on the track on which it arrives. 
	 * @throws Exception
	 */
	@Test 
	public void test14() {
		try {
		if(DEBUG) System.out.println("========================test14=======================");
		NsspInstance ti 			= GenTestTrpInstance.gen12(); 
		ti.setDebugging(this.DEBUG);
		TrpSolver ts		= new TrpSolver(ti); 
		ts.createInitialSchedule();
		
		Route r				= ti.getMoment(1).getRoute(); 
		int routeLength		= r.getTracks().size(); 
		assertEquals(2, routeLength);
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
}
