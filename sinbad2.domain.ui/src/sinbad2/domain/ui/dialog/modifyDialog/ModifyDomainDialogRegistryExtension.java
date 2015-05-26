package sinbad2.domain.ui.dialog.modifyDialog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import sinbad2.core.registry.RegistryExtension;

public class ModifyDomainDialogRegistryExtension extends RegistryExtension{

	public ModifyDomainDialogRegistryExtension(IConfigurationElement element) {
		super(element);
	}
	
	public String getElement(EModifyDomainDialogElements element) {
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	public ModifyDomainDialog modifyDomainDialog() throws CoreException {
		return (ModifyDomainDialog) _configuration.createExecutableExtension(EModifyDomainDialogElements.implementation.toString());
	}

}
