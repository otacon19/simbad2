package flintstones.element.expert.listener;

/**
 * IExpertsChangeListener.java
 * 
 * Observador que notifica los cambios producidos en el modelo (experto)
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public interface IExpertsChangeListener {
	
	public void notifyExpertsChange(ExpertsChangeEvent event);

}
