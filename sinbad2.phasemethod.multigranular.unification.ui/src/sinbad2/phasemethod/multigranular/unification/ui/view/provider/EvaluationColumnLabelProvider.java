package sinbad2.phasemethod.multigranular.unification.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealInterval;

public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Object[]) {
			Valuation result = (Valuation) ((Object[]) element)[4];
			if(result != null) {
				if(result instanceof IntegerValuation) {
					return Integer.toString((int) ((IntegerValuation) result).getValue());
				} else if(result instanceof RealValuation){
					return Double.toString(((RealValuation) result).getValue());
				} else if(result instanceof HesitantValuation) {
					HesitantValuation valuation = (HesitantValuation) result;
					if(valuation.isPrimary()) {
						return valuation.getLabel().getName();
					} else if(valuation.isUnary()) {
						String aux = valuation.getUnaryRelation().getRelationType();
						aux = aux.toLowerCase();
						aux = aux.substring(0, 1).toUpperCase() + aux.substring(1);
						return aux + " " + valuation.getTerm().getName();
					} else {
						return "Between" + " " + valuation.getLowerTerm().getName() + " " + "and" + " " + valuation.getUpperTerm().getName();
					}
				} else if(result instanceof LinguisticValuation) {
					return ((LinguisticValuation) result).getLabel().getName();
				} else if(result instanceof IntegerInterval) {
						return "[" + Integer.toString((int) ((IntegerInterval) result).getMin()) + ", " + Integer.toString((int) ((IntegerInterval) result).getMax()) + "]"; //$NON-NLS-1$
				} else if(result instanceof RealInterval) {
						return "[" + Double.toString(((RealInterval) result).getMin()) + ", " + Double.toString(((RealInterval) result).getMax()) + "]"; //$NON-NLS-1$
				} else {
					return "Not evaluate";
				}
			} else {
				return "Not evaluate";
			}
		} else {
			System.out.println("null");
			return null;
		}
	}

}
