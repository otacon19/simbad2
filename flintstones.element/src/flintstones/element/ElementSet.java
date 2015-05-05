package flintstones.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flintstones.element.expert.Expert;

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
	
	/**
	 * Constructor clase ElementSet
	 */
	public ElementSet(){
		_experts = new LinkedList<Expert>();
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
	public void setExperts(List<Expert> _experts) {
		//TODO Clase validator
		this._experts = _experts;
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
		
		return result;
		
	}
	
}
