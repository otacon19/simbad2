package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking;

import java.util.LinkedList;
import java.util.List;

public class RankingViewManager {
	
	private static RankingViewManager _instance = null;

	private Object _activeRanking;
	private List<IDisplayRankingChangeListener> _listeners;

	private RankingViewManager() {
		_activeRanking = null;
		_listeners = new LinkedList<IDisplayRankingChangeListener>();
	}

	public static RankingViewManager getInstance() {
		
		if(_instance == null) {
			_instance = new RankingViewManager();
		}

		return _instance;
	}

	public void setContent(Object ranking) {
		_activeRanking = ranking;
		notifyDisplayRankingChange();
	}

	public void registerDisplayRankingChangeListener(IDisplayRankingChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterDisplayRankingChangeListener(IDisplayRankingChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifyDisplayRankingChange() {
		
		for (IDisplayRankingChangeListener listener : _listeners) {
			listener.displayRankingChange(_activeRanking);
		}
	}

}
