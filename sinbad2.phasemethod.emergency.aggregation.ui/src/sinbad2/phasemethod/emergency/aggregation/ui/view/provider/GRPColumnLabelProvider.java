package sinbad2.phasemethod.emergency.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.valuation.real.interval.RealIntervalValuation;

public class GRPColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		RealIntervalValuation valuation = (RealIntervalValuation) ((Object[]) element)[1]; 
		
		return valuation.changeFormatValuationToString();
	}

}
