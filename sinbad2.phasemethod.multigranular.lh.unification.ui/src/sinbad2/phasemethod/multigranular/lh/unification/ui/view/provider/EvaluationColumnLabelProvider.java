package sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.LinguisticValuation;

public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[4];
			if(result != null) {
				if(result instanceof LinguisticValuation) {
					return ((LinguisticValuation) result).getLabel().getName();
				} else {
					return "Not evaluate";
				}
			} else {
				return "Not evaluate";
			}
		} else {
			return null;
		}
	}
}