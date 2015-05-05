package flintstones.element.ui.view.experts.provider;


import org.eclipse.jface.viewers.ColumnLabelProvider;

import flintstones.element.expert.Expert;
import flintstones.element.ui.Images;

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
	
	/**
	 * Método que extrae la cadena que mostrará el objeto en la visualización
	 */
	@Override
	public String getText(Object obj){
		return ((Expert) obj).getId();
	}
	
	/**
	 * Método que obtiene la imagen que va a mostrar el objeto en la visualización
	 */
	@Override
	public Image getImage(Object obj){
		if(((Expert) obj).hasMembers()) {
			return Images.GroupOfExperts;
		} else {
			return Images.Expert;
		}
	}

}
