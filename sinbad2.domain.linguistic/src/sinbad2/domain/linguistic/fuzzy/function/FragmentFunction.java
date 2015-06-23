package sinbad2.domain.linguistic.fuzzy.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.function.types.LinearPieceFunction;
import sinbad2.domain.numeric.real.NumericRealDomain;

public class FragmentFunction {
	
	Map<NumericRealDomain, IFragmentFunction> _pieces;
	
	public FragmentFunction() {
		_pieces = new HashMap<NumericRealDomain, IFragmentFunction>();
	}
	
	public Map<NumericRealDomain, IFragmentFunction> getPieces() {
		return _pieces;
	}
	
	public void addPiece(NumericRealDomain domain, IFragmentFunction piece) {
		Validator.notNull(domain);
		Validator.notNull(piece);
		
		double otherMin = domain.getMin();
		double otherMax = domain.getMax();
		
		if(otherMin == otherMax) {
			return;
		}
		
		double currentMin, currentMax;
		NumericRealDomain lower, central, upper;
		IFragmentFunction currentPiece;
		
		Set<NumericRealDomain> domains = _pieces.keySet();
		
		for(NumericRealDomain currentDomain: domains) {
			currentMin = currentDomain.getMin();
			currentMax = currentDomain.getMax();
			currentPiece = _pieces.get(currentDomain);
			
			switch(Double.compare(currentMin, otherMin)) {
				case -1:
					if(currentMax > otherMin) {
						switch(Double.compare(currentMax, otherMax)) {
							case -1:
								lower = new NumericRealDomain();
								lower.setMinMax(currentMin, otherMin);
								central = new NumericRealDomain();
								central.setMinMax(otherMin, currentMax);
								upper = new NumericRealDomain();
								upper.setMinMax(currentMax, otherMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(lower, currentPiece);
								_pieces.put(central, piece.sumFunctions(currentPiece));
								addPiece(upper, piece);
								break;
							case 0:
								lower = new NumericRealDomain();
								lower.setMinMax(currentMin, otherMin);
								upper = new NumericRealDomain();
								upper.setMinMax(otherMin, otherMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(lower, currentPiece);
								_pieces.put(upper, piece.sumFunctions(currentPiece));
								break;
							case 1:
								lower = new NumericRealDomain();
								lower.setMinMax(currentMin, otherMin);
								central = new NumericRealDomain();
								central.setMinMax(otherMin, otherMax);
								upper = new NumericRealDomain();
								upper.setMinMax(otherMax, currentMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(lower, currentPiece);
								_pieces.put(central, piece.sumFunctions(currentPiece));
								_pieces.put(upper, currentPiece);
								break;
						}
						
						return;
					}
					
					break;
					
				case 0:
					
					switch(Double.compare(currentMax, otherMax)) {
					case -1:
						lower = new NumericRealDomain();
						lower.setMinMax(otherMin, currentMax);
						upper = new NumericRealDomain();
						upper.setMinMax(currentMax, otherMax);
						
						_pieces.remove(currentDomain);
						_pieces.put(lower, piece.sumFunctions(currentPiece));
						addPiece(upper, piece);
						break;
					case 0:
						_pieces.remove(currentDomain);
						_pieces.put(domain, piece.sumFunctions(currentPiece));
						break;
					case 1:
						lower = new NumericRealDomain();
						lower.setMinMax(currentMin, otherMax);
						upper = new NumericRealDomain();
						upper.setMinMax(otherMax, currentMax);
						
						_pieces.remove(currentDomain);
						_pieces.put(lower, piece.sumFunctions(currentPiece));
						_pieces.put(upper, currentPiece);
						break;
					}
					
					return;
					
				case 1:
					
					if(otherMax > currentMin) {
						
						switch(Double.compare(otherMax, currentMax)) {
							case -1:
								lower = new NumericRealDomain();
								lower.setMinMax(otherMin, currentMin);
								central = new NumericRealDomain();
								central.setMinMax(currentMin, otherMax);
								upper = new NumericRealDomain();
								upper.setMinMax(otherMax, currentMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(central, piece.sumFunctions(currentPiece));
								_pieces.put(upper, currentPiece);
								addPiece(lower, piece);
								break;
								
							case 0:
								lower = new NumericRealDomain();
								lower.setMinMax(otherMin, currentMin);
								upper = new NumericRealDomain();
								upper.setMinMax(currentMin, otherMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(upper, piece.sumFunctions(currentPiece));
								addPiece(lower, piece);
								break;
							case 1:
								lower = new NumericRealDomain();
								lower.setMinMax(otherMin, currentMin);
								central = new NumericRealDomain();
								central.setMinMax(currentMin, currentMax);
								upper = new NumericRealDomain();
								upper.setMinMax(currentMax, otherMax);
								
								_pieces.remove(currentDomain);
								_pieces.put(central, piece.sumFunctions(currentPiece));
								addPiece(lower, piece);
								addPiece(upper, piece);
								break;
						}
						
						return;
						
					}
					
					break;
			}
		}	
		_pieces.put(domain, piece);
	}
	
	public void simplify() {
		Map<NumericRealDomain, IFragmentFunction> simplifyPieces = new HashMap<NumericRealDomain, IFragmentFunction>();
		LinearPieceFunction piece1, piece2;
		NumericRealDomain domain;
		
		for(NumericRealDomain domain1: _pieces.keySet()) {
			piece1 = (LinearPieceFunction) _pieces.get(domain1);
			
			for(NumericRealDomain domain2: simplifyPieces.keySet()) {
				piece2 = (LinearPieceFunction) simplifyPieces.get(domain2);
				
				if(domain1.getMin() == domain2.getMax()) {
					if(piece1.equals(piece2)) {
						domain = new NumericRealDomain();
						domain.setMinMax(domain2.getMin(), domain1.getMax());
						_pieces.remove(domain1);
						_pieces.remove(domain2);
						_pieces.put(domain, piece1);
						simplify();
						return;
					}
				} else if(domain1.getMax() == domain2.getMin()) {
					if(piece1.equals(piece2)) {
						domain = new NumericRealDomain();
						domain.setMinMax(domain1.getMin(), domain2.getMax());
						_pieces.remove(domain1);
						_pieces.remove(domain2);
						_pieces.put(domain, piece1);
						simplify();
						return;
					}
				}
			}
			
			simplifyPieces.put(domain1, piece1);
		}
		
		_pieces = simplifyPieces;
		
	}
}
