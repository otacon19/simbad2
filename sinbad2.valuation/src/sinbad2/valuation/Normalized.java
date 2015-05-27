package sinbad2.valuation;

import java.util.List;

public abstract class Normalized extends Valuation {
	
	private static final int MIN = -1;
	private static final int MAX = 1;
	
	private static Normalized getLimit(List<Normalized> valuations, int type) {
		Normalized result;
		
		//TODO validator
		
		if(valuations.isEmpty()) {
			return null;
		}
		
		result = valuations.get(0);
		for(Normalized valuation: valuations) {
			if(type == valuation.compareTo(result)) {
				result = valuation;
			}
		}
		
		return result;
	}
	
	public static Valuation min(List<Normalized> valuations) {
		return getLimit(valuations, MIN);
	}
	
	public static Valuation max(List<Normalized> valuations) {
		return getLimit(valuations, MAX);
	}
	
	@Override
	public Object clone() {
		return (Normalized) super.clone();
	}
	
	public abstract Normalized normalize();

}
