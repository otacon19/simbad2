package sinbad2.resolutionphase.sensitivityanalysis;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class SensitivityAnalysis implements IResolutionPhase {

	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis";

	private int _numberOfAlternatives;
	private int _numberOfCriteria;
	
	private boolean _aplicatedWeights;

	private double[] _w;
	private double[][] _decisionMatrix;

	private double[] _alternativesFinalPreferences;
	private int[] _ranking;
	private Double[][][] _minimumAbsoluteChangeInCriteriaWeights;
	private Double[][][] _minimumPercentChangeInCriteriaWeights;
	private List<Integer> _absoluteTop;
	private List<Integer> _absoluteAny;

	private ProblemElementsSet _elementsSet;

	public List<ISensitivityAnalysisChangeListener> _listeners;

	public SensitivityAnalysis() {
		_w = null;
		
		_absoluteTop = new LinkedList<Integer>();
		_absoluteAny = new LinkedList<Integer>();
		
		_aplicatedWeights = false;

		_listeners = new LinkedList<ISensitivityAnalysisChangeListener>();
	}

	public int getNumAlternatives() {
		return _numberOfAlternatives;
	}

	public void setNumAlternatives(int numberOfAlternatives) {
		_numberOfAlternatives = numberOfAlternatives;
	}

	public int getNumCriteria() {
		return _numberOfCriteria;
	}

	public void setNumCriteria(int numberOfCriteria) {
		_numberOfCriteria = numberOfCriteria;
	}

	public double[] getWeights() {
		return _w;
	}

	public void setWeights(double[] w) {
		_w = w;
	}

	public double[][] getDecisionMatrix() {
		return _decisionMatrix;
	}

	public void setDecisionMatrix(double[][] dm) {
		_decisionMatrix = dm;
	}

	public double[] getAlternativesFinalPreferences() {
		return _alternativesFinalPreferences;
	}

	public void setAlternativesFinalPreferences(double[] alternativesFinalPreferences) {
		_alternativesFinalPreferences = alternativesFinalPreferences;
	}

	public int[] getRanking() {
		return _ranking;
	}

	public void setRanking(int[] ranking) {
		_ranking = ranking;
	}

	public Double[][][] getMinimumAbsoluteChangeInCriteriaWeights() {
		return _minimumAbsoluteChangeInCriteriaWeights;
	}

	public void setMinimunAbsoluteChangeInCriteriaWeights(Double[][][] minimumAbsoluteChangeInCriteriaWeights) {
		_minimumAbsoluteChangeInCriteriaWeights = minimumAbsoluteChangeInCriteriaWeights;
	}

	public Double[][][] getMinimumPercentChangeInCriteriaWeights() {
		return _minimumPercentChangeInCriteriaWeights;
	}

	public void setMinimunPercentChangeInCriteriaWeights(Double[][][] minimumPercentChangeInCriteriaWeights) {
		_minimumPercentChangeInCriteriaWeights = minimumPercentChangeInCriteriaWeights;
	}

	public List<Integer> getAbsoluteTop() {
		return _absoluteTop;
	}

	public void setAbsoluteTop(List<Integer> absoluteTop) {
		_absoluteTop = absoluteTop;
	}

	public List<Integer> getAbsoluteAny() {
		return _absoluteAny;
	}

	public void setAbsoluteAny(List<Integer> absoluteAny) {
		_absoluteAny = absoluteAny;
	}

	public String[] getAlternativesIds() {
		String[] alternativesIds = new String[_elementsSet.getAlternatives().size()];

		int cont = 0;
		for (Alternative a : _elementsSet.getAlternatives()) {
			alternativesIds[cont] = a.getId();
			cont++;
		}

		return alternativesIds;
	}

	public String[] getCriteriaIds() {
		String[] criteriaIds = new String[_elementsSet.getAllCriteria().size()];

		int cont = 0;
		for (Criterion c : _elementsSet.getCriteria()) {
			criteriaIds[cont] = c.getCanonicalId();
			cont++;
		}

		return criteriaIds;
	}

	@SuppressWarnings("unchecked")
	public void calculateDecisionMatrix() {
		_numberOfAlternatives = _elementsSet.getAlternatives().size();
		_numberOfCriteria = _elementsSet.getAllCriteria().size();
		
		_aplicatedWeights = false;
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		AggregationPhase aggregationPhase = (AggregationPhase) pmm.getPhaseMethod(AggregationPhase.ID).getImplementation();
		
		if(aggregationPhase.getAggregatedValuationsAlternativeCriterion() != null) {
			_decisionMatrix = aggregationPhase.getAggregatedValuationsAlternativeCriterion();
			
			normalizeDecisionMatrix();

			if((aggregationPhase.getCriteriaOperatorWeights().get(null) == null)) {
				createDefaultWeights();
			} else {
				List<Double> weights = ((Map<Object, List<Double>>) aggregationPhase.getCriteriaOperatorWeights().get(null)).get(null);
				_w = new double[_numberOfCriteria];
				for(int i = 0; i < weights.size(); ++i) {
					_w[i] = weights.get(i);
				}
				_aplicatedWeights = true;
			}
		} else {
			createDefaultWeights();
			createDefaultDecisionMatrix();
			notifyAggregationOperatorNoSelected();
		}
		
		compute();
	}

	private void normalizeDecisionMatrix() {
		double acum, noStandarizedValue;
		for(int i = 0; i < _numberOfCriteria; ++i) {
			acum = calculateStandarizeCriterion(i);
			for(int j= 0; j < _numberOfAlternatives; ++j) {
				noStandarizedValue = _decisionMatrix[i][j];
				_decisionMatrix[i][j] = (double) Math.round((noStandarizedValue / acum) * 100) / 100;
			}
		}
	}

	private double calculateStandarizeCriterion(int numCriterion) {
		double value = 0; 
		for(int j= 0; j < _numberOfAlternatives; ++j) {
			value += Math.pow(_decisionMatrix[numCriterion][j], 2);
		}
		return Math.sqrt(value);
	}

	private void createDefaultWeights() {
		_w = new double[_numberOfCriteria];
		double tempW = 1d / (double) _numberOfCriteria;
		for (int i = 0; i < _numberOfCriteria; i++) {
			_w[i] = tempW;
		}
	}

	private void createDefaultDecisionMatrix() {
		_decisionMatrix = new double[_numberOfCriteria][_numberOfAlternatives];
		double tempW = 1d / (double) _numberOfCriteria;
		for(int j = 0; j < _numberOfAlternatives; ++j) {
			for (int i = 0; i < _numberOfCriteria; i++) {
				_decisionMatrix[i][j] = tempW;
			}
		}
	}
	
	private void normalize(double[] values) {

		double sum = 0;

		for (double value : values) {
			sum += value;
		}

		if (sum != 0) {
			for (int i = 0; i < values.length; i++) {
				values[i] /= sum;
			}
		}
	}

	private void computeRanking() {

		_ranking = new int[_numberOfAlternatives];

		List<Double> preferences = new LinkedList<Double>();
		for (double preference : _alternativesFinalPreferences) {
			preferences.add(new Double(preference));
		}

		Collections.sort(preferences);
		Collections.reverse(preferences);

		int rankingPos = 0;
		double previousPreference = 0;

		for (double preference : preferences) {
			rankingPos++;

			if (preference != previousPreference) {
				for (int alternative = 0; alternative < _numberOfAlternatives; alternative++) {
					if (_alternativesFinalPreferences[alternative] == preference) {
						_ranking[alternative] = rankingPos;
					}
				}
				previousPreference = preference;
			}
		}
	}

	// Weighted sum
	private void computeFinalPreferences() {
		_alternativesFinalPreferences = new double[_numberOfAlternatives];
		
		for (int alternative = 0; alternative < _numberOfAlternatives; alternative++) {
			_alternativesFinalPreferences[alternative] = 0;
			for (int criterion = 0; criterion < _numberOfCriteria; criterion++) {
				if(!_aplicatedWeights) {
					_alternativesFinalPreferences[alternative] += _decisionMatrix[criterion][alternative] * _w[criterion];
				} else {
					_alternativesFinalPreferences[alternative] += _decisionMatrix[criterion][alternative];
				}
			}
		}
		
		normalize(_alternativesFinalPreferences);
		
		computeRanking();
	}

	private void computeMinimumAbsoluteChangeInCriteriaWeights() {

		_minimumAbsoluteChangeInCriteriaWeights = new Double[_numberOfAlternatives][_numberOfAlternatives][_numberOfCriteria];
		for (int i = 0; i < _numberOfAlternatives; i++) {
			for (int j = 0; j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {
					_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
				}
			}
		}

		for (int i = 0; i < (_numberOfAlternatives - 1); i++) {
			for (int j = (i + 1); j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {

					if (_decisionMatrix[k][j] - _decisionMatrix[k][i] == 0) {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
					} else {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = (_alternativesFinalPreferences[j] - _alternativesFinalPreferences[i]) / (_decisionMatrix[k][j] - _decisionMatrix[k][i]);

						if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] > _w[k]) {
							_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
						}
					}
				}
			}
		}
	}

	private void computeMinimumPercentChangeInCriteriaWeights() {

		_minimumPercentChangeInCriteriaWeights = new Double[_numberOfAlternatives][_numberOfAlternatives][_numberOfCriteria];
		for (int i = 0; i < _numberOfAlternatives; i++) {
			for (int j = 0; j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {
					if (_minimumAbsoluteChangeInCriteriaWeights[i][j][k] != null) {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = _minimumAbsoluteChangeInCriteriaWeights[i][j][k] * 100d / _w[k];
					} else {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
					}
				}
			}
		}
	}
	
	public double[] getMinimumPercentPairAlternatives(int a1, int a2) {
		double[] percents = new double[_numberOfCriteria];
		for(int k = 0; k < _numberOfCriteria; k++) {
			if(_minimumPercentChangeInCriteriaWeights[a1][a2][k] != null) {
				percents[k] = _minimumPercentChangeInCriteriaWeights[a1][a2][k];
			}
		}
		
		return percents;
	}
	
	public double[] getMinimumAbsolutePairAlternatives(int a1, int a2) {
		double[] absolute = new double[_numberOfCriteria];
		for(int k = 0; k < _numberOfCriteria; k++) {
			if(_minimumAbsoluteChangeInCriteriaWeights[a1][a2][k] != null) {
				absolute[k] = _minimumAbsoluteChangeInCriteriaWeights[a1][a2][k];
			}
		}
		
		return absolute;
	}

	private void computeAbsoluteTop() {
		List<Integer> bestAlternatives = new LinkedList<Integer>();

		for (int i = 0; i < _numberOfAlternatives; i++) {
			if (_ranking[i] == 1) {
				bestAlternatives.add(new Integer(i));
			}
		}

		Double minimum = null;
		Double aux = null;
		for (int i = 0; i < bestAlternatives.size(); i++) {
			for (int j = 0; j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[bestAlternatives.get(i)][j][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteTop.add(new Integer(k));
						}
					}
				}
			}
			for (int j = 0; j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[j][bestAlternatives.get(i)][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteTop = new LinkedList<Integer>();
							_absoluteTop.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteTop.add(new Integer(k));
						}
					}
				}
			}
		}
	}

	private void computeAbsoluteAny() {
		Double minimum = null;
		Double aux = null;

		for (int i = 0; i < _numberOfAlternatives; i++) {
			for (int j = 0; j < _numberOfAlternatives; j++) {
				for (int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[i][j][k];
					if (aux != null) {
						aux = Math.abs(aux);
						if (minimum == null) {
							minimum = aux;
							_absoluteAny = new LinkedList<Integer>();
							_absoluteAny.add(new Integer(k));
						} else if (aux < minimum) {
							minimum = aux;
							_absoluteAny = new LinkedList<Integer>();
							_absoluteAny.add(new Integer(k));
						} else if (aux == minimum) {
							_absoluteAny.add(new Integer(k));
						}
					}
				}
			}
		}
	}

	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {

		if (event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
	}
	
	@Override
	public IResolutionPhase copyStructure() {
		return new SensitivityAnalysis();
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		SensitivityAnalysis sa = (SensitivityAnalysis) iResolutionPhase;

		clear();

		_absoluteAny = sa.getAbsoluteAny();
		_absoluteTop = sa.getAbsoluteTop();
		_alternativesFinalPreferences = sa.getAlternativesFinalPreferences();
		_decisionMatrix = sa.getDecisionMatrix();
		_minimumAbsoluteChangeInCriteriaWeights = sa.getMinimumAbsoluteChangeInCriteriaWeights();
		_minimumPercentChangeInCriteriaWeights = sa.getMinimumPercentChangeInCriteriaWeights();
		_numberOfAlternatives = sa.getNumAlternatives();
		_numberOfCriteria = sa.getNumCriteria();
		_ranking = sa.getRanking();
		_w = sa.getWeights();
	}

	@Override
	public void clear() {
		_absoluteAny.clear();
		_absoluteTop.clear();
		_alternativesFinalPreferences = null;
		_decisionMatrix = null;
		_minimumAbsoluteChangeInCriteriaWeights = null;
		_minimumPercentChangeInCriteriaWeights = null;
		_numberOfAlternatives = -1;
		_numberOfCriteria = -1;
		_ranking = null;
		_w = null;
	}

	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		@SuppressWarnings("unused")
		XMLStreamWriter streamWriter = writer.getStreamWriter();
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);

		return hcb.toHashCode();
	}

	@Override
	public IResolutionPhase clone() {
		SensitivityAnalysis result = null;

		try {
			result = (SensitivityAnalysis) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean validate() {
		if (_elementsSet.getAlternatives().isEmpty()) {
			return false;
		}

		if (_elementsSet.getCriteria().isEmpty()) {
			return false;
		}

		if (_elementsSet.getExperts().isEmpty()) {
			return false;
		}

		return true;
	}


	@Override
	public void activate() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	}

	public void compute() {
		normalize(_w);
		computeFinalPreferences();
		computeMinimumAbsoluteChangeInCriteriaWeights();
		computeMinimumPercentChangeInCriteriaWeights();
		computeAbsoluteTop();
		computeAbsoluteAny();

		notifySensitivityAnalysisChange();
	}

	public void registerSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.add(listener);
	}

	public void unregisterSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.remove(listener);
	}

	public void notifySensitivityAnalysisChange() {

		for (ISensitivityAnalysisChangeListener listener : _listeners) {
			listener.notifySensitivityAnalysisChange();
		}
	}
	
	public void notifyAggregationOperatorNoSelected() {

		for (ISensitivityAnalysisChangeListener listener : _listeners) {
			listener.notifyAggregationOperatorNoSelected();
		}
	}
}
