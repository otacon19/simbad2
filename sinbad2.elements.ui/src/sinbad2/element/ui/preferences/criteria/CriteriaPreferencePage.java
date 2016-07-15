package sinbad2.element.ui.preferences.criteria;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import sinbad2.element.ui.Activator;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.preferences.PreferenceConstants;

public class CriteriaPreferencePage extends FieldEditorPreferencePage implements 
	IWorkbenchPreferencePage {
	
	public CriteriaPreferencePage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.CriteriaPreferencePage_Criteria_preferences);
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_CRITERION_MEMBER_AS_DEFAULT, 
				Messages.CriteriaPreferencePage_Assign_new_criterion_as_subcriterion, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_CRITERION_BENEFIT_AS_DEFAULT,
				Messages.CriteriaPreferencePage_Benefit_criterion_as_default, getFieldEditorParent()));
		
	}
	
	

}
	