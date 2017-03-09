package sinbad2.phasemethod.linguistic.hesitant.twotuple.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.valuationset.ValuationKey;

public class AlternativeColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return ((ValuationKey) ((Object[]) element)[0]).getAlternative().getCanonicalId();
		} else {
			return null;
		}
	}
}