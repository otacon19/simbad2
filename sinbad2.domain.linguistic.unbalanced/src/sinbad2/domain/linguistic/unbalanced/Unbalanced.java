package sinbad2.domain.linguistic.unbalanced;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;

public class Unbalanced extends FuzzySet {
	
	public static final String ID = "flintstones.domain.linguistic.unbalanced";
	
	private int[] _lh;
	private int _sl;
	private int _sr;
	private int _slDensity;
	private int _srDensity;
	private int _cardinality;
	private Map<Integer, Map<Integer, Integer>> _labels;
	
	public Unbalanced() {
		super();
	}
	
	public Unbalanced(String info) {
		String[] parts = info.split(";");
		String[] lhLevels = parts[0].split(",");
		String[] format = parts[1].split(",");
		String[] labels = parts[2].split(",");
		
		loadLhLevels(lhLevels);
		loadFormat(format);
		loadLabels(labels);
		
	}

	public Unbalanced(Integer cardinality) {
		_cardinality = cardinality;
		_labels = new HashMap<Integer, Map<Integer,Integer>>();
	}
	
	public int[] getLh() {
		return _lh;
	}

	public void setLh(int[] lh) {
		_lh = lh;
	}

	public int getSl() {
		return _sl;
	}

	public void setSl(int sl) {
		_sl = sl;
	}

	public int getSr() {
		return _sr;
	}

	public void setSr(int sr) {
		_sr = sr;
	}

	public int getSlDensity() {
		return _slDensity;
	}

	public void setSlDensity(int slDensity) {
		_slDensity = slDensity;
	}

	public int getSrDensity() {
		return _srDensity;
	}

	public void setSrDensity(int srDensity) {
		_srDensity = srDensity;
	}

	public int getCardinality() {
		return _cardinality;
	}

	public void setCardinality(int cardinality) {
		_cardinality = cardinality;
	}

	public Map<Integer, Map<Integer, Integer>> getLabels() {
		return _labels;
	}

	public void setLabels(Map<Integer, Map<Integer, Integer>> labels) {
		_labels = labels;
	}
	
	public boolean isBrid(int pos) {
		return (_labels.get(pos).size() > 1);
	}
	
	public void setLabel(int pos, Map<Integer, Integer> label) {
		_labels.put(pos, label);
	}
	
	public Map<Integer, Integer> getLabel(int pos) {
		return _labels.get(pos);
	}
	
	public void setLabelInDomain(int pos, int domain, int label) {
		
		if(!_labels.containsKey(pos)) {
			_labels.put(pos, new HashMap<Integer, Integer>());
		}
		_labels.get(pos).put(domain, label);
	}
	
	public Integer labelPos(int domain, int label) {
		Integer aux;
		for(int pos: _labels.keySet()) {
			aux = getLabelInDomain(pos, domain);
			if(aux != null) {
				if(aux == label) {
					return pos;
				}
			}
		}
		
		return null;
	}
	
