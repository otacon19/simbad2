package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.emergency.computeweights.ui.Images;

public class CriterionIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Criterion) obj).getId();
	}
	
	@Override
	public Image getImage(Object obj) {
		if(((Criterion) obj).hasSubcriteria()) {
			return Images.Criteria;
		} else {
			return Images.Criterion;
		}
	}
}
