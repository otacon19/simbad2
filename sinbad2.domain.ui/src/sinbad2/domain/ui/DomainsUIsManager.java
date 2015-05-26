package sinbad2.domain.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.modifyDialog.EModifyDomainDialogElements;
import sinbad2.domain.ui.dialog.modifyDialog.ModifyDomainDialog;
import sinbad2.domain.ui.dialog.modifyDialog.ModifyDomainDialogRegistryExtension;
import sinbad2.domain.ui.dialog.newDialog.ENewDomainDialogElements;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialogRegistryExtension;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class DomainsUIsManager {

	private final String EXTENSION_POINT = "flintstones.domain.ui";
	private final String NEW_DOMAIN_DIALOG_EXTENSION_POINT = "flintstones.domain.ui.new.dialog";
	private final String MODIFY_DOMAIN_DIALOG_EXTENSION_POINT = "flintstones.domain.ui.modify.dialog";
	
	private static DomainsUIsManager _instance = null;
	
	private Map<String, DomainUIRegistryExtension> _registers;
	private Map<String, DomainUI> _domainUIs;
	private Map<String, NewDomainDialogRegistryExtension> _newDomainDialogRegisters;
	private Map<String, ModifyDomainDialogRegistryExtension> _modifyDomainDialogRegisters;
	
	private DomainsUIsManager() {
		_registers = new HashMap<String, DomainUIRegistryExtension>();
		_domainUIs = new HashMap<String, DomainUI>();
		_newDomainDialogRegisters = new HashMap<String, NewDomainDialogRegistryExtension>();
		_modifyDomainDialogRegisters = new HashMap<String, ModifyDomainDialogRegistryExtension>();
		
		loadRegisters();
	}
	
	public static DomainsUIsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainsUIsManager();
		}
		
		return _instance;
	}
	
	public String[] getRegistersIDs() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public DomainUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public DomainUI getDomainUI(String domain) {
		
		if(_domainUIs.containsKey(domain)) {
			return _domainUIs.get(domain);
		} else {
			return initializeDomainUI(domain);
		}
	}
	
	public DomainChart newDomainChart(Domain domain) {
		DomainChart result = null;
		
		DomainUI domainUI = getDomainUI(domain.getType());
		try {
			result = (DomainChart) domainUI.getRegistry().getConfiguration().createExecutableExtension(EDomainUIElements.chart.toString());
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	public NewDomainDialog newDomainDialog(Domain domain, String newDomainDialogId) {
		NewDomainDialog result = null;
		
		NewDomainDialogRegistryExtension registry = _newDomainDialogRegisters.get(newDomainDialogId);
		try {
			result = registry.newDomainDialog();
			result.setDomain(domain);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	public ModifyDomainDialog modifyDomainDialog(Domain domain, String modifyDomainDialogId) {
		ModifyDomainDialog result = null;
		
		ModifyDomainDialogRegistryExtension registry = _modifyDomainDialogRegisters.get(modifyDomainDialogId);
		try {
			result = registry.modifyDomainDialog();
			result.setDomain(domain);
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	private DomainUI initializeDomainUI(String domain) {
		
		try {
			DomainUI domainUI = new DomainUI();
			DomainUIRegistryExtension domainUIRegistry = getRegistry(domain);
			
			domainUI.setDomain(domain);
			
			String plugin = domainUIRegistry.getConfiguration().getContributor().getName();
			String pathIcon = domainUIRegistry.getElement(EDomainUIElements.icon);
			
			domainUI.setIcon(plugin, pathIcon);
			domainUI.setRegistry(domainUIRegistry);
			
			_domainUIs.put(domain, domainUI);
			
			return domainUI;
		} catch (Exception e) {
			return null;
		}
	}
	
	private void loadRegisters() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		DomainUIRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new DomainUIRegistryExtension(extension);
			_registers.put(registry.getElement(EDomainUIElements.domain), registry);
		}
		
		loadRegistersNewDomainDialog(reg);
		loadRegistersModifyDomainDialog(reg);
	}
	
	private void loadRegistersNewDomainDialog(IExtensionRegistry reg) {
		
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(NEW_DOMAIN_DIALOG_EXTENSION_POINT);
		
		NewDomainDialogRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new NewDomainDialogRegistryExtension(extension);
			_newDomainDialogRegisters.put(registry.getElement(ENewDomainDialogElements.id), registry);
		}
		
	}
	
	private void loadRegistersModifyDomainDialog(IExtensionRegistry reg) {
		
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(MODIFY_DOMAIN_DIALOG_EXTENSION_POINT);
		
		ModifyDomainDialogRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new ModifyDomainDialogRegistryExtension(extension);
			_modifyDomainDialogRegisters.put(registry.getElement(EModifyDomainDialogElements.id), registry);
		}
		
	}
}
