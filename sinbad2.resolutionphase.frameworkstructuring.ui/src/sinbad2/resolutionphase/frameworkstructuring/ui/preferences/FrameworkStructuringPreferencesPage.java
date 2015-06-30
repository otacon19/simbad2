package sinbad2.resolutionphase.frameworkstructuring.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.resolutionphase.frameworkstructuring.ui.Activator;


public class FrameworkStructuringPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FrameworkStructuringPreferencesPage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Framework structuring preferences");	
		
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EXPERTS_TABLE_ALTERNATIVES_IN_ROWS, 
				"In expert domain assignments table show alternatives in rows ", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CRITERIA_TABLE_ALTERNATIVES_IN_ROWS, 
				"In criterion domain assignments table show alternatives in rows", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALTERNATIVES_TABLE_EXPERTS_IN_ROWS, 
				"In alternative domain assignments table show experts in row", getFieldEditorParent()));
	}

}
