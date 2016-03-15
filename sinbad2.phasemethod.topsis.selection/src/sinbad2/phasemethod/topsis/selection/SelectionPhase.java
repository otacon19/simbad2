package sinbad2.phasemethod.topsis.selection;

import java.util.HashMap;
import java.util.Map;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class SelectionPhase implements IPhaseMethod {
	
	private Map<Criterion, Map<Alternative, Double>> _decisionMatrix;
	
	private ValuationSet _valuationSet;

	public SelectionPhase() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = valuationSetManager.getActiveValuationSet();
		
		_decisionMatrix = new HashMap<Criterion, Map<Alternative, Double>>();
		
		calculateDecisionMatrix();
		standarizeDecisionMatrix();
	}

	public Map<Criterion, Map<Alternative, Double>> getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setDecisionMatrix(Map<Criterion, Map<Alternative, Double>> decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	private void calculateDecisionMatrix() {		
		
	}
	
	private void standarizeDecisionMatrix() {
		
		
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new SelectionPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		SelectionPhase selectionPhase = (SelectionPhase) iPhaseMethod;
		
		clear();
		
		_decisionMatrix = selectionPhase.getDecisionMatrix();
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		
		if (_valuationSet.getValuations().isEmpty()) {
			return false;
		}

		return true;
	}
	
	@Override
	public IPhaseMethod clone() {
		SelectionPhase result = null;

		try {
			result = (SelectionPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	public void clear() {
		_decisionMatrix.clear();
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if(event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
