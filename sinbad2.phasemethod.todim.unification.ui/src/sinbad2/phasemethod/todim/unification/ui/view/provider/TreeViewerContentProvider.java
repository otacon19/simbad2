package sinbad2.phasemethod.todim.unification.ui.view.provider;

import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TreeViewerContentProvider implements ITreeContentProvider {

	private ValuationSet _valutationSet;
	private Map<ValuationKey, Valuation> _valuations;
	private Object[][] _information;
	private Map<ValuationKey, Valuation> _unifiedEvaluations;
	
	public TreeViewerContentProvider() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
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

		Expert expert;
		Alternative alternative;
		Criterion criterion;
		Domain domain;
		Valuation unifiedValuation;
	
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
					break;
				}
			}
	
			_information[i][0] = expert.getCanonicalId();
			_information[i][1] = alternative.getId();
			_information[i][2] = criterion.getCanonicalId();
			_information[i][3] = domain.getId();
			_information[i][4] = v;
			_information[i][5] = unifiedValuation;
			
			i++;
		}
		return _information;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Object[]) {
			Valuation valuation = (Valuation) ((Object[]) parentElement)[5];
			if (valuation instanceof UnifiedValuation) {
				FuzzySet domain = (FuzzySet) valuation.getDomain();
				int size = domain.getLabelSet().getCardinality();
				String[] result = new String[size];
				String labelName, measure;
				for (int i = 0; i < size; i++) {
					labelName = domain.getLabelSet().getLabel(i).getName();
					measure = Double.toString(domain.getValue(i));
					if (measure.length() > 5) {
						measure = measure.substring(0, 5);
					}
					result[i] = labelName + "/" + measure; //$NON-NLS-1$
				}		
				return result;
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
			if (((Object[]) element)[5] instanceof UnifiedValuation) {
				return true;
			}
		}

		return false;
	}
}
