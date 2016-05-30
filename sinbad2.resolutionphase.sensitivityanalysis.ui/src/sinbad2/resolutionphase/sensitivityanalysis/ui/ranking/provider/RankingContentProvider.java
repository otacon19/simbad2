package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.resolutionphase.sensitivityanalysis.EModel;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;

public class RankingContentProvider implements IStructuredContentProvider {	
	
	private SensitivityAnalysis _sensitivityAnalysis;

	class MyElement implements Comparable<MyElement> {

		String alternative;
		int ranking;
		Object value;

		@Override
		public int compareTo(MyElement other) {
			return ((Integer) ranking).compareTo(other.ranking);
		}
	}
	
	public RankingContentProvider(SensitivityAnalysis sensitivityAnalysis) {
		_sensitivityAnalysis = sensitivityAnalysis;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result = null;
		List<MyElement> elements = new LinkedList<MyElement>();
		MyElement element = null;
		
		EModel model = _sensitivityAnalysis.getModel();
		
		switch(model) {
		case WEIGHTED_SUM:
		case ANALYTIC_HIERARCHY_PROCESS:
			for (int i = 0; i < _sensitivityAnalysis.getNumAlternatives(); i++) {
				element = new MyElement();
				element.alternative = _sensitivityAnalysis.getAlternativesIds()[i];
				element.ranking = _sensitivityAnalysis.getRanking()[i];
				element.value = Math.round(_sensitivityAnalysis.getAlternativesFinalPreferences()[i] * 10000d) / 10000d;
				elements.add(element);
			}
			break;
		case WEIGHTED_PRODUCT:
			for (int i = 0; i < _sensitivityAnalysis.getNumAlternatives(); i++) {
				String ratios = ""; //$NON-NLS-1$
				element = new MyElement();
				element.alternative = _sensitivityAnalysis.getAlternativesIds()[i];
				element.ranking = _sensitivityAnalysis.getRanking()[i];
				for (int j = 0; j < _sensitivityAnalysis.getNumAlternatives(); j++) {
					if(i != j && j > i) {
						double ratio = Math.round(_sensitivityAnalysis.getAlternativesRatioFinalPreferences()[i][j] * 10000d) / 10000d;
						ratios += "A" + (j + 1) + ": " + Double.toString(ratio); //$NON-NLS-1$ //$NON-NLS-2$
						if(j != _sensitivityAnalysis.getNumAlternatives() - 1) {
							ratios += ", "; //$NON-NLS-1$
						}
					}
				}
				element.value = ratios;
				elements.add(element);
			}
			break;
		}
		
		Collections.sort(elements);

		result = new Object[elements.size()];
		int pos = 0;
		for (MyElement e : elements) {
			result[pos++] = new Object[] { e.ranking, e.alternative, e.value};
		}

		return result;
	}
}
