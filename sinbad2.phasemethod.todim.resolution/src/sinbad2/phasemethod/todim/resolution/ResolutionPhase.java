package sinbad2.phasemethod.todim.resolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class ResolutionPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution";
	
	private Double[][] _consensusMatrix;
	private int _numAlternatives;
	private int _numCriteria;
	private int _referenceCriterion;
	private List<Double> _globalWeights;
	
	private ProblemElementsSet _elementsSet;
	
	public ResolutionPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_numAlternatives = _elementsSet.getAlternatives().size();
		_numCriteria = _elementsSet.getAllCriteria().size();
		
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_globalWeights = new LinkedList<Double>();
		
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
	
	public Map<String, Double> calculateRelativeWeights() {
		Map<String, Double> result = new HashMap<String, Double>();
		
		if(_referenceCriterion != -1) {
			Double weightReference = _globalWeights.get(_referenceCriterion);
			List<Criterion> criteria = _elementsSet.getAllCriteria();
			for(int i = 0; i < _elementsSet.getAllCriteria().size(); ++i) {
				result.put(criteria.get(i).getCanonicalId(), _globalWeights.get(i) / weightReference);
			}
		}
		
		return result;
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
	}
	
	@Override
	public void clear() {	
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_globalWeights.clear();
		_referenceCriterion = -1;
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
