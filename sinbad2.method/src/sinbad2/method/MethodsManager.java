package sinbad2.method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.aggregationoperator.EAggregationOperatorType;

public class MethodsManager {
	
	private final String EXTENSION_POINT = "flintstones.method"; //$NON-NLS-1$

	private static MethodsManager _instance = null;

	private Method _activeMethod;

	private Map<String, MethodRegistryExtension> _registers;
	private Map<String, Method> _methods;
	private Map<MethodImplementation, String> _implementationMethods;

	private MethodsManager() {
		_activeMethod = null;
		_registers = new HashMap<String, MethodRegistryExtension>();
		_methods = new HashMap<String, Method>();
		_implementationMethods = new HashMap<MethodImplementation, String>();
		loadRegistersExtension();
	}

	public static MethodsManager getInstance() {
		if (_instance == null) {
			_instance = new MethodsManager();
		}
		return _instance;
	}

	private void loadRegistersExtension() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);

		MethodRegistryExtension registry;
		for (IConfigurationElement extension : extensions) {
			registry = new MethodRegistryExtension(extension);
			_registers.put(registry.getElement(EMethodElements.id), registry);
		}
		
		IConfigurationElement[] supportedTypesConfiguration = extensions[0].getChildren(EMethodElements.aggregation_supported.toString());
		Set<EAggregationOperatorType> supportedTypes = new HashSet<EAggregationOperatorType>();
		for(IConfigurationElement type: supportedTypesConfiguration) {
			supportedTypes.add(EAggregationOperatorType.valueOf(type.getAttribute(EMethodElements.type.toString())));
		}
	}

	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}

	public MethodRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}

	public void setImplementationMethod(MethodImplementation implementation, String methodId) {
		_implementationMethods.put(implementation, methodId);
	}

	public Method getImplementationMethod(MethodImplementation implementation) {
		Method result = null;

		String id = _implementationMethods.get(implementation);

		if (id != null) {
			result = _methods.get(id);
		}

		return result;
	}

	public Method getMethod(String id) {

		if (_methods.containsKey(id)) {
			return _methods.get(id);
		} else {
			try {
				return initializeMethod(id);
			} catch (Exception e) {
				return null;
			}
		}
	}

	public Method getActiveMethod() {
		return _activeMethod;
	}

	public void deactiveCurrentActive() {
		if (_activeMethod != null) {
			_activeMethod.deactivate();
			_activeMethod = null;
		}
	}

	public void activate(String id) {
		boolean needActivate = true;
		if (_activeMethod != null) {
			if (!_activeMethod.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}

		if (needActivate) {
			_activeMethod = getMethod(id);
			if (_activeMethod != null) {
				_activeMethod.activate();
			}
		}
	}
	
	private Method initializeMethod(String id) {
		MethodRegistryExtension methodRegistry = getRegistry(id);
		Method method = new Method();
		method.setId(id);
		method.setName(methodRegistry.getElement(EMethodElements.name));
		method.setCategory(methodRegistry.getElement(EMethodElements.category));
		method.setDescription(methodRegistry.getElement(EMethodElements.description));
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		IConfigurationElement[] supportedTypesConfiguration = extensions[0].getChildren(EMethodElements.aggregation_supported.toString());
		EAggregationOperatorType supportedType = EAggregationOperatorType.valueOf(supportedTypesConfiguration[0].getAttribute(EMethodElements.type.toString()));
		method.setAggregationTypeSupported(supportedType);
		
		method.setRegistry(methodRegistry);

		_methods.put(id, method);

		return method;

	}
	
}
