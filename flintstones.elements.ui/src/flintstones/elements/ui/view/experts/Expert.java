package flintstones.elements.ui.view.experts;

import java.util.ArrayList;
import java.util.List;

public class Expert {
	private String nombre;
	
	public Expert(String nombre){
		this.nombre = nombre;
	}
	
	
	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public static List<Expert> createExpertsExample() {
		List<Expert> experts = new ArrayList<Expert>();
		
		Expert exp1 = new Expert("Estrella");
		Expert exp2 = new Expert("Álvaro");
		Expert exp3 = new Expert("Fran");
		
		experts.add(exp1);
		experts.add(exp2);
		experts.add(exp3);
		
		return experts;
		
	}
}
