package sinbad2.phasemethod.linguistic.hesitant.twotuple.unification.ui.view.provider;

import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.twoTuple.HesitantTwoTupleValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TreeViewerContentProvider implements ITreeContentProvider {

	private ValuationSet _valutationSet;
	private Map<ValuationKey, Valuation> _valuations;
	private Object[][] _information;
	private Map<ValuationKey, Valuation> _unifiedEvaluations;
	private Map<ValuationKey, TrapezoidalFunction> _fuzzyNumbers;
	
	public TreeViewerContentProvider() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
	}
	
	public TreeViewerContentProvider(Map<ValuationKey, Valuation> unifiedEvaluations, Map<ValuationKey, TrapezoidalFunction> fuzzyNumbers) {
		this();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		_unifiedEvaluations = unifiedEvaluations;
		_fuzzyNumbers = fuzzyNumbers;
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
		_information = new Object[_valuations.size()][5];

		Domain domain;
		Valuation unifiedValuation;
		
		int i = 0;
		for (ValuationKey vk: _valuations.keySet()) {
			Valuation v = _valuations.get(vk);
			domain = v.getDomain();
		
			unifiedValuation = null;
			 for(ValuationKey vku: _unifiedEvaluations.keySet()) {
				if ((vku.getExpert().equals(vk.getExpert())) && (vku.getAlternative().equals(vk.getAlternative())) && (vku.getCriterion().equals(vk.getCriterion()))) {
					unifiedValuation = (Valuation) _unifiedEvaluations.get(vku);
					break;
				}
			}
			
			_information[i][0] = vk;
			_information[i][1] = domain.getId();
			_information[i][2] = v;
			_information[i][3] = unifiedValuation;
			_information[i][4] = ((HesitantTwoTupleValuation) unifiedValuation).calculateFuzzyEnvelopeEquivalentCLE((FuzzySet) unifiedValuation.getDomain());
			
			i++;
		}
		return _information;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result = new Object[1];
		if (parentElement instanceof Object[]) {
			result[0] = _fuzzyNumbers.get(((Object[]) parentElement)[0]) .toString();		
			return result;		
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
			if (((Object[]) element)[4] instanceof TrapezoidalFunction) {
				return true;
			}
		}

		return false;
	}

}

