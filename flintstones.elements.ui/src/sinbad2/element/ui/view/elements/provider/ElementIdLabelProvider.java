package sinbad2.element.ui.view.elements.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.ProblemElement;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.element.ui.Images;

public class ElementIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((ProblemElement) element).getId();
	}
	
	@Override
	public Image getImage(Object element) {
		
		if(element instanceof Expert) {
			if(((Expert) element).hasChildrens()) {
				return Images.GroupOfExperts;
			} else {
				return Images.Expert;
			}
		} else if(element instanceof Alternative) {
			return Images.Alternative;
		} else if(element instanceof Criterion) {
			if(((Criterion) element).hasSubcriteria()) {
				return Images.Criteria;
			} else {
				return Images.Criterion;
			}
		} else {
			return null;
		}
	}
}
