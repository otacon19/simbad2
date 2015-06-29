package sinbad2.element.ui.sourceprovider.criteria;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class BrothersCriteriaSelectedSourceProvider extends AbstractSourceProvider {
	public final static String BROTHERS_CRITERIA_SELECTED = "flintstones.ui.brothers.criteria.selected.state"; //$NON-NLS-1$
	public final static String ENABLED = "ENABLED"; //$NON-NLS-1$
	public final static String DISENABLED = "DISENABLED"; //$NON-NLS-1$
	
	private boolean _brothers_criteria_selected = true;
	
	public BrothersCriteriaSelectedSourceProvider() {}
	
	@Override
	public void dispose() {}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> map = new HashMap<String, String>();
		
		String value = _brothers_criteria_selected ? ENABLED : DISENABLED;
		map.put(BROTHERS_CRITERIA_SELECTED, value);
		
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { BROTHERS_CRITERIA_SELECTED };
	}
	
	public void toogleState() {
		_brothers_criteria_selected = !_brothers_criteria_selected;
		String value = _brothers_criteria_selected ? ENABLED: DISENABLED;
		fireSourceChanged(ISources.WORKBENCH, BROTHERS_CRITERIA_SELECTED, value);
	}
}
