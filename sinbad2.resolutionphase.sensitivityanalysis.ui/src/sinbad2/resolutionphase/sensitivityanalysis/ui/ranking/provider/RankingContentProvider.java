package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;

public class RankingContentProvider implements IStructuredContentProvider {	

	class MyElement implements Comparable<MyElement> {

		String alternative;
		int ranking;
		double value;

		@Override
		public int compareTo(MyElement other) {
			return ((Integer) ranking).compareTo(other.ranking);
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result = null;

		SensitivityAnalysis sensitivityAnalysis = (SensitivityAnalysis) inputElement;
		List<MyElement> elements = new LinkedList<MyElement>();
		MyElement element;
		for (int i = 0; i < sensitivityAnalysis.getNumAlternatives(); i++) {
			element = new MyElement();
			element.alternative = sensitivityAnalysis.getAlternativesIds()[i];
			element.ranking = sensitivityAnalysis.getRanking()[i];
			element.value = sensitivityAnalysis.getAlternativesFinalPreferences()[i];

			elements.add(element);
		}
		
		Collections.sort(elements);

		result = new Object[elements.size()];
		int pos = 0;
		for (MyElement e : elements) {
			result[pos++] = new Object[] { e.ranking, e.alternative, ((int) (e.value * 10000d)) / 10000d };
		}

		return result;
	}
}
