package sinbad2.element.ui.preferences.experts;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.element.ui.Activator;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.preferences.PreferenceConstants;

public class ExpertsPreferencePage extends FieldEditorPreferencePage implements 
	IWorkbenchPreferencePage {
	
	public ExpertsPreferencePage() {
		super(GRID);
	}
		
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.ExpertsPreferencePage_Experts_preferences);	
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EXPERT_MEMBER_AS_DEFAULT, 
				Messages.ExpertsPreferencePage_Assign_new_experts_as_members, getFieldEditorParent()));	
	}

}
