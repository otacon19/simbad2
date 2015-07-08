package sinbad2.valuation.valuationset.listener;

import sinbad2.valuation.valuationset.ValuationSet;

public interface IValuationSetChangeListener {
	
	public void notifyValuationSetChange(ValuationSetChangeEvent event);
	public void notifyNewActiveValuationSet(ValuationSet valuationSet);
	
}
