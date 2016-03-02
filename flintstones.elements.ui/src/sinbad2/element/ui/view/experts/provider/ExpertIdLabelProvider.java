package sinbad2.element.ui.view.experts.provider;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.expert.Expert;
import sinbad2.element.ui.Images;


public class ExpertIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj){
		return ((Expert) obj).getId();
	}

	@Override
	public Image getImage(Object obj){
		if(((Expert) obj).hasChildren()) {
			return Images.GroupOfExperts;
		} else {
			return Images.Expert;
		}
	}

}
