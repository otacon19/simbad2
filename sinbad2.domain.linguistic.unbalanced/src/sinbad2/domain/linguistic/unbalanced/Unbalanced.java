package sinbad2.domain.linguistic.unbalanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import sinbad2.core.validator.Validator;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.nls.Messages;
import sinbad2.resolutionphase.io.XMLRead;

public class Unbalanced extends FuzzySet {
	
	public static final String ID = "flintstones.domain.linguistic.unbalanced"; //$NON-NLS-1$
	
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
		String[] parts = info.split(";"); //$NON-NLS-1$
		String[] lhLevels = parts[0].split(","); //$NON-NLS-1$
		String[] format = parts[1].split(","); //$NON-NLS-1$
		String[] labels = parts[2].split(","); //$NON-NLS-1$
		
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
		String result = ""; //$NON-NLS-1$
		
		for(int label: _labels.keySet()) {
			result += "Label " + label + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
			for(int domain: _labels.get(label).keySet()) {
				result += "\tDomain: " + domain + ":" + _labels.get(label).get(domain) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		return result;
	}
	
	public String getInfo() {
		String result = ""; //$NON-NLS-1$
		
		result = loadStringLh(result);
		result = loadStringFormat(result);
		return loadStringLabels(result);
	}
	
	public Unbalanced createUnbalancedDomain(String[] labels, int sr, int sl, int sldensity, int srdensity, int initialDomain) {
		Validator.notNull(labels);
		Validator.notEmpty(labels);
		
		if(Validator.isNegative(sr)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_sr);
		}
		
		if(Validator.isNegative(sl)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_sl);
		}
		
