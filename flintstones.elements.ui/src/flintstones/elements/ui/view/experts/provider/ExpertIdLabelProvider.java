package flintstones.elements.ui.view.experts.provider;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * ColumnLabelProvider.java
 * 
 * Clase que se encarga de extraer el valor del objeto del árbol para mostrarlo de forma correcta
 * 
 * @author Labella Romero, Álvaro
 * @version 1.0
 *
 */
public class ExpertIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj){
		return "experto";	
	}
	
	@Override
	public Image getImage(Object obj){
		return null;
	}

}
