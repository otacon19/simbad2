package sinbad2.element.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import sinbad2.element.ui.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CRITERION_BENEFIT_AS_DEFAULT, true);
		store.setDefault(PreferenceConstants.P_CRITERION_MEMBER_AS_DEFAULT, true);
		store.setDefault(PreferenceConstants.P_EXPERT_MEMBER_AS_DEFAULT, true);	
	}
	
}
