package sinbad2.phasemethod.linguistic.hesitant.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;

public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[4];
			if(result != null) {
				HesitantValuation valuation = (HesitantValuation) result;
				if(valuation.isPrimary()) {
					return valuation.getLabel().getName();
				} else if (valuation.isUnary()) {
					String aux = valuation.getUnaryRelation().getRelationType();
					aux = aux.toLowerCase();
					aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
					return aux + " " + valuation.getTerm().getName();
				} else {
					return "Between" + " " + valuation.getLowerTerm().getName() + " " + "and" + " " + valuation.getUpperTerm().getName();
				}
			} else {
				return "Not evaluate";
			}
		} else {
			return null;
		}
	}
}