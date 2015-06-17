package sinbad2.element.ui.sourceprovider.experts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class BrothersExpertsSelectedSourceProvider extends AbstractSourceProvider {
	public final static String BROTHERS_EXPERTS_SELECTED = "flintstones.ui.brothers.experts.selected.state";
	public final static String ENABLED = "ENABLED";
	public final static String DISENABLED = "DISENABLED";
	
	private boolean _brothers_experts_selected = true;
	
	public BrothersExpertsSelectedSourceProvider() {}
	
	@Override
	public void dispose() {}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> map = new HashMap<String, String>();
		
		String value = _brothers_experts_selected ? ENABLED : DISENABLED;
		map.put(BROTHERS_EXPERTS_SELECTED, value);
		
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { BROTHERS_EXPERTS_SELECTED };
	}
	
	public void toogleState() {
		_brothers_experts_selected = !_brothers_experts_selected;
		String value = _brothers_experts_selected ? ENABLED: DISENABLED;
		fireSourceChanged(ISources.WORKBENCH, BROTHERS_EXPERTS_SELECTED, value);
	}
}
