package sinbad2.phasemethod.emergency.computeweights;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.computinggainslosses.ComputingGainsAndLossesPhase;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ComputeWeightsPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.emergency.computeweights"; //$NON-NLS-1$

	private Double[][] _y;
	private Double[][] _yN;
	private List<Expert> _expertsWithoutPredefinedControl;
	private List<Alternative> _alternativesWithoutRP;
	private Map<Criterion, Double[]> _positiveAndNegativeValues;
	private Map<Criterion, Double[]> _distances;
	private Map<Criterion, Double> _dispersion;
	private Map<Criterion, Double> _weights;
	private Map<Alternative, Double> _ovs;

	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationsSet;

	private ComputingGainsAndLossesPhase _computeGainsAndLossesPhase;

	public ComputeWeightsPhase() {
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
		
		_computeGainsAndLossesPhase = (ComputingGainsAndLossesPhase) PhasesMethodManager.getInstance().getPhaseMethod(ComputingGainsAndLossesPhase.ID).getImplementation();

		_positiveAndNegativeValues = new HashMap<Criterion, Double[]>();
		_distances = new HashMap<Criterion, Double[]>();
		_dispersion = new HashMap<Criterion, Double>();
		_weights = new HashMap<Criterion, Double>();
		_ovs = new LinkedHashMap<Alternative, Double>();
	}

	@Override
	public void setUnifiedDomain(Domain domain) {
	}

	@Override
	public Domain getUnifiedDomain() {
		return null;
	}

	@Override
	public Map<ValuationKey, Valuation> getTwoTupleValuations() {
		return null;
	}

	public List<Expert> getExpertsWithoutPredefined() {
		return _expertsWithoutPredefinedControl;
	}
	
	public void setExpertsWithoutPredefined(List<Expert> expertsWithoutPredefinedControl) {
		_expertsWithoutPredefinedControl = expertsWithoutPredefinedControl;
	}
	
	public Double[][] getY() {
		return _y;
	}

	public void setY(Double[][] y) {
		_y = y;
	}

	public Double[][] getYN() {
		return _yN;
	}

	public void setYN(Double[][] yN) {
		_yN = yN;
	}

	public Map<Criterion, Double[]> getPositiveAndNegativeValues() {
		return _positiveAndNegativeValues;
	}

	public void setPositiveAndNegativeValues(Map<Criterion, Double[]> positiveAndNegativeValues) {
		_positiveAndNegativeValues = positiveAndNegativeValues;
	}

	public Map<Criterion, Double[]> getDistances() {
		return _distances;
	}

	public void setDistances(Map<Criterion, Double[]> distances) {
		_distances = distances;
	}

	public Map<Criterion, Double> getDispersion() {
		return _dispersion;
	}

	public void setDispersion(Map<Criterion, Double> dispersion) {
		_dispersion = dispersion;
	}

	public Map<Criterion, Double> getWeights() {
		return _weights;
	}

	public void setWeights(Map<Criterion, Double> weights) {
		_weights = weights;
	}

	public Map<Criterion, Double> computeWeights(List<Criterion> criteria) {
		_weights = new HashMap<Criterion, Double>();

		computeY();
		computePositiveAndNegativeValues(criteria);
		computeDistance();
		computeDispersion();

		double acum = 0;
		for (Criterion c : _dispersion.keySet()) {
			acum += _dispersion.get(c);
		}

		for (Criterion c : _dispersion.keySet()) {
			_weights.put(c, _dispersion.get(c) / acum);
		}

		return _weights;
	}

	private void computeY() {
		Alternative RP = _elementsSet.getAlternative("RP");
		_y = new Double[_expertsWithoutPredefinedControl.size()][_elementsSet.getAllCriteria().size()];

		for (int c = 0; c < _elementsSet.getAllCriteria().size(); c++) {
			Criterion cr = _elementsSet.getAllCriteria().get(c);
			if (!cr.hasSubcriteria()) {
				for (Expert e : _expertsWithoutPredefinedControl) {
					RealIntervalValuation v = (RealIntervalValuation) _valuationsSet.getValuation(e, RP, cr);
					_y[_expertsWithoutPredefinedControl.indexOf(e)][c] = computeIntegral(v._min, v._max);
				}
			}
		}

		normalizeY();
	}

	private Double computeIntegral(double min, double max) {
		return (1d / (max - min)) * ((Math.pow(max, 2) / 2d) - (Math.pow(min, 2) / 2d));
	}

	public Double[][] normalizeY() {
		_yN = new Double[_expertsWithoutPredefinedControl.size()][_elementsSet.getAllCriteria().size()];
		
		double acum;
		Double[] acumValues = new Double[_elementsSet.getAllCriteria().size()];
		for(int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
			acum = 0;
			if(!_elementsSet.getAllCriteria().get(c).hasSubcriteria()) {
				for(int e = 0; e < _expertsWithoutPredefinedControl.size(); e++) {
					acum += _y[e][c];
				}
			}
			acumValues[c] = acum;
		}
		
		for (int e = 0; e < _expertsWithoutPredefinedControl.size(); ++e) {
			for (int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
				if (!_elementsSet.getAllCriteria().get(c).hasSubcriteria()) {
					_yN[e][c] = _y[e][c] / acumValues[c];
				}
			}
		}

		return _yN;
	}

	private void computePositiveAndNegativeValues(List<Criterion> criteria) {
		_positiveAndNegativeValues = new HashMap<Criterion, Double[]>();

		Double[] positiveNegativeValues;
		for (int c = 0; c < criteria.size(); ++c) {
			Criterion cr = criteria.get(c);
			Criterion realCriterion = _elementsSet.getCriterion(cr.getId());
			positiveNegativeValues = new Double[2];
			if (!cr.hasSubcriteria()) {
				if (!cr.isCost()) {
					positiveNegativeValues[0] = maxNormalizedY(c);
					positiveNegativeValues[1] = minNormalizedY(c);
				} else {
					positiveNegativeValues[0] = minNormalizedY(c);
					positiveNegativeValues[1] = maxNormalizedY(c);
				}

				_positiveAndNegativeValues.put(realCriterion, positiveNegativeValues);
			}
		}
	}

	private Double maxNormalizedY(int c) {
		double max = Double.NEGATIVE_INFINITY;

		for (int e = 0; e < _expertsWithoutPredefinedControl.size(); ++e) {
			if (max < _yN[e][c]) {
				max = _yN[e][c];
			}
		}

		return max;
	}

	private Double minNormalizedY(int c) {
		double min = Double.POSITIVE_INFINITY;

		for (int e = 0; e < _expertsWithoutPredefinedControl.size(); ++e) {
			if (min > _yN[e][c]) {
				min = _yN[e][c];
			}
		}

		return min;
	}

	private void computeDistance() {
		_distances = new HashMap<Criterion, Double[]>();

		for (Criterion c : _positiveAndNegativeValues.keySet()) {
			Double[] distances = new Double[2];
			distances[0] = 0d;
			distances[1] = 0d;

			Double[] posAndNeg = _positiveAndNegativeValues.get(c);
			for (int e = 0; e < _expertsWithoutPredefinedControl.size(); ++e) {
				distances[0] += Math.pow((_yN[e][_elementsSet.getAllCriteria().indexOf(c)] - posAndNeg[0]), 2d);
				distances[1] += Math.pow((_yN[e][_elementsSet.getAllCriteria().indexOf(c)] - posAndNeg[1]), 2d);
			}
			distances[0] = Math.sqrt(distances[0]);
			distances[1] = Math.sqrt(distances[1]);
			_distances.put(c, distances);
		}
	}

	private void computeDispersion() {
		_dispersion = new HashMap<Criterion, Double>();

		double d;
		for (Criterion c : _distances.keySet()) {
			Double[] distances = _distances.get(c);
			d = distances[0] / (distances[0] + distances[1]);
			_dispersion.put(c, d);
		}
	}

	public Map<Alternative, Double> computeOv() {
		Map<Alternative, Double> ovs = new HashMap<Alternative, Double>();
		Double[][] vNormalized = _computeGainsAndLossesPhase.getNormalizedVMatrix();

		double acum;
		for (int a = 0; a < _alternativesWithoutRP.size(); ++a) {
			acum = 0;
			for (int c = 0; c < _elementsSet.getAllCriteria().size(); ++c) {
				if (!_elementsSet.getAllCriteria().get(c).hasSubcriteria()) {
					acum += vNormalized[a][c] * _weights.get(_elementsSet.getAllCriteria().get(c));
				}
			}

			ovs.put(_alternativesWithoutRP.get(a), acum);
		}

		_ovs = sortByValue(ovs);

		return _ovs;
	}

	private Map<Alternative, Double> sortByValue(Map<Alternative, Double> unsortMap) {

		List<Map.Entry<Alternative, Double>> list = new LinkedList<Map.Entry<Alternative, Double>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Alternative, Double>>() {
			public int compare(Map.Entry<Alternative, Double> o1, Map.Entry<Alternative, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<Alternative, Double> sortedMap = new LinkedHashMap<Alternative, Double>();
		for (Map.Entry<Alternative, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	@Override
	public void clear() {
		_dispersion.clear();
		_distances.clear();
		_positiveAndNegativeValues.clear();
		_weights.clear();
		_y = new Double[0][0];
		_yN = new Double[0][0];
		
		_expertsWithoutPredefinedControl = new LinkedList<Expert>();
		_expertsWithoutPredefinedControl.addAll(_elementsSet.getAllExperts());
		_expertsWithoutPredefinedControl.remove(_elementsSet.getExpert("predefined_effective_control"));

		_alternativesWithoutRP = new LinkedList<Alternative>();
		_alternativesWithoutRP.addAll(_elementsSet.getAlternatives());
		_alternativesWithoutRP.remove(_elementsSet.getAlternative("RP"));
	}

	@Override
	public IPhaseMethod clone() {
		ComputeWeightsPhase result = null;

		try {
			result = (ComputeWeightsPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new ComputeWeightsPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		ComputeWeightsPhase computeWeightsPhase = (ComputeWeightsPhase) iPhaseMethod;

		clear();

		_dispersion = computeWeightsPhase.getDispersion();
		_distances = computeWeightsPhase.getDistances();
		_positiveAndNegativeValues = computeWeightsPhase.getPositiveAndNegativeValues();
		_weights = computeWeightsPhase.getWeights();
		_y = computeWeightsPhase.getY();
		_yN = computeWeightsPhase.getYN();
	}

	@Override
	public void activate() {
	}

	@Override
	public boolean validate() {
		if (_valuationsSet.getValuations().isEmpty()) {
			return false;
		}

		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}

		if (_elementsSet.getCriteria().isEmpty()) {
			return false;
		}

		if (_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}
}
