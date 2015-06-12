package sinbad2.domain.ui.dialog.selectdialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.ui.DomainUIsManager;

public class SelectDomainDialog extends Dialog {
	
	private String _description;
	private List<String> _dialogsIDs;
	
	public SelectDomainDialog(Shell shell, List<String> dialogsIDs) {
		super(shell);
		
		_dialogsIDs = dialogsIDs;
	}
	
	public String getDescription() {
		return _description;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		 Composite container = (Composite) super.createDialogArea(parent);
		 DomainUIsManager domainUIsManager = DomainUIsManager.getInstance();

		 for(String dialogID: _dialogsIDs) {
			 Button button = new Button(container, SWT.RADIO);
			 button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			 
			 String description = domainUIsManager.getDescriptionNewDomainDialog(dialogID);
			 
			 button.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.NONE)); //$NON-NLS-1$
			 button.setText(description);
			 
			 button.addSelectionListener(new SelectionAdapter() {
				 public void widgetSelected(SelectionEvent e) {
					 _description = ((Button)e.getSource()).getText();
				 };
			});
			 
		 }
		 
		 return container;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Build mode");
	}	
	
}
