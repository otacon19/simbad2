package sinbad2.domain.ui.dialog.newDialog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class NewDomainDialogRegistryExtension extends RegistryExtension {

	public NewDomainDialogRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(ENewDomainDialogElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	public NewDomainDialog newDomainDialog() throws CoreException {
		return (NewDomainDialog) _configuration.createExecutableExtension(ENewDomainDialogElements.implementation.toString());
	}

}
