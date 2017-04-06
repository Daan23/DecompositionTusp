package nedtrainSchedulingShuntingProblem;

public interface MomentObserver {
	
	/**
	 * This method is to communicate changes to the current moment to algorithm
	 * specific data structures which extend upon the moments. 
	 */
	void momentIsChanged(Moment m) throws Exception;

	/**
	 * Make the data structure consistent again after it was changed. 
	 */
	void makeConsistent() throws Exception; 

}
