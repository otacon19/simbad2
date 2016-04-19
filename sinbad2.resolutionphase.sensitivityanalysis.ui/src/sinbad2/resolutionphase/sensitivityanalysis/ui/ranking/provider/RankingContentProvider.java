package sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingView;

public class RankingContentProvider implements IStructuredContentProvider {	
	
	private RankingView _rankingView;
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
	
	public RankingContentProvider(SensitivityAnalysis sensitivityAnalysis, RankingView rankingView) {
		_rankingView = rankingView;
		_sensitivityAnalysis = sensitivityAnalysis;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput,Object newInput) {}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object inputElement) {
		int model = _rankingView.getModel();
		
		Object[] result = null;
		List<MyElement> elements = new LinkedList<MyElement>();
		MyElement element = null;
		if(model == 0 || model == 2) {
			for (int i = 0; i < _sensitivityAnalysis.getNumAlternatives(); i++) {
				element = new MyElement();
				element.alternative = _sensitivityAnalysis.getAlternativesIds()[i];
				element.ranking = _sensitivityAnalysis.getRanking()[i];
				element.value = Math.round(_sensitivityAnalysis.getAlternativesFinalPreferences()[i] * 100d) / 100d;
				elements.add(element);
			}
		} else if(model == 1){
			for (int i = 0; i < _sensitivityAnalysis.getNumAlternatives(); i++) {
				String ratios = "";
				element = new MyElement();
				element.alternative = _sensitivityAnalysis.getAlternativesIds()[i];
				element.ranking = _sensitivityAnalysis.getRanking()[i];
				for (int j = 0; j < _sensitivityAnalysis.getNumAlternatives(); j++) {
					if(i != j && j > i) {
						double ratio = Math.round(_sensitivityAnalysis.getAlternativesRatioFinalPreferences()[i][j] * 10000d) / 10000d;
						ratios += "A" + (j + 1) + ": " + Double.toString(ratio);
						if(j != _sensitivityAnalysis.getNumAlternatives() - 1) {
							ratios += ", ";
						}
					}
				}
				element.value = ratios;
				elements.add(element);
			}
		}
		
		Collections.sort(elements);

		result = new Object[elements.size()];
		int pos = 0;
		for (MyElement e : elements) {
			if(model == 0) {
				result[pos++] = new Object[] { e.ranking, e.alternative, ((int) ((Double) e.value * 10000d)) / 10000d };
			} else {
				result[pos++] = new Object[] { e.ranking, e.alternative, e.value};
			}
		}

		return result;
	}
}
