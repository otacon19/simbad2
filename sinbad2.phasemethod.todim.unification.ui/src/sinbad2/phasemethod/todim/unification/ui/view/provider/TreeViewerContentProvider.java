package sinbad2.phasemethod.todim.unification.ui.view.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class TreeViewerContentProvider implements ITreeContentProvider {

	private ValuationSet _valutationSet;
	private Map<ValuationKey, Valuation> _valuations;
	private Object[][] _information;
	private Map<ValuationKey, TrapezoidalFunction> _unificationFuzzyNumbers;
	
	public TreeViewerContentProvider() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
	}
	
	public TreeViewerContentProvider(Map<ValuationKey, TrapezoidalFunction> unificationFuzzyNumbers) {
		this();
		
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
		_unificationFuzzyNumbers = unificationFuzzyNumbers;
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

		Expert expert;
		Alternative alternative;
		Criterion criterion;
		Domain domain;
		TrapezoidalFunction tpf;
		
		Map<ValuationKey, Valuation> valuationsWithoutConfidence = getValuationsWithoutImportanceAndKnowledge();
		_information = new Object[valuationsWithoutConfidence.size()][6];
		
		int i = 0;
		for (ValuationKey vk: valuationsWithoutConfidence.keySet()) {
			Valuation v = valuationsWithoutConfidence.get(vk);
			expert = vk.getExpert();
			alternative = vk.getAlternative();
			criterion = vk.getCriterion();
			domain = v.getDomain();
		
			tpf = null;
			 for(ValuationKey vku: _unificationFuzzyNumbers.keySet()) {
				 
				if ((vku.getExpert().equals(vk.getExpert())) && (vku.getAlternative().equals(vk.getAlternative())) && (vku.getCriterion().equals(vk.getCriterion()))) {
					tpf = _unificationFuzzyNumbers.get(vku);
					break;
				}
			}
			 
			_information[i][0] = expert.getCanonicalId();
			_information[i][1] = alternative.getId();
			_information[i][2] = criterion.getCanonicalId();
			_information[i][3] = domain.getId();
			_information[i][4] = v;
			_information[i][5] = tpf.toString();
			
			i++;
		}
		
		return _information;
	}

	private Map<ValuationKey, Valuation> getValuationsWithoutImportanceAndKnowledge() {
		Map<ValuationKey, Valuation> result = new HashMap<ValuationKey, Valuation>();
		
		for(ValuationKey vk: _valuations.keySet()) {
			if(!vk.getAlternative().getId().contains("null_")) {
				Valuation v = _valuations.get(vk);
				result.put(vk, v);
			}
		}
		
		return result;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
