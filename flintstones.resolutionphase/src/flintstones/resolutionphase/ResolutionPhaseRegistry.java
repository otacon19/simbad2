package flintstones.resolutionphase;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * ResolutionPhaseRegistry.java
 * 
 * Clase que registra las distintas fases que se vayan a introducir. Para esto usamos un atributo de tipo IConfigurationElement
 * que nos permite acceder a los hijos a atributos de las extensiones
 * 
 * @author Labella Romero, Álvaro
 *
 */
public class ResolutionPhaseRegistry implements Cloneable {

	private IConfigurationElement _configuration;
	
	private ResolutionPhaseRegistry() {
		_configuration = null;
	}
	
	public ResolutionPhaseRegistry(IConfigurationElement element) {
		this();
		_configuration = element;
	}
	
	public IConfigurationElement getConfiguration() {
		return _configuration;
	}
	
	public String getElement(EResolutionPhaseElements element) {
		
		String result = null;
		
		if(_configuration != null) {
			result = _configuration.getAttribute(element.toString());
		}
		
		return result;
	}
	
	//TODO hash
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		ResolutionPhaseRegistry result = null;
		
		result = (ResolutionPhaseRegistry) super.clone();
		result._configuration = this._configuration;
		
		return result;
		
	}
}
