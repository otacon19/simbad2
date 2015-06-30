package sinbad2.resolutionphase.frameworkstructuring.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import sinbad2.resolutionphase.frameworkstructuring.ui.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CRITERIA_TABLE_ALTERNATIVES_IN_ROWS, true);
		store.setDefault(PreferenceConstants.P_ALTERNATIVES_TABLE_EXPERTS_IN_ROWS, true);
		store.setDefault(PreferenceConstants.P_EXPERTS_TABLE_ALTERNATIVES_IN_ROWS, true);
	}

}
