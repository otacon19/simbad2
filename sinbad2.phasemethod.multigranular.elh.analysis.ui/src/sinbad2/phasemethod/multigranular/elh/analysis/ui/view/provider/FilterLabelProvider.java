package sinbad2.phasemethod.multigranular.elh.analysis.ui.view.provider;

import org.eclipse.jface.viewers.LabelProvider;

import sinbad2.element.ProblemElement;

public class FilterLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		String result = ((ProblemElement) obj).getId();
		
		return result;
	}
}
