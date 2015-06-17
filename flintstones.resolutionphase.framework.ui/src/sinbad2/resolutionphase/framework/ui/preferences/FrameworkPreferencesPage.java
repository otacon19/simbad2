package sinbad2.resolutionphase.framework.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.resolutionphase.framework.ui.Activator;

public class FrameworkPreferencesPage extends FieldEditorPreferencePage 
	implements IWorkbenchPreferencePage {
	
	public FrameworkPreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Framework preferences");
		
	}

	@Override
	protected void createFieldEditors() {
		
	}


}
