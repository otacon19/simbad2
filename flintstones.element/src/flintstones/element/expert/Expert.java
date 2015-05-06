package flintstones.element.expert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flintstones.element.ProblemElement;

/**
 * Expert.java
 * 
 * Clase que define a un elemento 'experto'
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class Expert extends ProblemElement {
	
	private List<Expert> _members = null;
	private Expert _parent = null;
	
	/**
	 * Constructor de la clase Expert
	 */
	public Expert() {
		super();
	}
	
	/**
	 * Constructor de la clase Expert
	 */
	public Expert(String id) {
		super(id);
	}
	
	/**
	 * Método que crea muestra de forma jerárquica la estructura de los expertos (recursivo).
	 */
	@Override
	public String getFormatId(){
		String result = null;
		
		if(_parent != null) {
			result = _parent.getFormatId() + ":" + _id; //$NON-NLS-1$
		} else {
			result = _id;
		}
		
		return result;
	}
	
	/**
	 * Método set del atributo _parent
	 * @param parent
	 */
	public void setParent(Expert parent) {
		_parent = parent;
	}
	
	/**
	 * Método get del atributo _parent
	 * @return padre del nodo experto actual
	 */
	public Expert getParent() {
		return _parent;
	}
	
	/**
	 * Método que añade un experto a la lista de miembros (hijos)
	 */
	public void addMember(Expert member) {
		//TODO validar member
		
		if(_members == null) {
			_members = new LinkedList<Expert>();
		}
		_members.add(member);
		member.setParent(this);
		Collections.sort(_members);
	}
	
	/**
	 * Método que elimina un experto de la lista de miembros (hijos)
	 */
	public void removeMember(Expert member) {
		
		if(_members != null) {
			_members.remove(member);
			member.setParent(null);
		}
		
		Collections.sort(_members);
			
	}
	
	/**
	 * Método get del atributo _members
	 */
	public List<Expert> getMembers() {
		return _members;
	}
	
	/**
	 * Método set del atributo _members
	 */
	public void setMembers(List<Expert> members) {
		_members = members;
	}
	
	/**
	 * Método que comprueba si existen miembros almacenados
	 */
	public boolean hasMembers() {
		if(_members != null && !_members.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Método que compara dos objecto de la clase Expert
	 */
	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}
		
		if(object == null || object.getClass() != this.getClass()) {
			return false;
		}
		
		//TODO
		
		return false;
	}
	
	//TODO getExpertByCanonicalID
	
	//TODO hashcode
	
	/**
	 * Método que clona un objeto de la clase Expert
	 */
	@Override
	public Object clone() {
		
		Expert result = null;
		
		result = (Expert) super.clone();
		result.setParent(_parent);
		
		if(hasMembers()) {
			List<Expert> members = new LinkedList<Expert>();
			for(Expert expert: _members){
				members.add((Expert)expert.clone());
			}
			result.setMembers(members);
		}
		
		return result;
	}
}
