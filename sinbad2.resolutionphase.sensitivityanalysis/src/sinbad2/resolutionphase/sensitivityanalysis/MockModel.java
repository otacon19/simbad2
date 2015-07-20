package sinbad2.resolutionphase.sensitivityanalysis;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;

public class MockModel {
	public int _numberOfCriteria = -1;
	public int _numberOfAlternatives = -1;

	public String[] _alternatives = null;
	public String[] _criteria = null;
	public double[] _w = null;
	public double[][] _dm = null;

	public double[] _alternativesFinalPreferences;
	public int[] _ranking;
	public Double[][][] _minimumAbsoluteChangeInCriteriaWeights;
	public Double[][][] _minimumPercentChangeInCriteriaWeights;
	public List<Integer> _absoluteTop = null;
	public List<Integer> _absoluteAny = null;
	
	private void initialize() {
		ProblemElementsManager em = ProblemElementsManager.getInstance();
		ProblemElementsSet es = em.getActiveElementSet();
		
		List<Alternative> as = es.getAlternatives();
		_numberOfAlternatives = as.size();
		_alternatives = new String[_numberOfAlternatives];
		for(int i = 0; i < _numberOfAlternatives; i++) {
			_alternatives[i] = as.get(i).getCanonicalId();
		}
		
		List<Criterion> cr = es.getCriteria();
		_numberOfCriteria = cr.size();
		_criteria = new String[_numberOfCriteria];
		for(int i = 0; i < _numberOfCriteria; i++) {
			_criteria[i] = cr.get(i).getCanonicalId();
		}
		
		_w = new double[_numberOfCriteria];
		double tempW = 1d / (double) _numberOfCriteria;
		for(int i = 0; i < _numberOfCriteria; i++) {
			_w[i] = tempW;
		}
		
		_dm = new double[_numberOfCriteria][_numberOfAlternatives];
		for(int i = 0; i < _numberOfCriteria; i++) {
			for (int j = 0; j < _numberOfAlternatives; j++) {
				_dm[i][j] = tempW;
			}
		}
	}
	
	private void normalize(double[] values) {

		double sum = 0;

		for(double value : values) {
			sum += value;
		}

		if(sum != 0) {
			for(int i = 0; i < values.length; i++) {
				values[i] /= sum;
			}
		}

	}

	private void computeRanking() {

		_ranking = new int[_numberOfAlternatives];

		List<Double> preferences = new LinkedList<Double>();
		for(double preference : _alternativesFinalPreferences) {
			preferences.add(new Double(preference));
		}

		Collections.sort(preferences);
		Collections.reverse(preferences);

		int rankingPos = 0;
		double previousPreference = 0;

		for(double preference : preferences) {
			rankingPos++;

			if(preference != previousPreference) {
				for(int alternative = 0; alternative < _numberOfAlternatives; alternative++) {
					if(_alternativesFinalPreferences[alternative] == preference) {
						_ranking[alternative] = rankingPos;
					}
				}
				previousPreference = preference;
			}

		}
	}

	private void computeFinalPreferences() {
		_alternativesFinalPreferences = new double[_numberOfAlternatives];

		for(int alternative = 0; alternative < _numberOfAlternatives; alternative++) {
			_alternativesFinalPreferences[alternative] = 0;
			for(int criterion = 0; criterion < _numberOfCriteria; criterion++) {
				_alternativesFinalPreferences[alternative] += _dm[alternative][criterion] * _w[criterion];
			}
		}
		computeRanking();
	}

	private void computeMinimumAbsoluteChangeInCriteriaWeights() {

		_minimumAbsoluteChangeInCriteriaWeights = new Double[_numberOfAlternatives][_numberOfAlternatives][_numberOfCriteria];
		for(int i = 0; i < _numberOfAlternatives; i++) {
			for(int j = 0; j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
				}
			}
		}

		for(int i = 0; i < (_numberOfAlternatives - 1); i++) {
			for(int j = (i + 1); j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = (_alternativesFinalPreferences[j] - _alternativesFinalPreferences[i])
							/ (_dm[j][k] - _dm[i][k]);

					if(_minimumAbsoluteChangeInCriteriaWeights[i][j][k] > _w[k]) {
						_minimumAbsoluteChangeInCriteriaWeights[i][j][k] = null;
					}
				}
			}
		}
	}

	private void computeMinimumPercentChangeInCriteriaWeights() {

		_minimumPercentChangeInCriteriaWeights = new Double[_numberOfAlternatives][_numberOfAlternatives][_numberOfCriteria];
		for(int i = 0; i < _numberOfAlternatives; i++) {
			for(int j = 0; j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					if(_minimumAbsoluteChangeInCriteriaWeights[i][j][k] != null) {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = _minimumAbsoluteChangeInCriteriaWeights[i][j][k]
								* 100d / _w[k];
					} else {
						_minimumPercentChangeInCriteriaWeights[i][j][k] = null;
					}
				}
			}
		}
	}

	private void computeAbsoluteTop() {
		List<Integer> bestAlternatives = new LinkedList<Integer>();

		for(int i = 0; i < _numberOfAlternatives; i++) {
			if (_ranking[i] == 1) {
				bestAlternatives.add(new Integer(i));
			}
		}

		Double minimum = null;
		Double aux = null;
		for(int i = 0; i < bestAlternatives.size(); i++) {
			for(int j = 0; j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[bestAlternatives.get(i)][j][k];
					if(aux != null) {
						aux = Math.abs(aux);
						if(minimum == null) {
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
			for(int j = 0; j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[j][bestAlternatives.get(i)][k];
					if(aux != null) {
						aux = Math.abs(aux);
						if(minimum == null) {
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
		
		for(int i = 0; i < _numberOfAlternatives; i++) {
			for(int j = 0; j < _numberOfAlternatives; j++) {
				for(int k = 0; k < _numberOfCriteria; k++) {
					aux = _minimumPercentChangeInCriteriaWeights[i][j][k];
					if(aux != null) {
						aux = Math.abs(aux);
						if(minimum == null) {
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

	public MockModel() {
		initialize();
		normalize(_w);
		computeFinalPreferences();
		computeMinimumAbsoluteChangeInCriteriaWeights();
		computeMinimumPercentChangeInCriteriaWeights();
		computeAbsoluteTop();
		computeAbsoluteAny();
	}
}

