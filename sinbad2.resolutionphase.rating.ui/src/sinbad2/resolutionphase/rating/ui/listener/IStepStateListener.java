package sinbad2.resolutionphase.rating.ui.listener;

import sinbad2.resolutionphase.rating.ui.view.RatingView;

public interface IStepStateListener {

	public void notifyStepStateChange();
	
	public void setRatingView(RatingView rating);
}
