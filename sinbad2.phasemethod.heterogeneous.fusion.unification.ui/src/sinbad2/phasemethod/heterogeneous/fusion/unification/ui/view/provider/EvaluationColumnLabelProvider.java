package sinbad2.phasemethod.heterogeneous.fusion.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.phasemethod.heterogeneous.fusion.unification.ui.nls.Messages;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;

public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[4];
			if(result != null) {
				if(result instanceof LinguisticValuation) {
					return ((LinguisticValuation) result).getLabel().getName();
				} else if(result instanceof IntegerValuation) {
					return Integer.toString((int) ((IntegerValuation) result).getValue());
				} else if(result instanceof RealValuation) {
					return Double.toString(((RealValuation) result).getValue());
				} else if(result instanceof IntegerIntervalValuation) {
					return "[" + Integer.toString((int) ((IntegerIntervalValuation) result).getMin()) + ", " + Integer.toString((int) ((IntegerIntervalValuation) result).getMax()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if(result instanceof RealIntervalValuation) {
					return "[" + Double.toString(((RealIntervalValuation) result).getMin()) + ", " + Double.toString(((RealIntervalValuation) result).getMax()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					return Messages.EvaluationColumnLabelProvider_Not_evaluate;
				}
			} else {
				return Messages.EvaluationColumnLabelProvider_Not_evaluate;
			}
		} else {
			return null;
		}
	}
}
