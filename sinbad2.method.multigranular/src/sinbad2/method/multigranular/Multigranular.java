package sinbad2.method.multigranular;

import sinbad2.method.MethodImplementation;
import sinbad2.method.state.MethodStateChangeEvent;

public class Multigranular extends MethodImplementation {
	
	public static final String ID = "flintstones.method.multigranular";
	
	@Override
	public MethodImplementation newInstance() {
		return new Multigranular();
	}

	@Override
	public void notifyMethodStateChange(MethodStateChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
