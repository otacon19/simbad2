package sinbad2.phasemethod.linguistic.hesitant.twotuple.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.phasemethod.linguistic.hesitant.twotuple.unification.ui.nls.Messages;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;

public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[2];
			if(result != null) {
				if(result instanceof HesitantValuation) {
					return ((HesitantValuation) result).changeFormatValuationToString();
				} else {
					return Messages.EvaluationColumnLabelProvider_Not_evaluated;
				}
			} else {
				return Messages.EvaluationColumnLabelProvider_Not_evaluated;
			}
		} else {
			return null;
		}
	}
}
