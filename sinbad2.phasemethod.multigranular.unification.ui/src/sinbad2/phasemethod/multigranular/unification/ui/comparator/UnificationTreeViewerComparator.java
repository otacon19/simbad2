package sinbad2.phasemethod.multigranular.unification.ui.comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.phasemethod.multigranular.unification.ui.view.provider.EvaluationColumnLabelProvider;

public class UnificationTreeViewerComparator extends ViewerComparator {

	private int _propertyIndex;
	private static final int DESCENDING = SWT.DOWN;
	private static final int ASCENDING = SWT.UP;
	private int direction = ASCENDING;
	private Domain _domain = null;

	public UnificationTreeViewerComparator() {
		_propertyIndex = 0;
		direction = ASCENDING;
	}

	public int getDirection() {
		return direction;
	}

	public void setDomain(Domain domain) {
		_domain = domain;
	}

	public void setColumn(int column) {
		if (column == _propertyIndex) {
			if (direction == DESCENDING) {
				direction = ASCENDING;
			} else {
				direction = DESCENDING;
			}
		} else {
			_propertyIndex = column;
			direction = ASCENDING;
		}
	}

	public int compare(Object[] e1, Object[] e2, int pos) {

		String text1;
		String text2;

		int result;
		if (pos < 4) {
			result = ((String) e1[pos]).compareTo((String) e2[pos]);
		} else if (pos == 4) {
			result = compare(e1, e2, 5);
		} else if (pos == 5) {
			EvaluationColumnLabelProvider eclp = new EvaluationColumnLabelProvider();
			text1 = eclp.getText(e1);
			text2 = eclp.getText(e2);
			result = text1.compareTo(text2);
		} else {
			result = 0;
		}

		if (result == 0) {
			if (pos == _propertyIndex) {
				if (pos != 0) {
					result = compare(e1, e2, 0);
				} else {
					if (pos < 5) {
						result = compare(e1, e2, pos + 1);
					}
				}
			} else if (pos < _propertyIndex) {
				if ((pos + 1) != _propertyIndex) {
					result = compare(e1, e2, pos + 1);
				} else {
					if (_propertyIndex < 5) {
						result = compare(e1, e2, pos + 2);
					}
				}
			} else if (pos > _propertyIndex) {
				if ((pos + 1) < 5) {
					result = compare(e1, e2, pos + 1);
				}
			}
		}
		return result;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int result;

		if ((e1 instanceof Object[]) && (e2 instanceof Object[])) {
			Object[] evaluation1 = (Object[]) e1;
			Object[] evaluation2 = (Object[]) e2;
			result = compare(evaluation1, evaluation2, _propertyIndex);

			if (direction == DESCENDING) {
				result = -result;
			}

		} else {
			String evaluation1 = (String) e1;
			String evaluation2 = (String) e2;
			if (_domain != null) {
				if (_domain instanceof FuzzySet) {
					if (evaluation1.contains("/")) {
						evaluation1 = evaluation1.substring(0,
								evaluation1.lastIndexOf("/"));
					}
					if (evaluation2.contains("/")) {
						evaluation2 = evaluation2.substring(0,
								evaluation2.lastIndexOf("/"));
					}
					int pos1 = ((FuzzySet) _domain).getLabelSet().getPos(evaluation1);
					int pos2 = ((FuzzySet) _domain).getLabelSet().getPos(evaluation2);
					return pos1 - pos2;
				}
			}
			result = evaluation1.compareTo(evaluation2);
		}

		return result;
	}

}