	public Integer getLabelInDomain(int pos, int domain) {
		
		if(_labels.containsKey(pos)) {
			return _labels.get(pos).get(domain);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		String result = "";
		
		for(int label: _labels.keySet()) {
			result += "Label " + label + "\n";
			for(int domain: _labels.get(label).keySet()) {
				result += "\tDomain: " + domain + ":" + _labels.get(label).get(domain) + "\n";
			}
		}
		
		return result;
	}
	
	public String getInfo() {
		String result = "";
		
		result = loadStringLh(result);
		result = loadStringFormat(result);
		return loadStringLabels(result);
	}
	
	public Unbalanced createUnbalancedDomain(String[] labels, int sr, int sl, int sldensity, int srdensity, int initialDomain) {
		
		Validator.notNull(labels);
		Validator.notEmpty(labels);
		
		if(Validator.isNegative(sr)) {
			throw new IllegalArgumentException("Invalid sr");
		}
		
		if(Validator.isNegative(sl)) {
			throw new IllegalArgumentException("Invalid sl");
		}
		
		if(!Validator.isSameElement(sl + sr + 1, labels.length)) {
			throw new IllegalArgumentException("Invalid cardinality");
		}
		
		if((labels.length % 2) == 0) {
			throw new IllegalArgumentException("Pair cardinality");
		}
		
		if(!Validator.inRange(sldensity, 0, 1)) {
			throw new IllegalArgumentException("Invalid sl density");
		}
		
		if(!Validator.inRange(srdensity, 0, 1)) {
			throw new IllegalArgumentException("Invalid sr density");
		}
		
		if((initialDomain % 2) == 0) {
			throw new IllegalArgumentException("Pair initial domain");
		}
		
		if(initialDomain < 3) {
			throw new IllegalArgumentException("Invalid initial domain");
		}
		
		int aux = (initialDomain - 1) / 2;
		if((aux > sl) || (aux > sr)) {
			throw new IllegalArgumentException("Invalid initial domain");
		}
		
		Unbalanced result = new Unbalanced(labels.length);
		LabelLinguisticDomain leftCenterLabel, rightCenterLabel, centerLabel;
		
		result.setSl(sl);
		result.setSr(sr);
		result.setSlDensity(sldensity);
		result.setSrDensity(srdensity);
		
		List<Integer> lh = new LinkedList<Integer>();
		int lab_t, lab_tl, sideCardinality  = ((initialDomain - 1) / 2);
		boolean directly = false;
		
		lh.add(initialDomain);
		
		aux = sideCardinality;
		while(aux < sl) {
			aux *= 2;
			lh.add((aux * 2) + 1);
		}
		
		if(aux == sl) {
			directly = true;
		}
		
		if(directly) {
			int leftCardinality = (sl * 2) + 1;
			String leftLabels[] = new String[leftCardinality];
			
			for(int i = 0; i < leftCardinality; ++i) {
				if(i < sl) {
					leftLabels[i] = labels[i];
				} else {
					leftLabels[i] = "dirty label" + i;
				}
			}
			
			FuzzySet left = createTrapezoidalFunction(leftLabels);
			for(int i = 0; i < sl; ++i) {
				result.setLabelInDomain(i, leftCardinality, i);
				result.addLabel(left.getLabelSet().getLabel(i));
			}
			
			result.setLabelInDomain(sl, leftCardinality, sl);
			leftCenterLabel = left.getLabelSet().getLabel(sl);
		} else {
			LabelLinguisticDomain leftBrid, rightBrid, brid;
			lab_t = aux - sl;
			lab_tl = sl - lab_t;
			int sleCardinality, slcCardinality;
			String sleLabels[], slcLabels[];
			FuzzySet sleFuzzySet, slcFuzzySet;
		}
		
		
		return result;

		
	}


	private String loadStringLh(String result) {
		StringBuilder lh = new StringBuilder("");
		
		for(int l: _lh) {
			lh.append(l + ",");
		}
		lh.replace(lh.length() - 1, lh.length(),  "");
		result += lh.toString();
		
		return result;
	}
	
	private String loadStringFormat(String result) {
		String format = _sl + "," + _slDensity + "," + _sr + "," + _srDensity;
		
		result += format;
		
		return result;
	}
	
	private String loadStringLabels(String result) {
		StringBuilder labels = new StringBuilder(";");
		
		for(int label: _labels.keySet()) {
			for(int domain: _labels.get(label).keySet()) {
				labels.append(domain + ":" + _labels.get(label).get(domain) + "=");
			}
			labels.replace(labels.length() - 1, labels.length(), "");
		}
		labels.replace(labels.length() - 1, labels.length(), "");
		result += labels.toString();
		
		return result;
	}

	private void loadLhLevels(String[] lhLevels) {
		_lh = new int[lhLevels.length];
		for(int i = 0; i < lhLevels.length; ++i) {
			_lh[i] = Integer.parseInt(lhLevels[i]);
		}	
	}
	
	private void loadFormat(String[] format) {
		_sl = Integer.parseInt(format[0]);
		_slDensity = Integer.parseInt(format[1]);
		_sr = Integer.parseInt(format[2]);
		_srDensity = Integer.parseInt(format[3]);
		
		_cardinality = _sl + _sr + 1;
	}

	private void loadLabels(String[] labels) {
		_labels = new HashMap<Integer, Map<Integer,Integer>>();
		
		for(int i = 0; i < labels.length; ++i) {
			_labels.put(i, new HashMap<Integer, Integer>());
			String[] aux = labels[i].split("=");
			for(String s: aux) {
				String[] value = s.split(":");
				_labels.get(i).put(Integer.parseInt(value[0]), Integer.parseInt(value[1]));
			}
		}
	}

}
