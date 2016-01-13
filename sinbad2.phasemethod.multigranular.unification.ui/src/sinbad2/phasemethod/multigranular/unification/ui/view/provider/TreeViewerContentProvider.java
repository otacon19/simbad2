package sinbad2.phasemethod.multigranular.unification.ui.view.provider;

import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
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
	
	public TreeViewerContentProvider() {
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valutationSet = valuationSetManager.getActiveValuationSet();
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
		
		int i = 0;
		for (ValuationKey vk: _valuations.keySet()) {
			Valuation v = _valuations.get(vk);
			expert = vk.getExpert();
			alternative = vk.getAlternative();
			criterion = vk.getCriterion();
			domain = v.getDomain();
			
			_information[i][0] = expert.getId();
			_information[i][1] = alternative.getId();
			_information[i][2] = criterion.getId();
			_information[i][3] = domain.getId();
			_information[i][4] = v;
			
			i++;
		}
		return _information;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
