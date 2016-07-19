package sinbad2.phasemethod.todim.resolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class ResolutionPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution";
	
	private static final Integer ATTENUATION_FACTOR = 1;
	
	private Double[][] _consensusMatrix;
	private int _numAlternatives;
	private int _numCriteria;
	private int _referenceCriterion;
	private List<Double> _globalWeights;
	private Map<String, Double> _relativeWeights;
	private Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> _dominanceDegreeByCriterion;
	private Map<Pair<Alternative, Alternative>, Double> _dominanceDegreeAlternatives;
	private Map<Alternative, Double> _globalDominance;
	
	private ProblemElementsSet _elementsSet;
	
	public ResolutionPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllCriteria().size();
		
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_consensusMatrix[0][0] = 0.2;
		_consensusMatrix[0][1] = 0.5 ;
		_consensusMatrix[1][0] = 0.1;
		_consensusMatrix[1][1] = 0.1;
		_consensusMatrix[2][0] = 0.25;
		_consensusMatrix[2][1] = 0.3;
		
		
		_globalWeights = new LinkedList<Double>();
		_relativeWeights = new HashMap<String, Double>();
		_dominanceDegreeByCriterion = new HashMap<Criterion, Map<Pair<Alternative, Alternative>, Double>>();
		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();
		_globalDominance = new HashMap<Alternative, Double>();
		
		_referenceCriterion = -1;
	}
	
	public void setConsensusMatrix(Double[][] consensusMatrix) {
		_consensusMatrix = consensusMatrix;
	}
	
	public Double[][] getConsensusMatrix() {
		return _consensusMatrix;
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
	
	public void setDominanceDegreeByCriterion(Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> dominanceDegreeByCriterion) {
		_dominanceDegreeByCriterion = dominanceDegreeByCriterion;
	}
	
	public Map<Criterion, Map<Pair<Alternative, Alternative>, Double>>  getDominanceDegreeByCriterion() {
		return _dominanceDegreeByCriterion;
	}
	
	public void setDominanceDegreeAlternatives(Map<Pair<Alternative, Alternative>, Double> dominanceDegreeAlternatives) {
		_dominanceDegreeAlternatives = dominanceDegreeAlternatives;
	}
	
	public Map<Pair<Alternative, Alternative>, Double>  getDominanceDegreeAlternatives() {
		return _dominanceDegreeAlternatives;
	}
	
	public void setGlobalDominance(Map<Alternative, Double> globalDominance) {
		_globalDominance = globalDominance;
	}
	
	public Map<Alternative, Double> getGlobalDominance() {
		return _globalDominance;
	}
	
	public Map<String, Double> calculateRelativeWeights() {
		_relativeWeights = new HashMap<String, Double>();
		
		if(_referenceCriterion != -1) {
			Double weightReference = _globalWeights.get(_referenceCriterion);
			List<Criterion> criteria = _elementsSet.getAllCriteria();
			for(int i = 0; i < _elementsSet.getAllCriteria().size(); ++i) {
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
		for(Criterion c: _elementsSet.getAllCriteria()) {
			a1Index = 0;
			for(Alternative a1: _elementsSet.getAlternatives()) {
				a2Index = 0;
				for(Alternative a2: _elementsSet.getAlternatives()) {
					if(a1 != a2) {
						
						condition = _consensusMatrix[a1Index][criterionIndex] - _consensusMatrix[a2Index][criterionIndex];
						if(condition > 0) {
							dominance = Math.sqrt((condition * _relativeWeights.get(c.getCanonicalId())) / acumSumRelativeWeights);
						} else if(condition < 0) {
							double inverse = _consensusMatrix[a2Index][criterionIndex] - _consensusMatrix[a1Index][criterionIndex];
							dominance = (-1d / ATTENUATION_FACTOR);
							dominance *= Math.sqrt((inverse * acumSumRelativeWeights) / _relativeWeights.get(c.getCanonicalId()));
						} else {
							dominance = 0;
						}
						
						Pair<Alternative, Alternative> pairAlternatives = new Pair<Alternative, Alternative>(a1, a2);
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance;
						if(_dominanceDegreeByCriterion.get(c) != null) {
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
	
	public Map<Pair<Alternative, Alternative>, Double> calculateDominaceDegreeAlternatives() {
		
		_dominanceDegreeAlternatives = new HashMap<Pair<Alternative, Alternative>, Double>();

		double acum;
		for(Alternative a1: _elementsSet.getAlternatives()) {
			for(Alternative a2: _elementsSet.getAlternatives()) {
				acum = 0;
				if(a1 != a2) {
					for(Criterion c: _dominanceDegreeByCriterion.keySet()) {
						Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance = _dominanceDegreeByCriterion.get(c);
						for(Pair<Alternative, Alternative> pair: pairAlternativesDominance.keySet()) {
							if(a1.equals(pair.getLeft()) && a2.equals(pair.getRight())) {
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
		
		double acum;
		for(Alternative a: _elementsSet.getAlternatives()) {
			acum = 0;
			for(Pair<Alternative, Alternative> pairAlternatives: _dominanceDegreeAlternatives.keySet()) {
				if(a.equals(pairAlternatives.getLeft())) {
					acum += _dominanceDegreeAlternatives.get(pairAlternatives);
				}
			}
			
		}
		
		return _globalDominance;
	}

	private double getAcumSumRelativeWeights() {
		double acum = 0;
		
		for(Criterion c: _elementsSet.getAllCriteria()) {
			acum += _relativeWeights.get(c.getCanonicalId());
		}
		return acum;
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
	}
	
	@Override
	public void clear() {	
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_consensusMatrix[0][0] = 0.2;
		_consensusMatrix[0][1] = 0.5 ;
		_consensusMatrix[1][0] = 0.1;
		_consensusMatrix[1][1] = 0.1;
		_consensusMatrix[2][0] = 0.25;
		_consensusMatrix[2][1] = 0.3;
		
		_globalWeights.clear();
		_referenceCriterion = -1;
		_relativeWeights.clear();
		_dominanceDegreeByCriterion.clear();
		_dominanceDegreeAlternatives.clear();
		_globalDominance.clear();
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
	public void activate() {}
	
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
