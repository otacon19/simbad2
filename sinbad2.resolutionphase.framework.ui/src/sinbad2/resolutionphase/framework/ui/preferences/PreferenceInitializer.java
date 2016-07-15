package sinbad2.resolutionphase.framework.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import sinbad2.resolutionphase.framework.ui.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		@SuppressWarnings("unused")
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();	
	}

}
