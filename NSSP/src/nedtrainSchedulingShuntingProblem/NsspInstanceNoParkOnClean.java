package nedtrainSchedulingShuntingProblem;

import java.util.ArrayList;
import java.util.Collection;

import shuntingYard.ShuntingYard;
import shuntingYard.Track;

public class NsspInstanceNoParkOnClean extends NsspInstance{
	
	public NsspInstanceNoParkOnClean(ShuntingYard yard) throws Exception {
		super(yard); 
		super.add = new MomentAdderRemoveCleanFromOptions(this); 
	}
	
	protected class MomentAdderRemoveCleanFromOptions extends MomentAdder {

		public MomentAdderRemoveCleanFromOptions(NsspInstance in) {
			super(in);
		}
		
		@Override
		public void Moment(ObservedMoment m) throws Exception {
			super.Moment(m);
			boolean isParkMoment = this.isParkMoment(m); 
			if(isParkMoment) {
				this.removeCleanFromOptions(m); 
			}
		}

		private boolean isParkMoment(ObservedMoment m) {
			Collection<Track> options = m.getOptions().getCollectionOfTracks(); 
			if(options.size() == 0) {
				return false; 
			}
			boolean isParkMoment = false; 
			for(Track t: options) {
				if (!t.isCleaningTrack()) {
					isParkMoment = true; 
					break; 
				}
			}
			return isParkMoment;
		}

		private void removeCleanFromOptions(ObservedMoment m) {
			TrackSolutionSpace options 	= m.getOptions(); 
			Collection<Track> tracks	= m.getOptions().getCollectionOfTracks(); 
			ArrayList<Track> remove		= new ArrayList<>(); 
			for(Track t : tracks) {
				if(t.isCleaningTrack()) {
					remove.add(t); 
				}
			}
			for(Track t : remove) {
				options.permanentlyRemoveOption(t); 
			}
		}
		
	}
	

}
