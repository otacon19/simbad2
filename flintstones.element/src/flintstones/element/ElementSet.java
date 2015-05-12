package flintstones.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flintstones.element.expert.Expert;
import flintstones.element.expert.listener.EExpertsChange;
import flintstones.element.expert.listener.ExpertsChangeEvent;
import flintstones.element.expert.listener.IExpertsChangeListener;


/**
 * ElementSet.java
 * 
 * Clase que define el conjunto total de elementos del problema
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ElementSet implements Cloneable {
	
	private List<Expert> _experts;
	
	private List<IExpertsChangeListener> _expertsListener;
	
	/**
	 * Constructor clase ElementSet
	 */
	public ElementSet(){
		_experts = new LinkedList<Expert>();
		
		_expertsListener = new LinkedList<IExpertsChangeListener>();
	}

	/**
	 * Método get de la clase ElementSet
	 * @return lista de expertos
	 */
	public List<Expert> getExperts() {
		return _experts;
	}
	
	/**
	 * Método set de la clase ElementSet
	 * @param _experts lista de expertos a asignar
	 */
	public void setExperts(List<Expert> experts) {
		//TODO Clase validator
		_experts = experts;
		
		notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
			
	}

	/**
	 * Método que inserta un experto a la lista de expertos
	 * @param expert experto que se va a introducir
	 */
	public void insertExpert(Expert expert) {
		_experts.add(expert);
		Collections.sort(_experts);
		
	}
	
	/**
	 * Método que registra un observador para poder recibir eventos
	 * @param listener observador
	 */
	public void registerExpertsChangesListener(IExpertsChangeListener listener) {
		_expertsListener.add(listener);
	}
	
	/**
	 * Método que elimina el observador de la lista de observadores
	 * @param listener observador
	 */
	public void unregisterExpertsChangeListener(IExpertsChangeListener listener) {
		_expertsListener.remove(listener);
	}
	
	public void notifyExpertsChanges(ExpertsChangeEvent event) {
		for(IExpertsChangeListener listener: _expertsListener) {
			listener.notifyExpertsChange(event);
		}
		
		//TODO workspace
	}
	
	public void clear() {
		if(!_experts.isEmpty()) {
			_experts.clear();
			notifyExpertsChanges(new ExpertsChangeEvent(EExpertsChange.EXPERTS_CHANGES, null, _experts));
		}
	}
	
	/**
	 * Método que clona objetos de la clase ElementSet
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		ElementSet result = null;
		
		result = (ElementSet) super.clone();
		
		result._experts = new LinkedList<Expert>();
		for(Expert expert: _experts){
			result._experts.add((Expert) expert.clone());
		}
		
		result._expertsListener = new LinkedList<IExpertsChangeListener>();
		for(IExpertsChangeListener listener: _expertsListener) {
			result._expertsListener.add(listener);
		}
		
		return result;
		
	}
	
}
