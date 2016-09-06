package sinbad2.phasemethod;

import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.phasemethod.listener.IPhaseMethodStateListener;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;

public interface IPhaseMethod extends IPhaseMethodStateListener {

	public void clear();

	public IPhaseMethod clone();

	public IPhaseMethod copyStructure();

	public void copyData(IPhaseMethod iPhaseMethod);

	public void activate();

	public boolean validate();
	
	public Domain getUnifiedDomain();

	public Map<ValuationKey, Valuation> getTwoTupleValuations();
}
