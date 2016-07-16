package sinbad2.phasemethod.todim.resolution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.listener.EPhaseMethodStateChange;
import sinbad2.phasemethod.listener.PhaseMethodStateChangeEvent;

public class ResolutionPhase implements IPhaseMethod {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution";
	
	private Double[][] _consensusMatrix;
	private int _numAlternatives;
	private int _numCriteria;
	
	private List<Double> _globalWeights;
	
	public ResolutionPhase() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		_numAlternatives = elementsSet.getAlternatives().size();
		_numCriteria = elementsSet.getAllCriteria().size();
		
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_globalWeights = new LinkedList<Double>();
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
	
	public Map<String, Double> calculateRelativeWeights() {
		Map<String, Double> result = new HashMap<String, Double>();
		
		
		
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
	}
	
	@Override
	public void clear() {	
		_consensusMatrix = new Double[_numAlternatives][_numCriteria];
		_globalWeights.clear();
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
