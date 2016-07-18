package sinbad2.phasemethod.todim.resolution;

public class Pair<L,R> {
	
	private L _left;
	private R _right;
 
	public Pair() {
		_left = null;
		_right = null;
	}
	  
	public Pair(L left, R right) {
	    _left = left;
	    _right = right;
	}
	
	public L getLeft() { 
		return _left; 
	}
	  
	public R getRight() { 
		return _right; 
	}
	
	public void add(L left, R right) {
		_left = left;
		_right = right;
	}
	
	@Override
	public int hashCode() { 
		return _left.hashCode() ^ _right.hashCode(); 
	}
	
	@Override
	public boolean equals(Object o) {
	
	if (!(o instanceof Pair)) {
		return false;
	}
	Pair<?, ?> pairo = (Pair<?, ?>) o;
	return this._left.equals(pairo.getLeft()) && this._right.equals(pairo.getRight());
	}
	
	public boolean isEmpty() {
		return (_left == null) && (_right == null);
	}
	
	public void clear() {
		_left = null;
		_right = null;
	}
}
