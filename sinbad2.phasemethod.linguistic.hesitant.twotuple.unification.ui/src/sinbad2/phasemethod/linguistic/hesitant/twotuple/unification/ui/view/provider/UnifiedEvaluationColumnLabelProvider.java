package sinbad2.phasemethod.linguistic.hesitant.twotuple.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;

public class UnifiedEvaluationColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		
		if (element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[3];
			if (result != null) {
					if(result instanceof HesitantTwoTupleValuation) {
						return ((HesitantTwoTupleValuation) result).changeFormatValuationToString();
					} else {
						return "NA";//$NON-NLS-1$
					}
			} else {
				return "NA"; //$NON-NLS-1$
			}
		} else {
			return (String) element;
		}
	}
	
}

