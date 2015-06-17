package sinbad2.element.ui.preferences.experts;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.element.ui.Activator;
import sinbad2.element.ui.preferences.PreferenceConstants;

public class ExpertsPreferencePage extends FieldEditorPreferencePage implements 
	IWorkbenchPreferencePage {
	
	public ExpertsPreferencePage() {
		super(GRID);
	}
		
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Experts preferences");	
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EXPERT_MEMBER_AS_DEFAULT, 
				"&Assign new experts as members when an expert is selected", getFieldEditorParent()));	
	}

}
