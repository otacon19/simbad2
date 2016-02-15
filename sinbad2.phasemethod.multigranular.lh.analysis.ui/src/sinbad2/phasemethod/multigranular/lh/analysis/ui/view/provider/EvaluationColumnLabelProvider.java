package sinbad2.phasemethod.multigranular.lh.analysis.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.twoTuple.TwoTuple;


public class EvaluationColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (((Object[]) element)[2] != null) {
			if (((Object[]) element)[2] instanceof TwoTuple) {
				TwoTuple valuation = (TwoTuple) ((Object[]) element)[2];
				String labelName = valuation.getLabel().getName();
				String alpha = Double.toString(valuation.getAlpha());
				if (alpha.equals("-0.0")) { //$NON-NLS-1$
					alpha = "0"; //$NON-NLS-1$
				}
				if (alpha.equals("0.0")) { //$NON-NLS-1$
					alpha = "0"; //$NON-NLS-1$
				}
				int size = 4;
				if (alpha.startsWith("-")) {
					size = 5;
				}
				if (alpha.length() > size) {
					alpha = alpha.substring(0, size);
				}
				if (alpha.length() > 1) {
					if (alpha.endsWith("0")) {
						alpha = alpha.substring(0, size - 1);
					}
				}
				return "(" + labelName + ", " + alpha + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				return ""; //$NON-NLS-1$
			}
		} else {
			return "Not evaluate";
		}
	}
}

