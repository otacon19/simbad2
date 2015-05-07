package flintstones.resolutionphase.state;

/**
 * ResolutionPhaseStateChangeEvent.java
 * 
 * Clase que almacena los estados en los que se puede encontrar la fase
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ResolutionPhaseStateChangeEvent {
	
	private EResolutionPhaseStateChanges _change;
	
	private ResolutionPhaseStateChangeEvent(){}
	
	/**
	 * Constructor de la clase ResolutionPhaseStateChangeEvent
	 * @param change estado de la fase
	 */
	public ResolutionPhaseStateChangeEvent(EResolutionPhaseStateChanges change) {
		this();
		_change = change;
	}
	
	/**
	 * Método get del atributo _change
	 * @return estado de la fase
	 */
	public EResolutionPhaseStateChanges getChange() {
		return _change;
	}
	

}
