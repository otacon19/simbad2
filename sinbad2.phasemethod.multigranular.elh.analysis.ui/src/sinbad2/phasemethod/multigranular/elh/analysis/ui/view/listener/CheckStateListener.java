package sinbad2.phasemethod.multigranular.elh.analysis.ui.view.listener;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.multigranular.elh.analysis.ui.view.Analysis;

public class CheckStateListener implements ICheckStateListener {

	private final int NOTHING = 0;
	private final int PARTIAL = 1;
	private final int FULL = 2;

	private CheckboxTreeViewer _treeViewer;
	private ProblemElementsSet _elementsSet;
	private Analysis _analysis;

	private CheckStateListener() {
		super();
	}

	public CheckStateListener(CheckboxTreeViewer treeViewer, ProblemElementsSet elementsSet, Analysis analysis) {
		this();

		_treeViewer = treeViewer;
		_elementsSet = elementsSet;
		_analysis = analysis;
	}

	private void checkSon(ProblemElement element, boolean checked) {
		if(element instanceof Expert) {
			List<Expert> childrens = ((Expert) element).getChildrens();
			if(childrens != null) {
				for (ProblemElement son : childrens) {
					checkSon(son, checked);
				}
			}
		} else if(element instanceof Criterion) {
			List<Criterion> subcriteria = ((Criterion) element).getSubcriteria();
			if(subcriteria != null) {
	 			for (ProblemElement son : subcriteria) {
					checkSon(son, checked);
				}
			}
		}
		
		_treeViewer.setGrayChecked(element, false);
		_treeViewer.setChecked(element, checked);
	}

	private int selection(ProblemElement element) {
		List<ProblemElement> sons;
		
		if(element instanceof Expert) {
			sons = _elementsSet.getElementExpertChildren(element);
		} else if(element instanceof Criterion) {
			sons = _elementsSet.getElementCriterionSubcriteria(element);
		} else {
			sons = _elementsSet.getElementAlternatives();
		}
		
		int result = NOTHING;
		if (sons.size() == 0) {
			if (_treeViewer.getChecked(element)) {
				result = FULL;
			} else {
				result = NOTHING;
			}
		} else {
			result = FULL;
			int selection = 0;
			for (ProblemElement son : sons) {
				selection += selection(son);
			}
			if (selection == 0) {
				result = NOTHING;
			} else if (selection == (sons.size() * 2)) {
				result = FULL;
			} else {
				result = PARTIAL;
			}
		}

		return result;
	}

	private void setCheckState(ProblemElement element) {
		List<ProblemElement> sons = new LinkedList<ProblemElement>();
		
		if(element instanceof Expert) {
			sons = _elementsSet.getElementExpertChildren(element);
		} else if(element instanceof Criterion) {
			sons = _elementsSet.getElementCriterionSubcriteria(element);
		}
		
		if(sons.size() == 0) {
			boolean checked = _treeViewer.getChecked(element);
			_treeViewer.setGrayChecked(element, false);
			_treeViewer.setChecked(element, checked);
		} else {
			for (ProblemElement son : sons) {
				setCheckState(son);
			}
			switch (selection(element)) {
				case NOTHING:
					_treeViewer.setGrayChecked(element, false);
					_treeViewer.setChecked(element, false);
					break;
				case FULL:
					_treeViewer.setGrayChecked(element, false);
					_treeViewer.setChecked(element, true);
					break;
				case PARTIAL:
					_treeViewer.setGrayChecked(element, true);
					break;
			}
		}
	}

	public void rebuildTreeState(ProblemElement element) {
		_treeViewer.removeCheckStateListener(this);
		List<ProblemElement> sons = new LinkedList<ProblemElement>();
		
		if(element instanceof Expert) {
			sons = _elementsSet.getElementExpertChildren(null);
		} else if(element instanceof Criterion) {
			sons = _elementsSet.getElementCriterionSubcriteria(null);
		}
		
		for (ProblemElement son : sons) {
			setCheckState(son);
		}
		_treeViewer.addCheckStateListener(this);
		_analysis.refreshRanking();
	}

	public void checkAll(String type) {
		_treeViewer.removeCheckStateListener(this);
		
		if(type.equals("EXPERTS")) {
			for(Expert element : _elementsSet.getExperts()) {
				if(element.hasChildrens()) {
					for(Expert child: element.getChildrens()) {
						_treeViewer.setGrayChecked(child, false);
						_treeViewer.setChecked(child, true);
					}
				}
				_treeViewer.setGrayChecked(element, false);
				_treeViewer.setChecked(element, true);
			}
		} else if(type.equals("ALTERNATIVES")) {
			for(Alternative element : _elementsSet.getAlternatives()) {
				_treeViewer.setGrayChecked(element, false);
				_treeViewer.setChecked(element, true);
			}
		} else {
			for(Criterion element : _elementsSet.getCriteria()) {
				if(element.hasSubcriteria()) {
					for(Criterion subcriterion: element.getSubcriteria()) {
						_treeViewer.setGrayChecked(subcriterion, false);
						_treeViewer.setChecked(subcriterion, true);
					}
				}
				_treeViewer.setGrayChecked(element, false);
				_treeViewer.setChecked(element, true);
			}
		}
		
		_treeViewer.addCheckStateListener(this);
		_analysis.refreshRanking();
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		_treeViewer.removeCheckStateListener(this);
		ProblemElement element = (ProblemElement) event.getElement();
		Boolean checked = event.getChecked();
		checkSon(element, checked);
		rebuildTreeState(element);
		_treeViewer.addCheckStateListener(this);
	}

}
