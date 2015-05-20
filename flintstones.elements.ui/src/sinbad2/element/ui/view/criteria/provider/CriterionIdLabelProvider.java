package sinbad2.element.ui.view.criteria.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.Images;

public class CriterionIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Criterion) obj).getId();
	}
	
	@Override
	public Image getImage(Object obj) {
		if(((Criterion) obj).hasSubcriterial()) {
			return Images.Criteria;
		} else {
			return Images.Criterion;
		}
	}
}
