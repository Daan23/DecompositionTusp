package classTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.Moment;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nedtrainSchedulingShuntingProblem.NsspSolver;

import org.junit.Test;

import shuntingYard.Side;
import solutionSpace.Solution;
import solutionSpace.SolutionSet;
import solutionSpace.SpaceTrackSolution;
import trainLocationProblem.LocationConflict;
import trainLocationProblem.TlpInstancePrinter;
import trainLocationProblem.TlpMoment;
import trainLocationProblem.TlpSolver;
import trainRoutingProblem.TrpSolver;

public class TestTrainLocationProblem {
	
	private final boolean DEBUG = false;  
	private static final Search search = Search.normal; 
	
	@Test
	public void test1() {
		try {
		if (DEBUG) System.out.println("========================test1========================");
		NsspInstance instance 			= GenTestTlpInstance.gen3(); 
		TlpSolver solver 	= new TlpSolver(instance); 
		
		solver.createInitialSchedule();

		TlpMoment m0 = solver.getMoment(instance.getMoment(0));
		assertEquals(0, m0.getCoordinate());
		assertEquals(instance.getTrack("a"), m0.getTrack());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	@Test
	public void test2() {
		try {
		if (DEBUG) System.out.println("========================test2========================");
		NsspInstance instance 			= GenTestTlpInstance.gen1(); 
		instance.setDebugging(this.DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 

		if (DEBUG) System.out.println(solver.toString());
		solver.createInitialSchedule();
		if (DEBUG) System.out.println(solver.toString());
//		System.out.println(TlpInstancePrinter.TrackConsumers(solver.getInstance()));

		TlpMoment m0 = solver.getMoment(instance.getMoment(0));
		TlpMoment m1 = solver.getMoment(instance.getMoment(1));
		assertEquals(4, m0.getCoordinate());
		assertEquals(0, m1.getCoordinate());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	

	@Test
	public void test3NoConflicts() {
		try {
		if (DEBUG) System.out.println("========================test3========================");
		NsspInstance instance 			= GenTestTlpInstance.gen1(); 
		TlpSolver solver 	= new TlpSolver(instance); 
		solver.createInitialSchedule();
		solver.findConflicts(); 
		ArrayList<LocationConflict> l 	= solver.getConflicts(); 

		if (DEBUG) System.out.println(solver.toString());
		
		
		if (DEBUG) System.out.println(solver.toString());
		if (DEBUG) System.out.println(l.toString());
		assertEquals(0, l.size());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	@Test
	public void test4OneConflict() {
		try {
		if (DEBUG) System.out.println("========================test4========================");
		NsspInstance instance 			= GenTestTlpInstance.gen5(); 
		TlpSolver solver 	= new TlpSolver(instance); 
		solver.createInitialSchedule();
		solver.findConflicts(); 
		ArrayList<LocationConflict> l 	= solver.getConflicts(); 

		assertEquals(1, l.size());
		
		LocationConflict c = l.get(0); 

		assertEquals(instance.getMoment(0), c.get(0).getObservedMoment());
		assertEquals(instance.getMoment(1), c.get(1).getObservedMoment());
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}

	

	@Test
	public void test5LargeConflict() {
		try {
		if (DEBUG) System.out.println("========================test5========================");
		NsspInstance instance 			= GenTestTlpInstance.gen6(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		NsspSolver s					= new NsspSolver(instance); 		// The initial schedule is created here. 

		s.createInitialSchedule(); 
		
		solver.findConflicts(); 
		ArrayList<LocationConflict> l 	= solver.getConflicts(); 
		
		if (DEBUG) System.out.println(solver.toString());
		if (DEBUG) System.out.println(l.toString());
		
		assertEquals(1, l.size());
		
		// check if moments located outside of the track consume space. 
		assertEquals(1000000 - 19, solver.getProblemStateCost());
		
		for (LocationConflict c : l) {
			switch (c.get(0).getTimePoint()) {
				case 3: 
					assertEquals(instance.getMoment(7), c.get(1).getObservedMoment());
					assertEquals(instance.getMoment(6), c.get(2).getObservedMoment());
					break; 
				case 5:
					assertEquals(instance.getMoment(8), c.get(1).getObservedMoment());
					assertEquals(instance.getMoment(6), c.get(2).getObservedMoment());
					break; 
				case 6:
					assertEquals(instance.getMoment(10), c.get(1).getObservedMoment());
					assertEquals(instance.getMoment(9), c.get(2).getObservedMoment());
					break; 
				case 9:
					assertEquals(instance.getMoment(6), c.get(1).getObservedMoment());
					assertEquals(instance.getMoment(3), c.get(2).getObservedMoment());
					assertEquals(instance.getMoment(5), c.get(3).getObservedMoment());
					break; 
				default :
					fail(); 
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}

	//@Test
	public void test6LocationConflictInRoute() {
		try {
		if (DEBUG) System.out.println("========================test6========================");
		NsspInstance instance 			= GenTestTlpInstance.gen7(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		NsspSolver ns 					= new NsspSolver(instance); 		// The initial schedule is created here. 
		ns.createInitialSchedule();
		if (DEBUG) System.out.println(solver.toString());
		
		solver.findConflicts(); 
		ArrayList<LocationConflict> l 	= solver.getConflicts(); 
		if (DEBUG) System.out.println(l.toString());
		
		assertEquals(1, l.size());
		assertEquals(3, l.get(0).getContributingMoments().size()); 
		
		LocationConflict c = l.get(0); 

		assertEquals(instance.getMoment(1), c.get(0).getObservedMoment());
		assertEquals(instance.getMoment(2), c.get(1).getObservedMoment());
		assertEquals(instance.getMoment(0), c.get(2).getObservedMoment());
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	@Test
	public void test7InitialScheduleConsistent() {
		try {
		if (DEBUG) System.out.println("========================test7========================");
		NsspInstance instance 			= GenTestTlpInstance.gen8(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("a").getSideA()); 
		
		solver.createInitialSchedule();

		assertEquals(instance.getShuntingYard().getTrack("b").getSideA(), instance.getMoment(0).getArrivingSide());
		assertEquals(instance.getShuntingYard().getTrack("b").getSideA(), instance.getMoment(1).getArrivingSide());
		assertEquals(instance.getShuntingYard().getTrack("a").getSideA(), instance.getMoment(2).getArrivingSide());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	@Test
	public void test8SmallSolveSteps() {
		try {
		if (DEBUG) System.out.println("========================test8========================");
		NsspInstance instance 			= GenTestTlpInstance.gen8(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 
		
		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("a").getSideA()); 
		
		solver.createInitialSchedule();
		
		ArrayList<LocationConflict> l 	= solver.getConflicts(); 
		
		solver.solve(); 
		if (DEBUG) System.out.println(solver.toString());
		solver.findConflicts(); 
		l = solver.getConflicts(); 
		if (DEBUG) System.out.println(l.toString());

		assertEquals(0, l.size());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 * test if freeRelocations is consistent within the initial schedule. 
	 */
	@Test
	public void test9() {
		try {
		if (DEBUG) System.out.println("========================test9========================");
		NsspInstance instance 			= GenTestTlpInstance.gen8(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 

		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("b").getSideA()); 
		
		solver.createInitialSchedule(); 
		
		Moment m0 		= instance.getMoment(0);
		TlpMoment tm0	= solver.getInstance().getMoment(m0); 
		Side aa			= instance.getShuntingYard().getTrack("a").getSideA();
		assertTrue(tm0.couldBeLocatedForFreeOn(aa)); 
		assertEquals(1, tm0.numberOfFreeRelocations()); 
		
		Moment m1 		= instance.getMoment(1);
		TlpMoment tm1	= solver.getInstance().getMoment(m1); 
		assertTrue(tm1.couldBeLocatedForFreeOn(aa)); 
		assertEquals(1, tm1.numberOfFreeRelocations()); 
		
		Moment m2 		= instance.getMoment(2);
		TlpMoment tm2	= solver.getInstance().getMoment(m2); 
		assertTrue(tm2.couldBeLocatedForFreeOn(aa)); 
		assertEquals(1, tm2.numberOfFreeRelocations()); 

	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 * test if freeRelocations is consistent within the initial schedule. 
	 */
	@Test
	public void test10() {
		try {
		if (DEBUG) System.out.println("========================test10========================");
		NsspInstance instance 			= GenTestTlpInstance.gen8(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 

		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("b").getSideA()); 
		
		solver.createInitialSchedule(); 
		
		Moment m0 		= instance.getMoment(0);
		TlpMoment tm0	= solver.getInstance().getMoment(m0); 
		Side ba			= instance.getShuntingYard().getTrack("b").getSideA();
		assertTrue(tm0.couldBeLocatedForFreeOn(ba)); 
		assertEquals(1, tm0.numberOfFreeRelocations()); 
		
		Moment m1 		= instance.getMoment(1);
		TlpMoment tm1	= solver.getInstance().getMoment(m1); 
		assertEquals(0, tm1.numberOfFreeRelocations()); 
		
		Moment m2 		= instance.getMoment(2);
		TlpMoment tm2	= solver.getInstance().getMoment(m2); 
		assertEquals(0, tm2.numberOfFreeRelocations()); 

	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 *  We need to test the cases below: 
	 *  
	 * 1. new relocation from same side as where moment was removed
	 * 2. new relocation from other side as where moment was removed
	 * 3. minus one relocation from same side as where moment was put
	 * 4. minus one relocation from other side as where moment was put
	 * 5. minus one relocation from putting a moment on a free Relocation
	 * 6. minus two relocation from putting a moment on a free Relocation
	 * 
	 * In test 11 we test cases 1 and 5. 
	 */
	@Test
	public void test11() {
		try {
		if (DEBUG) System.out.println("========================test11========================");
		NsspInstance instance 			= GenTestTlpInstance.gen9(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 

		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.createInitialSchedule();
		
		SolutionSet ss = solver.genSolutions();
		solver.assignCosts(ss);
		Solution s = ss.getBestSolution(); 
		
		// one conflict less, no more free relocations (2 previous and 2 new freeRelocations), one more space consumed
		int expectedCost = (-1 * 1000000) + (0 * -1000) + (1 * -1); 
		
		if (DEBUG) System.out.println(s.toString());
		
		assertEquals(expectedCost, s.getCostTlp());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 *  We need to test the cases below: 
	 *  
	 * 1. new relocation from same side as where moment was removed
	 * 2. new relocation from other side as where moment was removed
	 * 3. minus one relocation from same side as where moment was put
	 * 4. minus one relocation from other side as where moment was put
	 * 5. minus one relocation from putting a moment on a free Relocation
	 * 6. minus two relocation from putting a moment on a free Relocation
	 * 
	 * In test 12 we test cases 1, 2, 6
	 */
	@Test
	public void test12() {
		try {
		if (DEBUG) System.out.println("========================test11========================");
		NsspInstance instance 			= GenTestTlpInstance.gen9(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.createInitialSchedule();
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 
		
		SolutionSet ss = solver.genSolutions();
		solver.assignCosts(ss);
		Solution s = ss.getBestSolution(); 
		if (DEBUG) System.out.println(s.toString());
		((SpaceTrackSolution) s).ignoreOtherRouteCost();
		s.execute(search);
		
		if (DEBUG) System.out.println(solver.toString());
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 

		// one conflict less, no more free relocations (2 previous and 2 new freeRelocations), one more space consumed
		int expectedCost = (-1 * 1000000) + (0 * -1000) + (1 * -1); 
		assertEquals(expectedCost, s.getCostTlp());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 *  We need to test the cases below: 
	 *  
	 * 1. new relocation from same side as where moment was removed
	 * 2. new relocation from other side as where moment was removed
	 * 3. minus one relocation from same side as where moment was put
	 * 4. minus one relocation from other side as where moment was put
	 * 5. minus one relocation from putting a moment on a free Relocation
	 * 6. minus two relocation from putting a moment on a free Relocation
	 * 
	 * In test 13 we test cases 3 and 5
	 */
	@Test
	public void test13() {
		try {
		if (DEBUG) System.out.println("========================test11========================");
		NsspInstance instance 			= GenTestTlpInstance.gen10(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 

		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 
		
		// Remove options
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideA()); 
		
		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(3, instance.getShuntingYard().getTrack("c").getSideA()); 
		
		solver.createInitialSchedule();
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 
		
		SolutionSet ss = solver.genSolutions();
		solver.assignCosts(ss);
		Solution s = ss.getBestSolution(); 
		if (DEBUG) System.out.println(s.toString());
		((SpaceTrackSolution) s).ignoreOtherRouteCost();
		s.execute(search);
		
		if (DEBUG) System.out.println(solver.toString());
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 

		// one conflict less, two less free relocations, one more space consumed
		int expectedCost = (-1 * 1000000) + (-2 * -1000) + (1 * -1); 
		assertEquals(expectedCost, s.getCostTlp());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
	/**
	 *  We need to test the cases below: 
	 *  
	 * 1. new relocation from same side as where moment was removed
	 * 2. new relocation from other side as where moment was removed
	 * 3. minus one relocation from same side as where moment was put
	 * 4. minus one relocation from other side as where moment was put
	 * 5. minus one relocation from putting a moment on a free Relocation
	 * 6. minus two relocation from putting a moment on a free Relocation
	 * 
	 * In test 14 we test cases 3, 4, and 6
	 */
	@Test
	public void test14() {
		try {
		if (DEBUG) System.out.println("========================test14========================");
		AlgSettings.removeBothSidesOfOption = false; 
		NsspInstance instance 			= GenTestTlpInstance.gen10(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// Remove options
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideA()); 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("c").getSideB()); 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("b").getSideB()); 
		
		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("a").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(3, instance.getShuntingYard().getTrack("c").getSideA()); 
		
		solver.createInitialSchedule();
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 
		
		SolutionSet ss = solver.genSolutions();
		solver.assignCosts(ss);
		Solution s = ss.getBestSolution(); 
		if (DEBUG) System.out.println(s.toString());
		((SpaceTrackSolution) s).ignoreOtherRouteCost();
		s.execute(search);
		
		if (DEBUG) System.out.println(solver.toString());
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 

		// one conflict less, two less free relocations, one more space consumed
		int expectedCost = (-1 * 1000000) + (-4 * -1000) + (1 * -1); 
		assertEquals(expectedCost, s.getCostTlp());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}


	/**
	 * Test if tracks which are shorter than the given train are removed from
	 * the options to locate that train on.
	 */
	@Test
	public void test15() {
		try {
		if (DEBUG) System.out.println("========================test15========================");
		NsspInstance instance 			= GenTestTlpInstance.gen11(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		solver.createInitialSchedule();
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 
		if (DEBUG) System.out.println(solver.toString());

		Moment m0 = instance.getMoment(0);
		
		assertEquals(2, m0.getOptions().size());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}


	/**
	 * Test if tracks which are shorter than the given train are removed from
	 * the options to locate that train on.
	 */
	@Test
	public void test16() {
		try {
		if (DEBUG) System.out.println("========================test16========================");
		NsspInstance instance 			= GenTestTlpInstance.gen12(); 
		instance.setDebugging(DEBUG);
		TlpSolver solver 	= new TlpSolver(instance); 
		
		// We use the code below to remove the unrealistic options from the instance. 
		TrpSolver rSolver		= new TrpSolver(instance); 
		rSolver.removeAsObserver(); 
		
		// We initialize the location of the moments. 
		solver.initMomentAtLocation(0, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(1, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.initMomentAtLocation(2, instance.getShuntingYard().getTrack("c").getSideA()); 
		solver.createInitialSchedule();
		
		// Execute a change such that the schedule becomes feasible
		Side option = instance.getShuntingYard().getTrack("a").getSideA();
		solver.initMomentAtLocation(2, option); 
		
		if (DEBUG) System.out.println(TlpInstancePrinter.freeRelocationsToString(solver)); 
		if (DEBUG) System.out.println(solver.toString());

		Moment m0 = instance.getMoment(0);
		Moment m1 = instance.getMoment(1);
		Moment m2 = instance.getMoment(2);

		assertEquals(2, m0.getCoordinate());
		assertEquals(0, m1.getCoordinate());
		assertEquals(0, m2.getCoordinate());
		
	} catch (Exception e) {
		e.printStackTrace();
		fail(); 
	}
	}
	
}
