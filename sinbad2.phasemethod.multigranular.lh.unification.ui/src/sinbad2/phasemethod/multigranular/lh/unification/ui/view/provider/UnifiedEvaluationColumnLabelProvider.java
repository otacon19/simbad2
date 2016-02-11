package sinbad2.phasemethod.multigranular.lh.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public class UnifiedEvaluationColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		
		if (element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[5];
			if (result != null) {
					TwoTuple valuation;
					if (result instanceof UnifiedValuation) {
						FuzzySet domain = (FuzzySet) result.getDomain();
						valuation = ((UnifiedValuation) result).disunification(domain);
					} else {
						valuation = (TwoTuple) result;
					}
					
					String labelName = valuation.getLabel().getName();
					String alpha = Double.toString(valuation.getAlpha());
					
					if(alpha.equals("-0.0") || alpha.equals("0.0")) { //$NON-NLS-1$
						alpha = "0"; //$NON-NLS-1$
					}

					int size = 4;
					if(alpha.startsWith("-")) {
						size = 5;
					}
					
					if(alpha.length() > size) {
						alpha = alpha.substring(0, size);
					}
					
					if(alpha.length() > 1) {
						if(alpha.endsWith("0")) {
							alpha = alpha.substring(0, size - 1);
						}
					}
					return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				return "NA";
			}
		} else {
			return (String) element;
		}
	}
	
}
