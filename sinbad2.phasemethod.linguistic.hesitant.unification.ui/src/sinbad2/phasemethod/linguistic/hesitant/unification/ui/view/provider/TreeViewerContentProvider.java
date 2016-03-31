package sinbad2.phasemethod.linguistic.hesitant.unification.ui.view.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.linguistic.hesitant.unification.UnificationPhase;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TreeViewerContentProvider implements ITreeContentProvider {

	private ValuationSet _valutationSet;
	private Map<ValuationKey, Valuation> _valuations;
	private Object[][] _information;
	private Map<ValuationKey, Valuation> _unifiedEvaluations;
	private Map<String, TrapezoidalFunction> _trapezoidalFunctions;
	
	private UnificationPhase _unificationPhase;
	
	public TreeViewerContentProvider() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_unificationPhase = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();
		
		_trapezoidalFunctions = new HashMap<String, TrapezoidalFunction>();
	}
	
	public TreeViewerContentProvider(Map<ValuationKey, Valuation> unifiedEvaluations) {
		this();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		_unifiedEvaluations = unifiedEvaluations;
	}
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((Object[][]) inputElement);
	}

	public Object[][] getInput() {
		_valuations = _valutationSet.getValuations();
		_information = new Object[_valuations.size()][6];
		Map<ValuationKey, TrapezoidalFunction> envelopeValuations = _unificationPhase.getEnvelopeValuations();

		Expert expert;
		Alternative alternative;
		Criterion criterion;
		Domain domain;
		Valuation unifiedValuation;
		TrapezoidalFunction tmf = null;
		
		int i = 0;
		for (ValuationKey vk: _valuations.keySet()) {
			Valuation v = _valuations.get(vk);
			expert = vk.getExpert();
			alternative = vk.getAlternative();
			criterion = vk.getCriterion();
			domain = v.getDomain();
		
			unifiedValuation = null;
			 for(ValuationKey vku: _unifiedEvaluations.keySet()) {
				if ((vku.getExpert().equals(vk.getExpert())) && (vku.getAlternative().equals(vk.getAlternative())) && (vku.getCriterion().equals(vk.getCriterion()))) {
					unifiedValuation = (Valuation) _unifiedEvaluations.get(vku);
					tmf = envelopeValuations.get(vku);
					break;
				}
			}
			
			_information[i][0] = expert.getCanonicalId();
			_information[i][1] = alternative.getId();
			_information[i][2] = criterion.getCanonicalId();
			_information[i][3] = domain.getId();
			_information[i][4] = v;
			_information[i][5] = unifiedValuation;
			
			unifiedValuation.setId(Integer.toString(i));
			
			_trapezoidalFunctions.put(unifiedValuation.getId(), tmf);
			
			i++;
		}
		return _information;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Object[]) {
			Valuation valuation = (Valuation) ((Object[]) parentElement)[5];
			if (valuation instanceof TwoTuple) {
				Object[] tf = new String[1];
				tf[0] = _trapezoidalFunctions.get(valuation.getId()).toString();
				return tf;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		if (element instanceof Object[]) {
			if (((Object[]) element)[5] instanceof TwoTuple) {
				return true;
			}
		}

		return false;
	}

}
