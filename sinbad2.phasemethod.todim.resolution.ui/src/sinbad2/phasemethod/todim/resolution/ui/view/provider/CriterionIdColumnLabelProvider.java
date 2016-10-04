package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;

public class CriterionIdColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((String[]) element)[0];
	}
	 
	@Override
	public Color getForeground(Object element) {
		ResolutionPhase resolutionPhase = (ResolutionPhase) PhasesMethodManager.getInstance().getPhaseMethod(ResolutionPhase.ID).getImplementation();
		
		Map<Criterion, Double> weights = resolutionPhase.getCriteriaWeights();
		Double criterionWeight = Double.parseDouble(((String[]) element)[1]);
		for(Criterion c: weights.keySet()) {
			if(weights.get(c) > criterionWeight) {
				return null;
			}
		}
		
		return new Color(Display.getCurrent(), 255, 0, 0);
	}
}
