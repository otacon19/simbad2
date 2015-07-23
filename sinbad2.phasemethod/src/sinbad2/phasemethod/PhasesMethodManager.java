package sinbad2.phasemethod;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;


public class PhasesMethodManager {
	private final String EXTENSION_POINT = "flintstones.phasemethod"; //$NON-NLS-1$

	private static PhasesMethodManager _instance = null;

	private PhaseMethod _activePhaseMethod;

	private Map<String, PhaseMethodRegistryExtension> _registers;
	private Map<String, PhaseMethod> _phasesMethod;
	private Map<IPhaseMethod, String> _implementationsPhasesMethod;

	private PhasesMethodManager() {
		_activePhaseMethod = null;

		_registers = new HashMap<String, PhaseMethodRegistryExtension>();
		_phasesMethod = new HashMap<String, PhaseMethod>();
		_implementationsPhasesMethod = new HashMap<IPhaseMethod, String>();
		loadRegisters();
	}

	private void loadRegisters() {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(EXTENSION_POINT);

		PhaseMethodRegistryExtension registry;
		for (IConfigurationElement extension : extensions) {
			registry = new PhaseMethodRegistryExtension(extension);
			_registers.put(registry.getAttribute(EPhaseMethodElements.id),
					registry);
		}

	}
	
	public static PhasesMethodManager getInstance() {
		if (_instance == null) {
			_instance = new PhasesMethodManager();
		}
		return _instance;
	}

	public String[] getIDs() {
		return _registers.keySet().toArray(new String[0]);
	}

	public PhaseMethodRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}

	public void setImplementationResolutionPhase(
			IPhaseMethod implementation, String resolutionPhaseId) {
		_implementationsPhasesMethod.put(implementation, resolutionPhaseId);
	}

	public PhaseMethod getImplementationResolutionPhase(
			IPhaseMethod implementation) {
		PhaseMethod result = null;

		String id = _implementationsPhasesMethod.get(implementation);

		if (id != null) {
			result = _phasesMethod.get(id);
		}

		return result;
	}

	public PhaseMethod getResolutionPhase(String id) {

		if (_phasesMethod.containsKey(id)) {
			return _phasesMethod.get(id);

		} else {
			try {

				PhaseMethodRegistryExtension resolutionPhaseRegistry = getRegistry(id);

				PhaseMethod resolutionPhase = new PhaseMethod();
				resolutionPhase.setId(id);
				resolutionPhase.setName(resolutionPhaseRegistry.getAttribute(EPhaseMethodElements.name));
				resolutionPhase.setRegistryExtension(resolutionPhaseRegistry);

				_phasesMethod.put(id, resolutionPhase);

				return resolutionPhase;

			} catch (Exception e) {
				return null;
			}
		}

	}

	public PhaseMethod getActiveResolutionPhase() {
		return _activePhaseMethod;
	}

	public void deactivateCurrentActive() {
		if (_activePhaseMethod != null) {
			_activePhaseMethod.deactivate();
			_activePhaseMethod = null;
		}
	}

	public void activate(String id) {

		boolean needActivate = true;
		if (_activePhaseMethod != null) {
			if (!_activePhaseMethod.getId().equals(id)) {
				deactivateCurrentActive();
			} else {
				needActivate = false;
			}

		}

		if (needActivate) {
			_activePhaseMethod = getResolutionPhase(id);
			if (_activePhaseMethod != null) {
				_activePhaseMethod.activate();
			}
		}
	}
}
