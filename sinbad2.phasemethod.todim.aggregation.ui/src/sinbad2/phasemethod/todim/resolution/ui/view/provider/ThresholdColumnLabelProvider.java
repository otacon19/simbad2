package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ThresholdColumnLabelProvider extends ColumnLabelProvider {

	private final Color lightGreen = new Color(null, new RGB(230, 255, 230));
	private final Color lightRed = new Color(null, new RGB(255, 230, 230));
	
	@Override
	public String getText(Object element) {
		if (element instanceof Object[]) {
			return (String) ((Object[]) element)[6];
		} else {
			return null;
		}
	}
	
	@Override
	public Color getBackground(Object element) {
		if (element instanceof Object[]) {
			double threshold = Double.parseDouble((String) ((Object[]) element)[6]);
			double distance = Double.parseDouble((String) ((Object[]) element)[5]);
			if(threshold - distance > 0) {
				return lightRed;
			} else {
				return lightGreen;
			}
		}
		
		return null;
	}
	
}
