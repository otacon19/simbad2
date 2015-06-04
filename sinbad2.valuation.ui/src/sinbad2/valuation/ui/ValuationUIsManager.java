package sinbad2.valuation.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.domain.valuations.DomainValuationsManager;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationUIsManager {
	
	private final String EXTENSION_POINT = "flintstones.valuation.ui";
	
	private static ValuationUIsManager _instance;
	
	private Map<String, ValuationUIRegistryExtension> _registers;
	private Map<String, ValuationUI> _valuationsUIs;
	
	private ValuationUIsManager() {
		_registers = new HashMap<String, ValuationUIRegistryExtension>();
		_valuationsUIs = new HashMap<String, ValuationUI>();
		
		loadRegisters();
	}
	
	public static ValuationUIsManager getInstance() {
	
		if(_instance == null) {
			_instance = new ValuationUIsManager();
		}
		
		return _instance;
	}

	private void loadRegisters() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		Valuation valuation;
		ValuationUIRegistryExtension registry;
		ValuationsManager valuationsManager = ValuationsManager.getInstance();
		
		for(IConfigurationElement extension: extensions) {
			registry = new ValuationUIRegistryExtension(extension);
			_registers.put(registry.getElement(EValuationUIElements.valuation), registry);
			
			IConfigurationElement[] newDomainDialogs = extension.getChildren(EValuationUIElements.new_domain_dialog.toString());
			
			if(newDomainDialogs.length > 0) {
				valuation = valuationsManager.getValuationImplementation(registry.getElement(EValuationUIElements.valuation));
				DomainValuationsManager dvm = DomainValuationsManager.getInstance();
				
				for(IConfigurationElement newDomainDialog: newDomainDialogs) {
					dvm.addValuationNewDomainDialog(valuation.getId(), newDomainDialog.getAttribute(EValuationUIElements.dialog.toString()));
				}
			}
			
			IConfigurationElement[] modifyDomainDialogs = extension.getChildren(EValuationUIElements.modify_domain_dialog.toString());
			
			if(modifyDomainDialogs.length > 0) {
				valuation = valuationsManager.getValuationImplementation(registry.getElement(EValuationUIElements.valuation));
				DomainValuationsManager dvm = DomainValuationsManager.getInstance();
				
				for(IConfigurationElement modifyDomainDialog: modifyDomainDialogs) {
					dvm.addValuationModifyDomainDialog(valuation.getId(), modifyDomainDialog.getAttribute(EValuationUIElements.dialog.toString()));
				}
			}
			
		}
		
	}
	
	public String[] getRegistersIDs() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ValuationUIRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public ValuationUI getValuationUI(String valuation) {
		
		if(_valuationsUIs.containsKey(valuation)) {
			return _valuationsUIs.get(valuation);
		} else {
			return initializeValuationUIRegistry(valuation);
		}
	}

	private ValuationUI initializeValuationUIRegistry(String valuation) {
		
		ValuationUIRegistryExtension valuationUIRegistry = getRegistry(valuation);
		ValuationUI valuationUI = new ValuationUI();
		valuationUI.setRegistry(valuationUIRegistry);
		
		_valuationsUIs.put(valuation, valuationUI);
		
		return valuationUI;
	}
	
	public ValuationPanel newValuationPanel(String valuation) {
		ValuationPanel result = null;
		
		ValuationUI valuationUI = getValuationUI(valuation);
		IConfigurationElement valuationUIConfiguration = valuationUI.getRegistry().getConfiguration();
		
		IConfigurationElement[] valuationPanels = valuationUIConfiguration.getChildren(EValuationUIElements.valuation_panel.toString());
		
		if(valuationPanels != null) {
			if(valuationPanels.length > 0) {
				try {
					result = (ValuationPanel) valuationPanels[0].createExecutableExtension(EValuationUIElements.panel.toString());
				} catch (CoreException e) {
					e.printStackTrace();
					result = null;
				}
			}
		}
		
		return result;
	}
		
	
}
