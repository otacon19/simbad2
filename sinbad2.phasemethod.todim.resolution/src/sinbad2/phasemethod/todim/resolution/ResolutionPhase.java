package sinbad2.phasemethod.todim.resolution;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;
import sinbad2.valuation.valuationset.ValuationKey;

public class ResolutionPhase implements IPhaseMethod {

	public static final String ID = "flintstones.phasemethod.todim.resolution"; //$NON-NLS-1$

	private static final Integer ATTENUATION_FACTOR = 1;

	private Object[][] _consensusMatrix;
	private String[][] _trapezoidalConsensusMatrix;
	
	private int _numAlternatives;
	private int _numCriteria;
	private int _referenceCriterion;
	
	private List<Double> _globalWeights;
	private Map<String, Double> _relativeWeights;
	
	private Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> _dominanceDegreeByCriterion;
	private Map<Pair<Alternative, Alternative>, Double> _dominanceDegreeAlternatives;
	private Map<Alternative, Double> _globalDominance;
	private Map<ValuationKey, TrapezoidalFunction> _fuzzyValuations;

	private ProblemElementsSet _elementsSet;

	private static class MapUtil {
		
		public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
			List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
			
			Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
				
				public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});

			Map<K, V> result = new LinkedHashMap<K, V>();
			for (Map.Entry<K, V> entry : list) {
				result.put(entry.getKey(), entry.getValue());
				
			}
			
			return result;
		}
	}

	public ResolutionPhase() {		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllCriteria().size();

		_consensusMatrix = new Object[_numAlternatives][_numCriteria];
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		initializeConsesusMatrix();

		_globalWeights = new LinkedList<Double>();
		_relativeWeights = new HashMap<String, Double>();
		
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();
		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();
		_globalDominance = new HashMap<Alternative, Double>();
		_fuzzyValuations = new HashMap<ValuationKey, TrapezoidalFunction>();

		_referenceCriterion = -1;
	}

	public void setConsensusMatrix(Object[][] consensusMatrix) {
		_consensusMatrix = consensusMatrix;
	}

	public Object[][] getConsensusMatrix() {
		return _consensusMatrix;
	}
	
	public void setTrapezoidalConsensusMatrix(String[][] consensusMatrix) {
		_trapezoidalConsensusMatrix = consensusMatrix;
	}

	public String[][] getTrapezoidalConsensusMatrix() {
		return _trapezoidalConsensusMatrix;
	}

	public void setGlobalWeights(List<Double> globalWeights) {
		_globalWeights = globalWeights;
	}

	public List<Double> getGlobalWeights() {
		return _globalWeights;
	}

	public void setReferenceCriterion(int referenceCriterion) {
		_referenceCriterion = referenceCriterion;
	}

	public int getReferenceCriterion() {
		return _referenceCriterion;
	}

	public void setRelativeWeights(Map<String, Double> relativeWeights) {
		_relativeWeights = relativeWeights;
	}

	public Map<String, Double> getRelativeWeights() {
		return _relativeWeights;
	}

	public void setDominanceDegreeByCriterion(
			Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> dominanceDegreeByCriterion) {
		_dominanceDegreeByCriterion = dominanceDegreeByCriterion;
	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> getDominanceDegreeByCriterion() {
		return _dominanceDegreeByCriterion;
	}

	public void setDominanceDegreeAlternatives(
			Map<Pair<Alternative, Alternative>, Double> dominanceDegreeAlternatives) {
		_dominanceDegreeAlternatives = dominanceDegreeAlternatives;
	}

	public Map<Pair<Alternative, Alternative>, Double> getDominanceDegreeAlternatives() {
		return _dominanceDegreeAlternatives;
	}

	public void setGlobalDominance(Map<Alternative, Double> globalDominance) {
		_globalDominance = globalDominance;
	}

	public Map<Alternative, Double> getGlobalDominance() {
		return _globalDominance;
	}

	public void setFuzzyValuations(Map<ValuationKey, TrapezoidalFunction> fuzzyValuations) {
		_fuzzyValuations = fuzzyValuations;
	}

	public Map<ValuationKey, TrapezoidalFunction> getFuzzyValuations() {
		return _fuzzyValuations;
	}
	
	private void initializeConsesusMatrix() {
		
		for(int a = 0; a < _numAlternatives; ++a) {
			for(int c = 0; c < _numCriteria; ++c) {
				_consensusMatrix[a][c] = "(a,b,c,d)"; //$NON-NLS-1$
			}
		}
	}
	
	public Map<String, Double> calculateRelativeWeights() {
		_relativeWeights = new HashMap<String, Double>();

		if (_referenceCriterion != -1) {
			Double weightReference = _globalWeights.get(_referenceCriterion);
			List<Criterion> criteria = _elementsSet.getAllCriteria();
			for (int i = 0; i < _elementsSet.getAllCriteria().size(); ++i) {
				_relativeWeights.put(criteria.get(i).getCanonicalId(), _globalWeights.get(i) / weightReference);
			}
		}
		return _relativeWeights;
	}

	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> calculateDominanceDegreeByCriterion() {
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();

		double acumSumRelativeWeights = getAcumSumRelativeWeights();

		int criterionIndex = 0, a1Index = 0, a2Index = 0;
		double dominance = 0, condition = 0;
		for (Criterion c : _elementsSet.getAllCriteria()) {
			a1Index = 0;
			for (Alternative a1 : _elementsSet.getAlternatives()) {
				a2Index = 0;
				for (Alternative a2 : _elementsSet.getAlternatives()) {
					
					if (a1 != a2) {
						condition = (Double) _consensusMatrix[a1Index][criterionIndex] - (Double) _consensusMatrix[a2Index][criterionIndex];
						if (condition > 0) {
							dominance = Math.sqrt((condition * _relativeWeights.get(c.getCanonicalId())) / acumSumRelativeWeights);
						} else if (condition < 0) {
							double inverse = (Double) _consensusMatrix[a2Index][criterionIndex] - (Double) _consensusMatrix[a1Index][criterionIndex];
							dominance = (-1d / ATTENUATION_FACTOR);
							dominance *= Math.sqrt((inverse * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
						} else {
							dominance = 0;
						}

						Pair<Alternative, Alternative> pairAlternatives = new Pair<Alternative, Alternative>(a1, a2);
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance;
						if (_dominanceDegreeByCriterion.get(c) != null) {
							pairAlternativesDominance = _dominanceDegreeByCriterion.get(c);
						} else {
							pairAlternativesDominance = new HashMap<Pair<Alternative, Alternative>, Double>();
						}
						pairAlternativesDominance.put(pairAlternatives, dominance);
						_dominanceDegreeByCriterion.put(c, pairAlternativesDominance);
					}
					a2Index++;
				}
				a1Index++;
			}
			criterionIndex++;
		}

		return _dominanceDegreeByCriterion;
	}

	private double getAcumSumRelativeWeights() {
		double acum = 0;

		for (Criterion c : _elementsSet.getAllCriteria()) {
			acum += _relativeWeights.get(c.getCanonicalId());
		}
		return acum;
	}

	public Map<Pair<Alternative, Alternative>, Double> calculateDominaceDegreeAlternatives() {

		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();

		double acum;
		for (Alternative a1 : _elementsSet.getAlternatives()) {
			for (Alternative a2 : _elementsSet.getAlternatives()) {
				acum = 0;
				if (a1 != a2) {
					for (Criterion c : _dominanceDegreeByCriterion.keySet()) {
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance = _dominanceDegreeByCriterion
								.get(c);
						for (Pair<Alternative, Alternative> pair : pairAlternativesDominance.keySet()) {
							if (a1.equals(pair.getLeft()) && a2.equals(pair.getRight())) {
								acum += pairAlternativesDominance.get(pair);
							}
						}
					}
					_dominanceDegreeAlternatives.put(new Pair<Alternative, Alternative>(a1, a2), acum);
				}
			}
		}
		return _dominanceDegreeAlternatives;
	}

	public Map<Alternative, Double> calculateGlobalDominance() {
		Map<Alternative, Double> acumDominanceDegreeAlternatives = new HashMap<Alternative, Double>();

		double acum, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		for (Alternative a : _elementsSet.getAlternatives()) {
			acum = 0;
			for (Pair<Alternative, Alternative> pairAlternatives : _dominanceDegreeAlternatives.keySet()) {
				if (a.equals(pairAlternatives.getLeft())) {
					acum += _dominanceDegreeAlternatives.get(pairAlternatives);
				}
			}

			if (acum < min) {
				min = acum;
			}

			if (acum > max) {
				max = acum;
			}
			
			acumDominanceDegreeAlternatives.put(a, acum);
		}

		double dominance;
		for (Alternative a : acumDominanceDegreeAlternatives.keySet()) {
			dominance = (acumDominanceDegreeAlternatives.get(a) - min) / (max - min);
			_globalDominance.put(a, dominance);
		}

		MapUtil.sortByValue(_globalDominance);
		
		return _globalDominance;
	}

	public Double[][] calculateCOG() {
		Double[][] result = new Double[_numAlternatives][_numCriteria];
		double a, b, c, d, cog;
		String trapezoidalNumber, limits[];
		TrapezoidalFunction semantic;
		
		if(_consensusMatrix != null && _consensusMatrix[0][0] instanceof String) {
			for(int al = 0; al < _numAlternatives; ++al) {
				for(int cr = 0; cr < _numCriteria; ++cr) {
					trapezoidalNumber = (String) _consensusMatrix[al][cr];
					trapezoidalNumber = trapezoidalNumber.replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
					trapezoidalNumber = trapezoidalNumber.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
					limits = trapezoidalNumber.split(","); //$NON-NLS-1$
					a = Double.parseDouble(limits[0]);
					b = Double.parseDouble(limits[1]);
					c = Double.parseDouble(limits[2]);
					d = Double.parseDouble(limits[3]);
					semantic = new TrapezoidalFunction(new double[]{a, b, c, d});
					cog = semantic.centroid();
					result[al][cr] = Math.round(cog * 100d) / 100d;
				}
			}
			
			_consensusMatrix = result;
		}
		
		return (Double[][]) _consensusMatrix;
	}
	
	@Override
	public IPhaseMethod copyStructure() {
		return new ResolutionPhase();
	}

	@Override
	public void copyData(IPhaseMethod iPhaseMethod) {
		ResolutionPhase resolution = (ResolutionPhase) iPhaseMethod;

		clear();

		_consensusMatrix = resolution.getConsensusMatrix();
		_globalWeights = resolution.getGlobalWeights();
		_referenceCriterion = resolution.getReferenceCriterion();
		_relativeWeights = resolution.getRelativeWeights();
		_dominanceDegreeByCriterion = resolution.getDominanceDegreeByCriterion();
		_dominanceDegreeAlternatives = resolution.getDominanceDegreeAlternatives();
		_globalDominance = resolution.getGlobalDominance();
		_trapezoidalConsensusMatrix = resolution.getTrapezoidalConsensusMatrix();
		_fuzzyValuations = resolution.getFuzzyValuations();
	}

	@Override
	public void clear() {
		_consensusMatrix = new Object[_numAlternatives][_numCriteria];
		initializeConsesusMatrix();
		_trapezoidalConsensusMatrix = new String[_numAlternatives][_numCriteria];
		
		_globalWeights.clear();
		_referenceCriterion = -1;
		_relativeWeights.clear();
		_dominanceDegreeByCriterion.clear();
		_dominanceDegreeAlternatives.clear();
		_globalDominance.clear();
		_fuzzyValuations.clear();
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void notifyPhaseMethodStateChange(PhaseMethodStateChangeEvent event) {
		if (event.getChange().equals(EPhaseMethodStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public void activate() {
	}

	@Override
	public IPhaseMethod clone() {
		ResolutionPhase result = null;

		try {
			result = (ResolutionPhase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
}
