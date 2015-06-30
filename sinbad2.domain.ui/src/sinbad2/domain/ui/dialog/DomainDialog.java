package sinbad2.domain.ui.dialog;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import sinbad2.domain.DomainsManager;
import sinbad2.domain.DomainSet;

public class DomainDialog extends Dialog {
	
	protected Set<String> _ids;
	protected String _id;
	
	protected DomainDialog() {
		super(Display.getCurrent().getActiveShell());
		
		DomainsManager domainManager = DomainsManager.getInstance();
		DomainSet domainSet = domainManager.getActiveDomainSet();
		
		String[] domainsIds = domainSet.getAllDomainsIds();
		_ids = new HashSet<String>();
		for(String id: domainsIds) {
			_ids.add(id);
		}
	}
	
	protected ControlDecoration createNotificationDecorator(Text text) {
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		validate(controlDecoration, ""); //$NON-NLS-1$
		
		return controlDecoration;
	}
	
	protected ControlDecoration createNotificationDecorator(Button button) {
		ControlDecoration controlDecoration = new ControlDecoration(button, SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		controlDecoration.setImage(fieldDecoration.getImage());
		validate(controlDecoration, ""); //$NON-NLS-1$
		
		return controlDecoration;
	}
	
	protected boolean validate(ControlDecoration controlDecoration, String text) {
		controlDecoration.setDescriptionText(text);
		if(text.isEmpty()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.show();
			return false;
			
		}
	}

}
