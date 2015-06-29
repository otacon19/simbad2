package sinbad2.element.ui.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class ElementSourceProvider extends AbstractSourceProvider {
	
	public final static String CAN_BE_MODIFIED_STATE = "flintstones.element.ui.can.be.modified.state"; //$NON-NLS-1$
	public final static String ENABLED = "ENABLED"; //$NON-NLS-1$
	public final static String DISENABLED = "DISENABLED"; //$NON-NLS-1$
	
	private boolean _canBeModified = true;
	
	public ElementSourceProvider() {}
	
	@Override
	public void dispose() {}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> map = new HashMap<String, String>();
		
		String value = _canBeModified ? ENABLED : DISENABLED;
		map.put(CAN_BE_MODIFIED_STATE, value);
		
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { CAN_BE_MODIFIED_STATE };
	}
	
	public void changeCanBeModifiedState(Boolean newState) {
		
		if(_canBeModified != newState) {
			_canBeModified = newState;
			if(_canBeModified) {
				fireSourceChanged(ISources.WORKBENCH, CAN_BE_MODIFIED_STATE, ENABLED);
			} else {
				fireSourceChanged(ISources.WORKBENCH, CAN_BE_MODIFIED_STATE, DISENABLED);
			}
		}
	}

}
