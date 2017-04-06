package classTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import nedtrainSchedulingShuntingProblem.AlgSettings;
import nedtrainSchedulingShuntingProblem.AlgSettings.Init;
import nedtrainSchedulingShuntingProblem.AlgSettings.Search;
import nedtrainSchedulingShuntingProblem.NsspInstance;
import nsspAlgorithmVariations.NsspSequentialSolver;
import nsspAlgorithmVariations.NsspSequentialSolver.SubProblem;

import org.junit.Test;

import shuntingYard.Location;
import trainLocationProblem.LocationCost;
import trainRoutingProblem.RouteCost;

public class TestSequentialSolver {
	
	private static final boolean DEBUG 				= false; 
	private static final boolean DEBUG_ALG_STEPS 	= false; 

	@Test
	public void test1() {
		NsspSequentialSolver ns = null; 
		try {
		
		if(DEBUG) System.out.println("========================test1========================");
		NsspInstance instance 			= GenTestTrpInstance.gen1(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		/*----------------------------settings----------------------------*/
		
		AlgSettings settings = new AlgSettings(); 
		settings.setSearch(Search.normal);
		settings.setSubProblemInit(Init.trpProposes); 
		
		RouteCost routeCost		= new RouteCost(); 
		routeCost.setTurnCost(1);
		settings.setRouteCost(routeCost);
		
		LocationCost locationCost = new LocationCost(); 
		locationCost.setFreeRelocationCost(-1); 
		settings.setLocationCost(locationCost); 
		
		/*----------------------------settings----------------------------*/

		ns = new NsspSequentialSolver(instance, settings, SubProblem.TRP); 
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
	
	/**
	 * This test shows that the sequential algorithm is not capable of solving a
	 * tight instance. 
	 */
	@Test
	public void test2() {
		NsspSequentialSolver ns = null; 
		try {
		if(DEBUG) System.out.println("========================test5========================");
		NsspInstance instance 			= GenTestNsspInstance.gen2(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		/*----------------------------settings----------------------------*/
		
		AlgSettings settings = new AlgSettings(); 
		settings.setSearch(Search.normal);
		settings.setSubProblemInit(Init.trpProposes); 
		
		RouteCost routeCost		= new RouteCost(); 
		routeCost.setTurnCost(1);
		settings.setRouteCost(routeCost);
		
		LocationCost locationCost = new LocationCost(); 
		locationCost.setFreeRelocationCost(-1); 
		settings.setLocationCost(locationCost); 
		
		/*----------------------------settings----------------------------*/

		ns = new NsspSequentialSolver(instance, settings, SubProblem.TRP); 
		ns.createInitialSchedule(); 
		boolean solved = ns.solve(); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		assertTrue(!solved);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
	/**
	 * From a given starting point, the sequential algorithm is capable of
	 * solving the same instance as test2(). It first solves all TRP conflicts,
	 * then all TLP conflicts and finally all newly created TRP conflicts. It
	 * needs a total of two swaps between algorithms.
	 */
	@Test
	public void test3() {
		NsspSequentialSolver ns = null; 
		try {
		if(DEBUG) System.out.println("========================test5========================");
		NsspInstance instance 			= GenTestNsspInstance.gen2(); 
		instance.setDebugging(DEBUG_ALG_STEPS); 
		
		/*----------------------------initial shunts----------------------------*/
		instance.initMomentRoute(0, instance.getShuntingYard().getEntrance(), instance.getTrack("c").getSideA(), Search.tabu);
		instance.initMomentRoute(1, instance.getShuntingYard().getEntrance(), instance.getTrack("b").getSideA(), Search.tabu);
		instance.initMomentRoute(2, instance.getShuntingYard().getEntrance(), instance.getTrack("b").getSideA(), Search.tabu);
		instance.initMomentRoute(3, instance.getShuntingYard().getEntrance(), instance.getTrack("b").getSideA(), Search.tabu);
		instance.initMomentRoute(4, instance.getShuntingYard().getEntrance(), instance.getTrack("a").getSideA(), Search.tabu);
		instance.initMomentRoute(5, instance.getShuntingYard().getEntrance(), instance.getTrack("a").getSideA(), Search.tabu);
		instance.initMomentRoute(6, instance.getShuntingYard().getEntrance(), instance.getTrack("a").getSideA(), Search.tabu);
		/*----------------------------initial shunts----------------------------*/
		
		/*----------------------------settings----------------------------*/
		AlgSettings settings = new AlgSettings(); 
		settings.setSearch(Search.normal);
		settings.setSubProblemInit(Init.trpProposes); 
		
		RouteCost routeCost		= new RouteCost(); 
		routeCost.setTurnCost(1);
		settings.setRouteCost(routeCost);
		
		LocationCost locationCost = new LocationCost(); 
		locationCost.setFreeRelocationCost(-1); 
		settings.setLocationCost(locationCost); 
		/*----------------------------settings----------------------------*/

		ns = new NsspSequentialSolver(instance, settings, SubProblem.TRP); 
		ns.createInitialSchedule(); 
		boolean solved = ns.solve(); 
		
		if (DEBUG) System.out.println(ns.toString());
		
		assertTrue(solved);
		assertEquals(2, ns.getSwaps());
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(); 
		}
	}
	
}