		if(!Validator.isSameElement(sl + sr + 1, labels.length)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_cardinality);
		}
		
		if((labels.length % 2) == 0) {
			throw new IllegalArgumentException(Messages.Unbalanced_Pair_cardinality);
		}
		
		if(!Validator.inRange(sldensity, 0, 1)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_sl_density);
		}
		
		if(!Validator.inRange(srdensity, 0, 1)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_sr_density);
		}
		
		if((initialDomain % 2) == 0) {
			throw new IllegalArgumentException(Messages.Unbalanced_Pair_initial_domain);
		}
		
		if(initialDomain < 3) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_initial_domain);
		}
		
		int aux = (initialDomain - 1) / 2;
		if((aux > sl) || (aux > sr)) {
			throw new IllegalArgumentException(Messages.Unbalanced_Invalid_initial_domain);
		}
		
		Unbalanced unbalancedInfo = new Unbalanced(labels.length);
		_cardinality = labels.length;
		_sr = sr;
		_sl = sl;
		_srDensity = srdensity;
		_slDensity = sldensity;
		
		LabelLinguisticDomain leftCenterLabel, rightCenterLabel, centerLabel;
		
		List<Integer> lh = new LinkedList<Integer>();
		int lab_t, lab_t1, sideCardinality  = ((initialDomain - 1) / 2);
		boolean directly = false;
		
		lh.add(initialDomain);
		
		directly = false;
		aux = sideCardinality;
		while (aux < sl) {
			aux *= 2;
			lh.add((aux * 2) + 1);
		}

		if (aux == sl) {
			directly = true;
		}

		if (directly) {
			int leftCardinality = (sl * 2) + 1;
			String leftLabels[] = new String[leftCardinality];

			for (int i = 0; i < leftCardinality; i++) {
				if (i < sl) {
					leftLabels[i] = labels[i];
				} else {
					leftLabels[i] = Messages.Unbalanced_Dirty_label + i;
				}
			}
			
			FuzzySet left = createTrapezoidalFunction(leftLabels);
			for (int i = 0; i < sl; i++) {
				unbalancedInfo.setLabelInDomain(i, leftCardinality, i);
				unbalancedInfo.addLabel(left.getLabelSet().getLabel(i));
			}

			unbalancedInfo.setLabelInDomain(sl, leftCardinality, sl);
			leftCenterLabel = left.getLabelSet().getLabel(sl);
		} else {
			LabelLinguisticDomain leftBrid, rightBrid, brid;
			lab_t = aux - sl;
			lab_t1 = sl - lab_t;
			int sleCardinality, slcCardinality;
			String sleLabels[], slcLabels[];
			FuzzySet sleFuzzySet, slcFuzzySet;

			if (sldensity == 0) {
				sleCardinality = (aux * 2) + 1;
				sleLabels = new String[sleCardinality];

				for (int i = 0; i < sleCardinality; i++) {
					if (i < lab_t1) {
						sleLabels[i] = labels[i];
					} else {
						sleLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}

				sleFuzzySet = createTrapezoidalFunction(sleLabels);
				for (int i = 0; i < lab_t1; i++) {
					unbalancedInfo.setLabelInDomain(i, sleCardinality, i);
					unbalancedInfo.addLabel(sleFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(lab_t1, sleCardinality, lab_t1);
				leftBrid = sleFuzzySet.getLabelSet().getLabel(lab_t1);

				slcCardinality = aux + 1;
				slcLabels = new String[slcCardinality];

				int alreadyUsed = lab_t1 / 2;
				int j = 0;
				for (int i = 0; i < slcCardinality; i++) {
					if ((i >= alreadyUsed) && (i < (lab_t + alreadyUsed))) {
						slcLabels[i] = labels[lab_t1 + j++];
					} else {
						slcLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}
				
					
				slcFuzzySet = createTrapezoidalFunction(slcLabels);
				for (int i = (alreadyUsed + 1); i < (lab_t + alreadyUsed); i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, slcCardinality, i);
					unbalancedInfo.addLabel(slcFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(lab_t1, slcCardinality, alreadyUsed);
				rightBrid = slcFuzzySet.getLabelSet().getLabel(alreadyUsed);

				double a = leftBrid.getSemantic().getCoverage().getMin();
				double b = rightBrid.getSemantic().getCenter().getMin();
				double d = rightBrid.getSemantic().getCoverage().getMax();
				double limits[] = {a, b, b, d};
				TrapezoidalFunction semantic = new TrapezoidalFunction(limits);
				brid = new LabelLinguisticDomain(rightBrid.getName(), semantic);
				unbalancedInfo.addLabel(lab_t1, brid);

				unbalancedInfo.setLabelInDomain(sl, slcCardinality, lab_t + alreadyUsed);
				leftCenterLabel = slcFuzzySet.getLabelSet().getLabel(lab_t + alreadyUsed);

			} else {
				sleCardinality = aux + 1;
				sleLabels = new String[sleCardinality];

				for (int i = 0; i < sleCardinality; i++) {
					if (i < lab_t) {
						sleLabels[i] = labels[i];
					} else {
						sleLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}
				
				sleFuzzySet = createTrapezoidalFunction(sleLabels);
				for (int i = 0; i < lab_t; i++) {
					unbalancedInfo.setLabelInDomain(i, sleCardinality, i);
					unbalancedInfo.addLabel(sleFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(lab_t, sleCardinality, lab_t);
				leftBrid = sleFuzzySet.getLabelSet().getLabel(lab_t);

				slcCardinality = (aux * 2) + 1;
				slcLabels = new String[slcCardinality];

				int alreadyUsed = lab_t * 2;
				int j = 0;
				for (int i = 0; i < slcCardinality; i++) {
					if ((i >= alreadyUsed) && (i < (lab_t1 + alreadyUsed))) {
						slcLabels[i] = labels[lab_t + j++];
					} else {
						slcLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}
				slcFuzzySet = createTrapezoidalFunction(slcLabels);
				for (int i = (alreadyUsed + 1); i < (lab_t1 + alreadyUsed); i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, slcCardinality, i);
					unbalancedInfo.addLabel(slcFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(lab_t, slcCardinality, alreadyUsed);
				rightBrid = slcFuzzySet.getLabelSet().getLabel(alreadyUsed);

				double a = leftBrid.getSemantic().getCoverage().getMin();
				double b = rightBrid.getSemantic().getCenter().getMin();
				double d = rightBrid.getSemantic().getCoverage().getMax();
				double limits[] = { a, b, b, d };
				TrapezoidalFunction semantic = new TrapezoidalFunction(limits);
				brid = new LabelLinguisticDomain(rightBrid.getName(), semantic);
				unbalancedInfo.addLabel(lab_t, brid);
				unbalancedInfo.setLabelInDomain(sl, slcCardinality, lab_t1 + alreadyUsed);
				leftCenterLabel = slcFuzzySet.getLabelSet().getLabel(lab_t1 + alreadyUsed);
			}
		}
	
		directly = false;
		aux = sideCardinality;
		int bigger = lh.get(lh.size() - 1);
		while (aux < sr) {
			aux *= 2;
			if (((aux * 2) + 1) > bigger) {
				lh.add((aux * 2) + 1);
			}
		}

		if (aux == sr) {
			directly = true;
		}

		if (directly) {
			int rightCardinality = (sr * 2) + 1;
			String rightLabels[] = new String[rightCardinality];

			for (int i = 0; i < rightCardinality; i++) {
				if (i >= (rightCardinality - sr)) {
					rightLabels[i] = labels[labels.length - (rightCardinality - i)];
				} else {
					rightLabels[i] = Messages.Unbalanced_Dirty_label + i;
				}
			}
			FuzzySet right = createTrapezoidalFunction(rightLabels);
			for (int i = (rightCardinality - sr); i < rightCardinality; i++) {
				unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, rightCardinality, i);
				unbalancedInfo.addLabel(right.getLabelSet().getLabel(i));
			}

			rightCenterLabel = right.getLabelSet().getLabel(rightCardinality - sr - 1);

			double a = leftCenterLabel.getSemantic().getCoverage().getMin();
			double b = rightCenterLabel.getSemantic().getCenter().getMin();
			double d = rightCenterLabel.getSemantic().getCoverage().getMax();
			double limits[] = { a, b, b, d };
			TrapezoidalFunction semantic = new TrapezoidalFunction(limits);
			centerLabel = new LabelLinguisticDomain(labels[sl], semantic);
			unbalancedInfo.setLabelInDomain(sl, rightCardinality, rightCardinality - sr - 1);
			unbalancedInfo.addLabel(sl, centerLabel);

		} else {
			LabelLinguisticDomain leftBrid, rightBrid, brid;
			lab_t = aux - sr;
			lab_t1 = sr - lab_t;
			int sreCardinality, srcCardinality;
			String sreLabels[], srcLabels[];
			FuzzySet sreFuzzySet, srcFuzzySet;

			if (srdensity == 0) {
				srcCardinality = aux + 1;
				srcLabels = new String[srcCardinality];

				int j = 0;
				for (int i = 0; i < srcCardinality; i++) {
					if ((i > (aux / 2)) && (i <= ((aux / 2) + lab_t))) {
						srcLabels[i] = labels[sl + 1 + j++];
					} else {
						srcLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}

				srcFuzzySet = createTrapezoidalFunction(srcLabels);
				for (int i = ((aux / 2) + 1); i < ((aux / 2) + lab_t); i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, srcCardinality, i);
					unbalancedInfo.addLabel(srcFuzzySet.getLabelSet().getLabel(i));
				}
				unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, srcCardinality, (aux / 2) + lab_t);
				leftBrid = srcFuzzySet.getLabelSet().getLabel((aux / 2) + lab_t);

				rightCenterLabel = srcFuzzySet.getLabelSet().getLabel(aux / 2);

				double a = leftCenterLabel.getSemantic().getCoverage().getMin();
				double b = rightCenterLabel.getSemantic().getCenter().getMin();
				double d = rightCenterLabel.getSemantic().getCoverage().getMax();
				double limits[] = { a, b, b, d };
				TrapezoidalFunction semantic = new TrapezoidalFunction(limits);
				centerLabel = new LabelLinguisticDomain(labels[sl], semantic);
				
				unbalancedInfo.setLabelInDomain(sl, srcCardinality, aux / 2);
				unbalancedInfo.addLabel(sl, centerLabel);

				sreCardinality = (aux * 2) + 1;
				sreLabels = new String[sreCardinality];

				for (int i = 0; i < sreCardinality; i++) {
					if (i >= (sreCardinality - lab_t1)) {
						sreLabels[i] = labels[labels.length - (sreCardinality - i)];
					} else {
						sreLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}

				sreFuzzySet = createTrapezoidalFunction(sreLabels);
				for (int i = (sreCardinality - lab_t1); i < sreCardinality; i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, sreCardinality, i);
					unbalancedInfo.addLabel(sreFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(sl + lab_t, sreCardinality, sreCardinality - lab_t1 - 1);
				rightBrid = sreFuzzySet.getLabelSet().getLabel(sreCardinality - lab_t1 - 1);

				a = leftBrid.getSemantic().getCoverage().getMin();
				b = rightBrid.getSemantic().getCenter().getMin();
				d = rightBrid.getSemantic().getCoverage().getMax();
				double limits1[] = { a, b, b, d };
				TrapezoidalFunction semantic1 = new TrapezoidalFunction(limits1);
				brid = new LabelLinguisticDomain(leftBrid.getName(), semantic1);
				unbalancedInfo.addLabel(sl + lab_t, brid);

			} else {
				srcCardinality = (aux * 2) + 1;
				srcLabels = new String[srcCardinality];

				int j = 0;
				for (int i = 0; i < srcCardinality; i++) {
					if ((i > aux) && i < (aux + lab_t1)) {
						srcLabels[i] = labels[sl + 1 + j++];
					} else {
						srcLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}

				srcFuzzySet = createTrapezoidalFunction(srcLabels);
				for (int i = (aux + 1); i < (aux + lab_t1); i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, srcCardinality, i);
					unbalancedInfo.addLabel(srcFuzzySet.getLabelSet().getLabel(i));
				}

				leftBrid = srcFuzzySet.getLabelSet().getLabel(aux + lab_t1);

				rightCenterLabel = srcFuzzySet.getLabelSet().getLabel(aux);

				double a = leftCenterLabel.getSemantic().getCoverage().getMin();
				double b = rightCenterLabel.getSemantic().getCenter().getMin();
				double d = rightCenterLabel.getSemantic().getCoverage().getMax();
				double limits[] = { a, b, b, d };
				TrapezoidalFunction semantic = new TrapezoidalFunction(limits);
				centerLabel = new LabelLinguisticDomain(labels[sl], semantic);
				unbalancedInfo.setLabelInDomain(sl, srcCardinality, aux);
				unbalancedInfo.addLabel(sl, centerLabel);

				sreCardinality = aux + 1;
				sreLabels = new String[sreCardinality];

				for (int i = 0; i < sreCardinality; i++) {
					if (i >= (sreCardinality - lab_t - 1)) {
						sreLabels[i] = labels[labels.length - (sreCardinality - i)];
					} else {
						sreLabels[i] = Messages.Unbalanced_Dirty_label + i;
					}
				}

				sreFuzzySet = createTrapezoidalFunction(sreLabels);

				for (int i = (sreCardinality - lab_t); i < sreCardinality; i++) {
					unbalancedInfo.setLabelInDomain(unbalancedInfo.getCardinality() + 1, sreCardinality, i);
					unbalancedInfo.addLabel(sreFuzzySet.getLabelSet().getLabel(i));
				}

				unbalancedInfo.setLabelInDomain(sl + lab_t1, srcCardinality, aux + lab_t1);
				unbalancedInfo.setLabelInDomain(sl + lab_t1, sreCardinality, sreCardinality - lab_t - 1);
				rightBrid = sreFuzzySet.getLabelSet().getLabel(sreCardinality - lab_t - 1);
				a = leftBrid.getSemantic().getCoverage().getMin();
				b = rightBrid.getSemantic().getCenter().getMin();
				d = rightBrid.getSemantic().getCoverage().getMax();
				double limits1[] = { a, b, b, d };
				TrapezoidalFunction semantic1 = new TrapezoidalFunction(limits1);
				brid = new LabelLinguisticDomain(rightBrid.getName(), semantic1);
				unbalancedInfo.addLabel(sl + lab_t1, brid);
			}
		}
		
		int lhArray[] = new int[lh.size()];
		
		for (int i = 0; i < lhArray.length; i++) {
			lhArray[i] = lh.get(i);
		}
		
		unbalancedInfo.setLh(lhArray);
		_lh = lhArray;
		
		_labelSet = unbalancedInfo.getLabelSet();
		_labels = unbalancedInfo.getLabels();
		
		return unbalancedInfo;
		
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
	
		writer.writeStartElement("lh"); //$NON-NLS-1$
		for (int i = 0; i < _lh.length; ++i) {
			writer.writeStartElement("lhValue"); //$NON-NLS-1$
			writer.writeAttribute("l", Integer.toString(_lh[i])); //$NON-NLS-1$
			writer.writeEndElement();
		}
		writer.writeEndElement();
		
		writer.writeStartElement("labelsU"); //$NON-NLS-1$
		for (Integer pos: _labels.keySet()) {
			Map<Integer, Integer> labels = _labels.get(pos);
			for(Integer domain: labels.keySet()) {
				writer.writeStartElement("domain-label"); //$NON-NLS-1$
				writer.writeAttribute("d", Integer.toString(domain)); //$NON-NLS-1$
				writer.writeAttribute("la", Integer.toString(labels.get(domain))); //$NON-NLS-1$
				writer.writeEndElement();
			}
			writer.writeStartElement("labelU"); //$NON-NLS-1$
			writer.writeAttribute("pos", Integer.toString(pos)); //$NON-NLS-1$
			writer.writeEndElement();
		}
		writer.writeEndElement();
		
		writer.writeStartElement("data"); //$NON-NLS-1$
		writer.writeAttribute("sl", Integer.toString(_sl)); //$NON-NLS-1$
		writer.writeAttribute("sr", Integer.toString(_sr)); //$NON-NLS-1$
		writer.writeAttribute("slDensity", Integer.toString(_slDensity)); //$NON-NLS-1$
		writer.writeAttribute("srDensity", Integer.toString(_srDensity));; //$NON-NLS-1$
		writer.writeAttribute("cardinality", Integer.toString(_cardinality)); //$NON-NLS-1$
		writer.writeEndElement();
		
		super.save(writer);
	}
	
	@Override
	public void read(XMLRead reader) throws XMLStreamException {	
		XMLEvent event;
		String v, domain, pos, label,  endtag = null;
		Integer value = null;
		boolean end = false;
		ArrayList<Integer> lh = new ArrayList<Integer>();
		Map<Integer, Integer> data = null;
		
		_labels = new HashMap<Integer, Map<Integer,Integer>>();
		
		reader.goToStartElement("lh"); //$NON-NLS-1$
		
		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				if ("lhValue".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					v = reader.getStartElementAttribute("l"); //$NON-NLS-1$
					value = new Integer(v);
					lh.add(value);
				} else if("domain-label".equals(reader.getStartElementLocalPart())) {	 //$NON-NLS-1$
					domain = reader.getStartElementAttribute("d"); //$NON-NLS-1$
					label = reader.getStartElementAttribute("la"); //$NON-NLS-1$
					data = new HashMap<Integer, Integer>();
					data.put(Integer.parseInt(domain), Integer.parseInt(label));
				} else if("labelU".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					pos = reader.getStartElementAttribute("pos"); //$NON-NLS-1$
					_labels.put(Integer.parseInt(pos), data);	
				} else if("data".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					_sl = Integer.parseInt(reader.getStartElementAttribute("sl"));	 //$NON-NLS-1$
					_sr = Integer.parseInt(reader.getStartElementAttribute("sr")); //$NON-NLS-1$
					_slDensity = Integer.parseInt(reader.getStartElementAttribute("slDensity")); //$NON-NLS-1$
					_srDensity = Integer.parseInt(reader.getStartElementAttribute("srDensity")); //$NON-NLS-1$
					_cardinality = Integer.parseInt(reader.getStartElementAttribute("cardinality")); //$NON-NLS-1$
				}
			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("data")) { //$NON-NLS-1$
					_lh = new int[lh.size()] ;
					for(int i = 0; i < lh.size(); ++i) {
						_lh[i] = lh.get(i); 
					}
					end = true;
				}
			}
		}
		
		super.read(reader);
	}

	private String loadStringLh(String result) {
		StringBuilder lh = new StringBuilder(""); //$NON-NLS-1$
		
		for(int l: _lh) {
			lh.append(l + ","); //$NON-NLS-1$
		}
		lh.replace(lh.length() - 1, lh.length(),  ""); //$NON-NLS-1$
		result += lh.toString();
		
		return result;
	}
	
	private String loadStringFormat(String result) {
		String format = _sl + "," + _slDensity + "," + _sr + "," + _srDensity; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		result += format;
		
		return result;
	}
	
	private String loadStringLabels(String result) {
		StringBuilder labels = new StringBuilder(";"); //$NON-NLS-1$
		
		for(int label: _labels.keySet()) {
			for(int domain: _labels.get(label).keySet()) {
				labels.append(domain + ":" + _labels.get(label).get(domain) + "="); //$NON-NLS-1$ //$NON-NLS-2$
			}
			labels.replace(labels.length() - 1, labels.length(), ""); //$NON-NLS-1$
		}
		labels.replace(labels.length() - 1, labels.length(), ""); //$NON-NLS-1$
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
			String[] aux = labels[i].split("="); //$NON-NLS-1$
			for(String s: aux) {
				String[] value = s.split(":"); //$NON-NLS-1$
				_labels.get(i).put(Integer.parseInt(value[0]), Integer.parseInt(value[1]));
			}
		}
	}

}
