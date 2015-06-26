package sinbad2.valuation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.domain.valuations.DomainValuationsManager;


public class ValuationsManager {
	
	private final static String EXTENSION_POINT = "flintstones.valuation"; //$NON-NLS-1$
	
	private static ValuationsManager _instance = null;
	
	private Map<String, ValuationRegistryExtension> _registers;
	private Map<String, Valuation> _valuations;
	
	private ValuationsManager() {
		_registers = new HashMap<String, ValuationRegistryExtension>();
		_valuations = new HashMap<String, Valuation>();
		
		loadRegisters();
	}
	
	public static ValuationsManager getInstance() {
		
		if(_instance == null) {
			_instance = new ValuationsManager();
		}
		
		return _instance;
	}
	
	public String[] getRegistersIDs() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public ValuationRegistryExtension getRegistry(String id) {
		
		return _registers.get(id);
	}
	
	public Valuation getValuationImplementation(String id) {
		if(_valuations.containsKey(id)) {
			return _valuations.get(id);
		} else {
			return initializeValuation(id);
		}
	}
	
	public Valuation copyValuation(String id) {
		Valuation result = getValuationImplementation(id);
		
		if(result != null) {
			result = (Valuation) result.clone();
		}
		
		return result;
	}
	
	private Valuation initializeValuation(String id) {
		ValuationRegistryExtension valuationRegistry = getRegistry(id);

		IConfigurationElement type = valuationRegistry.getConfiguration().getChildren(EValuationElements.type.toString())[0];
		
		try {
			Valuation valuation = (Valuation) type.createExecutableExtension(EValuationElements.implementation.toString());
			
			valuation.setId(id);
			valuation.setName(valuationRegistry.getElement(EValuationElements.name));
			valuation.setRegistry(valuationRegistry);
			valuation.setDomainExtensionId(valuationRegistry.getElement(EValuationElements.domain));

			DomainValuationsManager domainValuationsManager = DomainValuationsManager.getInstance();
			domainValuationsManager.addValuation(valuation.getId(), valuation.getName());
			
			domainValuationsManager.addSupportedValuationForDomain(valuation.getId(), valuation.getDomainExtensionId());

			_valuations.put(id, valuation);
			
	
			return valuation;
			
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void loadRegisters() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		ValuationRegistryExtension registry;
		String id;
		
		for(IConfigurationElement extension: extensions) {
			registry = new ValuationRegistryExtension(extension);
			id = registry.getElement(EValuationElements.id); 
			_registers.put(id, registry);
		}
	}

}
