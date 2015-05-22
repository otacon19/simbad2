package sinbad2.resolutionscheme;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ResolutionSchemesManager {

	private final String EXTENSION_POINT = "flintstones.resolutionscheme"; //$NON-NLS-1$

	private static ResolutionSchemesManager _instance = null;

	private ResolutionScheme _activeResolutionScheme;

	private Map<String, ResolutionSchemeRegistryExtension> _registers;
	private Map<String, ResolutionScheme> _resolutionSchemes;
	private Map<ResolutionSchemeImplementation, String> _implementationResolutionSchemes;

	private ResolutionSchemesManager() {
		_activeResolutionScheme = null;
		_registers = new HashMap<String, ResolutionSchemeRegistryExtension>();
		_resolutionSchemes = new HashMap<String, ResolutionScheme>();
		_implementationResolutionSchemes = new HashMap<ResolutionSchemeImplementation, String>();
		loadRegisters();
	}

	public static ResolutionSchemesManager getInstance() {
		if (_instance == null) {
			_instance = new ResolutionSchemesManager();
		}
		return _instance;
	}

	private void loadRegisters() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(EXTENSION_POINT);

		ResolutionSchemeRegistryExtension registry;
		for (IConfigurationElement extension : extensions) {
			registry = new ResolutionSchemeRegistryExtension(extension);
			_registers.put(registry.getElement(EResolutionSchemeElements.id),
					registry);
		}
	}

	public String[] getIDs() {
		return _registers.keySet().toArray(new String[0]);
	}

	public ResolutionSchemeRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}

	public void setImplementationResolutionScheme(
			ResolutionSchemeImplementation implementation,
			String resolutionSchemeId) {
		_implementationResolutionSchemes
				.put(implementation, resolutionSchemeId);
	}

	public ResolutionScheme getImplementationResolutionScheme(
			ResolutionSchemeImplementation implementation) {
		ResolutionScheme result = null;

		String id = _implementationResolutionSchemes.get(implementation);

		if (id != null) {
			result = _resolutionSchemes.get(id);
		}

		return result;
	}

	public ResolutionScheme getResolutionScheme(String id) {

		if (_resolutionSchemes.containsKey(id)) {
			return _resolutionSchemes.get(id);
		} else {
			try {
				return initializeResolutionScheme(id);
			} catch (Exception e) {
				return null;
			}
		}
	}

	public ResolutionScheme getActiveResolutionScheme() {
		return _activeResolutionScheme;
	}

	public void deactiveCurrentActive() {
		if (_activeResolutionScheme != null) {
			_activeResolutionScheme.deactivate();
			_activeResolutionScheme = null;
		}
	}

	public void activate(String id) {
		boolean needActivate = true;
		if (_activeResolutionScheme != null) {
			if (!_activeResolutionScheme.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}

		if (needActivate) {
			_activeResolutionScheme = getResolutionScheme(id);
			if (_activeResolutionScheme != null) {
				_activeResolutionScheme.activate();
			}
		}
	}
	
	private ResolutionScheme initializeResolutionScheme(String id) {

		ResolutionSchemeRegistryExtension resolutionSchemeRegistry = getRegistry(id);
		ResolutionScheme resolutionScheme = new ResolutionScheme();
		resolutionScheme.setId(id);
		resolutionScheme.setName(resolutionSchemeRegistry
				.getElement(EResolutionSchemeElements.name));
		resolutionScheme.setRegistry(resolutionSchemeRegistry);

		_resolutionSchemes.put(id, resolutionScheme);

		return resolutionScheme;

	}
}
