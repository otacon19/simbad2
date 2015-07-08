package sinbad2.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.domain.listener.IDomainSetChangeListener;

public class DomainsManager {
	
	private final String EXTENSION_POINT = "flintstones.domain"; //$NON-NLS-1$;
	
	private static DomainsManager _instance = null;
	
	private DomainSet _activeDomainSet;
	private Map<String, DomainRegistryExtension> _registers;
	private Map<String, Domain> _domains;
	
	private List<IDomainSetChangeListener> _listeners;
	
	private DomainsManager() {
		_activeDomainSet = null;
		_registers = new HashMap<String, DomainRegistryExtension>();
		_domains = new HashMap<String, Domain>();
		loadRegistersExtension();
		
		_listeners = new LinkedList<IDomainSetChangeListener>();
	}
	
	public static DomainsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainsManager();
		}
		
		return _instance;
	}
	
	public DomainSet getActiveDomainSet() {
		return _activeDomainSet;
	}
	
	public void setActiveDomainSet(DomainSet domainSet) {
		
		if(_activeDomainSet != domainSet) {
			_activeDomainSet = domainSet;
			notifyDomainSetChangeListener();
		}
	}
	
	private void loadRegistersExtension() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		DomainRegistryExtension registry;
		for(IConfigurationElement extension: extensions) {
			registry = new DomainRegistryExtension(extension);
			_registers.put(registry.getAttribute(EDomainElements.id), registry);
		}
	}
	
	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}
	
	public DomainRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}
	
	public Domain getDomainImplementation(String id) {
		
		if(_domains.containsKey(id)) {
			return _domains.get(id);
		} else {
			return initializeDomain(id);
		}
	}
	
	public Domain copyDomain(String id) {
		Domain result = getDomainImplementation(id);
		
		if(result != null) {
			result = (Domain) result.clone();
		}
		
		return result;
	}
	
	public void registerDomainSetChangeListener(IDomainSetChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterDomainSetChangeListener(IDomainSetChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyDomainSetChangeListener() {
		
		for(IDomainSetChangeListener listener: _listeners) {
			listener.notifyNewActiveDomainSet(_activeDomainSet);
		}
	}
	
	private Domain initializeDomain(String id) {
		
		DomainRegistryExtension domainRegistry = getRegistry(id);
		IConfigurationElement type = domainRegistry.getConfiguration().getChildren(EDomainElements.type.toString())[0];
		IConfigurationElement typeImplementation = type.getChildren()[0];
		
		try {
			Domain domain = (Domain) typeImplementation.createExecutableExtension(EDomainElements.implementation.toString());
			domain.setType(id);
			domain.setName(domainRegistry.getAttribute(EDomainElements.name));
			domain.setRegistry(domainRegistry);
			
			_domains.put(id, domain);
			
			return domain;
			
		} catch (CoreException e) {
			e.printStackTrace();
			
			return null;
		}	
	}
}
