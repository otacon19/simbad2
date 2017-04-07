package sinbad2.phasemethod.emergency.computinggainslosses;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.core.utils.Pair;
import sinbad2.domain.Domain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.aggregation.AggregationPhase;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ComputingGainsAndLossesPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.emergency.computinggainsandlosses"; //$NON-NLS-1$

	private static final double alpha = 0.89;
	private static final double beta = 0.92;
	private static final double lambda = 2.25;

	private ValuationSet _valuationsSet;
	private ProblemElementsSet _elementsSet;
	private Map<Criterion, RealIntervalValuation> _grps;
	private Map<Pair<Alternative, Criterion>, Double[]> _GLM;
	private Double[][] _GLMMatrix;
	private Double[][] _V;
	private Double[][] _VN;
	private List<Alternative> _alternativesWithoutRP;

	private AggregationPhase _aggregationPhase;

	public ComputingGainsAndLossesPhase() {
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();

		_grps = new HashMap<Criterion, RealIntervalValuation>();
		_GLM = new HashMap<Pair<Alternative, Criterion>, Double[]>();
		

		_aggregationPhase = (AggregationPhase) PhasesMethodManager.getInstance().getPhaseMethod(AggregationPhase.ID).getImplementation();
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

	public void setGRPs(Map<Criterion, RealIntervalValuation> grps) {
		_grps = grps;
	}

	public Map<Criterion, RealIntervalValuation> getGRPs() {
		return _grps;
	}

	public Map<Pair<Alternative, Criterion>, Double[]> getGLM() {
		return _GLM;
	}

	public void setGLM(Map<Pair<Alternative, Criterion>, Double[]> GLM) {
		_GLM = GLM;
	}

	public Double[][] getGLMMatrix() {
		return _GLMMatrix;
	}
	
	public void setGLMMatrix(Double[][] GLMMatrix) {
		_GLMMatrix = GLMMatrix;
	}

	public void setVMatrix(Double[][] V) {
		_V = V;
	}

	public Double[][] getVMatrix() {
		return _V;
	}
	
	public void setNormalizedVMatrix(Double[][] VN) {
		_VN = VN;
	}

	public Double[][] getNormalizedVMatrix() {
		return _VN;
	}
	
	public void setAlternativesWithoutRP(List<Alternative> alternativesWithoutRP) {
		_alternativesWithoutRP = alternativesWithoutRP;
	}

	public List<Alternative> getAlternativesWithoutRP() {
		return _alternativesWithoutRP;
	}

	public Double[][] computeVMatrix(List<Criterion> criteria) {

		setGRPs(_aggregationPhase.getGRPs());

		_V = new Double[_alternativesWithoutRP.size()][_elementsSet.getCriteria().size()];
		double value = Double.NaN;

		computeGLM(criteria);
		
		for(int a = 0; a < _alternativesWithoutRP.size(); ++a) {
			for(int c = 0; c < criteria.size(); ++c) {
				if(!criteria.get(c).hasSubcriteria()) {
					if(_GLMMatrix[a][c] >= 0) {
						value = Math.pow(_GLMMatrix[a][c], alpha);
					} else if(_GLMMatrix[a][c] < 0) {
						value = -lambda * Math.pow(-_GLMMatrix[a][c], beta);
					}
					_V[a][c] = value;
				}
			}
		}

		return _V;
	}

	private void computeGLM(List<Criterion> criteria) {
		_GLM = new HashMap<Pair<Alternative, Criterion>, Double[]>();
		_GLMMatrix = new Double[_alternativesWithoutRP.size()][_elementsSet.getCriteria().size()];
		
		Expert predefined_effective_control = _elementsSet.getExpert("predefined_effective_control");

		for (Alternative a : _alternativesWithoutRP) {
			for (Criterion c : criteria) {
				Criterion realCriterion = _elementsSet.getCriterion(c.getId());
				RealIntervalValuation p = (RealIntervalValuation) _valuationsSet.getValuation(predefined_effective_control, a, realCriterion);
				RealIntervalValuation grp = _grps.get(realCriterion);
				if (!c.hasSubcriteria()) {
					Double[] gainsLosses = new Double[2];
					Pair<Alternative, Criterion> pair = new Pair<Alternative, Criterion>(a, realCriterion);
					if (c.isCost()) {
						if (p._max < grp._min) { // Case 1
							gainsLosses[0] = grp._min - 0.5 * (p._min + p._max);
							gainsLosses[1] = 0d;
						} else if (grp._max < p._min) { // Case 2
							gainsLosses[0] = 0d;
							gainsLosses[1] = grp._max - 0.5 * (p._min + p._max);
						} else if ((p._min < grp._min) && (grp._min <= p._max) && (p._max < grp._max)) { // Case 3
							gainsLosses[0] = 0.5 * (grp._min - p._min);
							gainsLosses[1] = 0d;
						} else if ((grp._min < p._min) && (p._min <= grp._max) && (grp._max < p._max)) { // Case 4
							gainsLosses[0] = 0d;
							gainsLosses[1] = 0.5 * (grp._max - p._max);
						} else if ((p._min < grp._min) && (grp._min < grp._max) && (grp._max < p._max)) { // Case 5
							gainsLosses[0] = 0.5 * (grp._min - p._min);
							gainsLosses[1] = 0.5 * (grp._max - p._max);
						} else if ((grp._min <= p._min) && (p._min < p._max) && (p._max <= grp._max)) { // Case 6
							gainsLosses[0] = 0d;
							gainsLosses[1] = 0d;
						}
					} else {
						if (p._max < grp._min) { // Case 1
							gainsLosses[0] = 0d;
							gainsLosses[1] = 0.5 * (p._min + p._max) - grp._min;
						} else if (grp._max < p._min) { // Case 2
							gainsLosses[0] = 0.5 * (p._min + p._max) - grp._max;
							gainsLosses[1] = 0d;
						} else if ((p._min < grp._min) && (grp._min <= p._max) && (p._max < grp._max)) { // Case 3
							gainsLosses[0] = 0d;
							gainsLosses[1] = 0.5 * (p._min - grp._min);
						} else if ((grp._min < p._min) && (p._min <= grp._max) && (grp._max < p._max)) { // Case 4
							gainsLosses[0] = 0.5 * (p._max - grp._max);
							gainsLosses[1] = 0d;
						} else if ((p._min < grp._min) && (grp._min < grp._max) && (grp._max < p._max)) { // Case 5
							gainsLosses[0] = 0.5 * (p._max - grp._max);
							gainsLosses[1] = 0.5 * (p._min - grp._min);
						} else if ((grp._min <= p._min) && (p._min < p._max) && (p._max <= grp._max)) { // Case 6
							gainsLosses[0] = 0d;
							gainsLosses[1] = 0d;
						}
					}
					
					if(gainsLosses[0] > 0 && gainsLosses[1] == 0) {
						_GLMMatrix[_alternativesWithoutRP.indexOf(a)][criteria.indexOf(c)] = gainsLosses[0];
					} else if(gainsLosses[0] == 0 && gainsLosses[1] < 0) {
						_GLMMatrix[_alternativesWithoutRP.indexOf(a)][criteria.indexOf(c)] = gainsLosses[1];
					} else if(gainsLosses[0] > 0 && gainsLosses[1] < 0){
						_GLMMatrix[_alternativesWithoutRP.indexOf(a)][criteria.indexOf(c)] = gainsLosses[0] + gainsLosses[1];
					} else {
						_GLMMatrix[_alternativesWithoutRP.indexOf(a)][criteria.indexOf(c)] = 0d;
					}
	
					_GLM.put(pair, gainsLosses);
				}
			}
		}
	}

	public Double[][] normalizeVMatrix() {
		_VN = new Double[_alternativesWithoutRP.size()][_elementsSet.getCriteria().size()];
		
		Double max;;
		Double[] maxValues = new Double[_elementsSet.getCriteria().size()];
		for(int c = 0; c < _elementsSet.getCriteria().size(); ++c) {
			max = Double.NEGATIVE_INFINITY;
			for(int a = 0; a < _alternativesWithoutRP.size(); ++a) {
				if(max < Math.abs(_V[a][c])) {
					max = Math.abs(_V[a][c]);
				}
			}
			
			maxValues[c] = max;
		}
		
		for (int r = 0; r < _alternativesWithoutRP.size(); ++r) {
			for (int c = 0; c < _elementsSet.getCriteria().size(); ++c) {
				if(!_elementsSet.getCriteria().get(c).hasSubcriteria()) {
					_VN[r][c] = _V[r][c] / maxValues[c];
				}
			}
		}
		
		return _VN;
	}

	@Override
	public void clear() {
		_grps.clear();
		_GLM.clear();
		_GLMMatrix = new Double[0][0];
		_V = new Double[0][0];
		_VN = new Double[0][0];
		
		_alternativesWithoutRP = new LinkedList<Alternative>();
		_alternativesWithoutRP.addAll(_elementsSet.getAlternatives());
		_alternativesWithoutRP.remove(_elementsSet.getAlternative("RP"));
	}

	@Override
	public IPhaseMethod clone() {
		ComputingGainsAndLossesPhase result = null;

		try {
			result = (ComputingGainsAndLossesPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public IPhaseMethod copyStructure() {
		return new ComputingGainsAndLossesPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		ComputingGainsAndLossesPhase computingGainsAndLosses = (ComputingGainsAndLossesPhase) iPhaseMethod;

		clear();

		_grps = computingGainsAndLosses.getGRPs();
		_GLM = computingGainsAndLosses.getGLM();
		_GLMMatrix = computingGainsAndLosses.getGLMMatrix();
		_V = computingGainsAndLosses.getVMatrix();
		_VN = computingGainsAndLosses.getNormalizedVMatrix();
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
