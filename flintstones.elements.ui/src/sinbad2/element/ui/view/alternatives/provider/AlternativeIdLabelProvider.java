package sinbad2.element.ui.view.alternatives.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.Images;

public class AlternativeIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Alternative) obj).getId();
	}

	@Override
	public Image getImage(Object element) {
		return Images.Alternative;
	}
}
