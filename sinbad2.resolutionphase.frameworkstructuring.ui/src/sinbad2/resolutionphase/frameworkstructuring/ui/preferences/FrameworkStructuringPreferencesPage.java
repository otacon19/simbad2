package sinbad2.resolutionphase.frameworkstructuring.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.resolutionphase.frameworkstructuring.ui.Activator;
import sinbad2.resolutionphase.frameworkstructuring.ui.nls.Messages;


public class FrameworkStructuringPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FrameworkStructuringPreferencesPage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.FrameworkStructuringPreferencesPage_Framework_structuring_preferences);	
		
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EXPERTS_TABLE_ALTERNATIVES_IN_ROWS, 
				Messages.FrameworkStructuringPreferencesPage_In_expert_domain_assignments_table_in_rows, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CRITERIA_TABLE_ALTERNATIVES_IN_ROWS, 
				Messages.FrameworkStructuringPreferencesPage_In_criterion_domain_assignments_table_show_alternatives_in_rows, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_ALTERNATIVES_TABLE_EXPERTS_IN_ROWS, 
				Messages.FrameworkStructuringPreferencesPage_In_alternative_domain_assignments_table_show_experts_in_row, getFieldEditorParent()));
	}

}
