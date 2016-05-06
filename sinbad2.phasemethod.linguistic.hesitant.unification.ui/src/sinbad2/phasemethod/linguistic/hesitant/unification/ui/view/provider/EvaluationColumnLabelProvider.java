package sinbad2.phasemethod.linguistic.hesitant.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.phasemethod.linguistic.hesitant.unification.ui.nls.Messages;
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
					return aux + " " + valuation.getTerm().getName(); //$NON-NLS-1$
				} else {
					return Messages.EvaluationColumnLabelProvider_Betwwen + " " + valuation.getLowerTerm().getName() + " " + Messages.EvaluationColumnLabelProvider_And + " " + valuation.getUpperTerm().getName();  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				}
			} else {
				return Messages.EvaluationColumnLabelProvider_Not_evaluate;
			}
		} else {
			return null;
		}
	}
}