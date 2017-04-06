package nedtrainSchedulingShuntingProblem;

import solutionSpace.SolutionSet;

public interface Subproblem {
	
	/**
	 * This subProblem wil generate a solutionSet solving one or more conflicts
	 * defined within it.
	 * 
	 * @return
	 */
	public SolutionSet genSolutions() throws Exception; 
	
	/**
	 * This subProblem will assign the costs for this supProblem to the given
	 * solutions within the solutionSet.
	 * 
	 * @param ss
	 */
	public void assignCosts(SolutionSet ss) throws Exception;
	
	/**
	 * The subProblem returns the a value indicating the severity of the
	 * conflicts within the subProblem. The severity might depend on the amount
	 * of conflicts or the amount of solution space still available to solve the
	 * conflicts. A higher return value indicates a more severe subProblem. The
	 * costs are 0 if no conflicts exists within this subProblem.
	 * 
	 * @return
	 */
	public int getProblemSeverity() throws Exception;
	
	/**
	 * This method creates an initial schedule for the subproblem. Only
	 * variables are assigned which have an affect on the conflicts defined
	 * within the conflict space within the sub problem. This method should be
	 * designed such that it will leave any variable which is already assigned
	 * as it originally was, i.e. if problem 1 and 2 both share a solution space
	 * X we can first create an initial schedule for problem 1 so that variables
	 * within X are assigned to the instance, if we then would call this method
	 * for problem 2 no other assignment of variables within X may again be
	 * done.
	 */
	public void createInitialSchedule() throws Exception ;

	/**
	 * Not sure if we want this method. The idea is that we can schedule a
	 * single moment within a solution space. However, subProblems typically do
	 * not deal with ObservedMoments but with their own versions of such a
	 * moment.
	 */
	public SolutionSet schedule(Moment m) throws Exception ;

}
