package flintstones.element;

/**
 * ProblemElement.java
 * 
 * Clase que representa un elemento del problema.
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public final class ProblemElement implements Cloneable, Comparable<ProblemElement> {
	
	protected String _id;
	
	/**
	 * Constructor clase ProblemElement
	 */
	public ProblemElement() {
		_id = null;
	}
	
	/**
	 * Constructor clase ProblemElement
	 * @param id: id del elemento del problema
	 */
	public ProblemElement(String id){
		this._id = id;
	}
	
	/**
	 * Método get del atributo id de la clase ProblemElement
	 * @return devuelve el id del elemento del problema
	 */
	public String get_id() {
		return _id;
	}
	
	/**
	 * Método set del atributo id de la clase ProblemElement
	 * @param id: id del elemento del problema
	 */
	public void set_id(String id) {
		this._id = id;
	}

	@Override
	public int compareTo(ProblemElement arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Función que se encarga de la clonación del objeto, asignando el mismo id que el de su copia.
	 */
	@Override
	public Object clone() {
		ProblemElement result = null;
		
		try {
			result = (ProblemElement) super.clone();
		} catch (CloneNotSupportedException e) {
			//No ocurre nunca
		}
		
		result.set_id(_id);
		
		return result;
		
	}

	

}
