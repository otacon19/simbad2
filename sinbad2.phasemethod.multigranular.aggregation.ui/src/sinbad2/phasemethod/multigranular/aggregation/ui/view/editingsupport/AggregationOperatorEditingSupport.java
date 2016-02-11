package sinbad2.phasemethod.multigranular.aggregation.ui.view.editingsupport;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.multigranular.aggregation.AggregationPhase;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.AggregationProcess;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.dialog.QuantifiersDialog;
import sinbad2.phasemethod.multigranular.aggregation.ui.view.dialog.WeightsDialog;

public class AggregationOperatorEditingSupport extends EditingSupport {

	private CellEditor _cellEditor;
	
	private AggregationPhase _aggregationPhase;
	private String[] _aggregationOperatorsNames;
	private Set<String> _aggregationOperatorsIds;
	private List<Double> _weights;
	private Map<String, List<Double>> _mapWeights;
	private String _type;
	private boolean _abort;
	private boolean _assignAll;

	private AggregationOperatorsManager _aggregationOperatorsManager;
	
	private final TreeViewer _viewer;
	
	private AggregationProcess _aggregationProcess;
	
	private static ProblemElement[] getLeafElements(ProblemElement root) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if (root instanceof Expert || root == null) {
			List<Expert> childrens = elementsSet.getExpertChildren((Expert) root);
			for (Expert children : childrens) {
				if (!children.hasChildrens()) {
					result.add(children);
				} else {
					ProblemElement[] subchildrens = getLeafElements(children);
					for (ProblemElement subchildren : subchildrens) {
						result.add(subchildren);
					}
				}
			}

		} else if(root instanceof Criterion){
			List<Criterion> subcriteria = elementsSet.getCriterionSubcriteria((Criterion) root);
			for (Criterion subcriterion : subcriteria) {
				if (!subcriterion.hasSubcriteria()) {
					result.add(subcriterion);
				} else {
					ProblemElement[] sub2criteria = getLeafElements(subcriterion);
					for (ProblemElement sub2criterion : sub2criteria) {
						result.add(sub2criterion);
					}
				}
			}

		}

		return result.toArray(new ProblemElement[0]);
	}

	public AggregationOperatorEditingSupport(AggregationPhase aggregationPhase, AggregationProcess aggregationProcess, TreeViewer viewer, String type) {
		super(viewer);

		_type = type;
		_aggregationOperatorsIds = null;
		_aggregationPhase = aggregationPhase;
		_aggregationProcess = aggregationProcess;
		_aggregationOperatorsManager = AggregationOperatorsManager.getInstance();

		_viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		
		if(_aggregationOperatorsIds == null) {
			_aggregationOperatorsIds = new HashSet<String>();
			MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();	
			Set<EAggregationOperatorType> operatorsTypes = methodsUIManager.getActivateMethodUI().getMethod().getAggregationTypesSupported();
	
			String[] operatorsIds;
			for(EAggregationOperatorType operatorType: operatorsTypes) {
				operatorsIds = _aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
				for(String operator: operatorsIds) {
					_aggregationOperatorsIds.add(operator);
				}
			}
			
			_aggregationOperatorsNames = new String[_aggregationOperatorsIds.size()];
			AggregationOperator operator;
			for(int i = 0; i < _aggregationOperatorsIds.size(); i++) {
				operator = _aggregationOperatorsManager.getAggregationOperator((String) _aggregationOperatorsIds.toArray()[i]);
				if (operator instanceof WeightedAggregationOperator) {
					_aggregationOperatorsNames[i] = "(W) " + _aggregationOperatorsManager.getAggregationOperator((String) _aggregationOperatorsIds.toArray()[i]).getName();
				} else {
					_aggregationOperatorsNames[i] = _aggregationOperatorsManager.getAggregationOperator((String) _aggregationOperatorsIds.toArray()[i]).getName();
				}
			}
		}
		
		_cellEditor = new ComboBoxCellEditor(_viewer.getTree(), _aggregationOperatorsNames);
		
		return _cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (AggregationPhase.EXPERTS.equals(_type) || AggregationPhase.CRITERIA.equals(_type)) {
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return getPos(element);
	}

	private int getPos(Object element) {
		String auxItems[] = ((ComboBoxCellEditor) _cellEditor).getItems();
		String items[] = new String[auxItems.length];
		for (int i = 0; i < auxItems.length; i++) {
			if (auxItems[i].startsWith("(W) ")) { //$NON-NLS-1$
				items[i] = auxItems[i].substring(4);
			} else {
				items[i] = auxItems[i];
			}
			items[i] = items[i].toLowerCase();
		}
		int pos = 0;
		int length = auxItems.length;
		if (length == 0) {
			return -1;
		}

		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		AggregationOperator operator;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);
		}
		if (operator != null) {
			while (!items[pos].equals(operator.getId())) {
				pos++;
				if (pos == length) {
					return -1;
				}
			}
			return pos;
		} else {
			return -1;
		}

	}

	private ProblemElement[] setOperator(ProblemElement element, AggregationOperator aggregationOperator) {
		ProblemElement[] result = new ProblemElement[] {};

		AggregationOperator operator = null;
		operator = aggregationOperator;
		
		if (aggregationOperator instanceof WeightedAggregationOperator) {

			String elementType;
			String elementId;

			if (AggregationPhase.EXPERTS.equals(_type)) {
				elementType = "Expert";
			} else {
				elementType = "Criterion";
			}

			if (element == null) {
				if (AggregationPhase.EXPERTS.equals(_type)) {
					elementId = "All experts";
				} else {
					elementId = "All criteria";
				}
			} else {
				elementId = element.getId();
			}

			if (aggregationOperator.getName().equals("Owa")) { //$NON-NLS-1$
				if (_weights == null) {
					QuantifiersDialog dialog = new QuantifiersDialog(Display.getCurrent().getActiveShell(), null, null, QuantifiersDialog.SIMPLE, elementType, elementId);

					int exitValue = dialog.open();
					if (exitValue == QuantifiersDialog.SAVE) {
						_mapWeights = null;
						_weights = new LinkedList<Double>();
						_weights.add(dialog.getAlpha());
						_weights.add(dialog.getBeta());
						operator = aggregationOperator;
					} else if (exitValue == QuantifiersDialog.CANCEL_ALL) {
						_weights = null;
						_mapWeights = null;
						_abort = true;
					}
				} else {
					operator = aggregationOperator;
				}
			} else {
				_weights = null;
				_mapWeights = null;
				if (aggregationOperator.getName().equals("Weighted mean")) { //$NON-NLS-1$
					ProblemElement nullElement = null;
					ProblemElement[] secondary = getLeafElements(nullElement);
					
					ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
					ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
					
					WeightsDialog dialog; 
					if(elementType.equals("Expert")) {
						dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getElementExpertChildren((Expert) element), secondary, null, QuantifiersDialog.SIMPLE, elementType, elementId);
					} else {
						dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getElementCriterionSubcriteria((Criterion) element), secondary, null, QuantifiersDialog.SIMPLE, elementType, elementId);
					}
						
					int exitValue = dialog.open();
					if(exitValue == WeightsDialog.SAVE) {
						_mapWeights = dialog.getWeights();
						_weights = null;
						operator = aggregationOperator;
					} else if(exitValue == QuantifiersDialog.CANCEL_ALL) {
						_mapWeights = null;
						_weights = null;
						_abort = true;
					}
				}
			}
		} else {
			operator = aggregationOperator;
		}

		if (AggregationPhase.EXPERTS.equals(_type)) {
			if (_mapWeights != null) {
				result = _aggregationPhase.setExpertOperator(element, operator, _mapWeights);
			} else {
				result = _aggregationPhase.setExpertOperator(element, operator, _weights);
			}
		} else {
			if (_mapWeights != null) {
				result = _aggregationPhase.setCriterionOperator(element, operator, _mapWeights);
			} else {
				result = _aggregationPhase.setCriterionOperator(element, operator, _weights);
			}
		}

		if (result.length > 0) {
			if (!_assignAll) {
				MessageDialog dg = new MessageDialog(Display.getCurrent().getActiveShell(), "Assign the operator to all elements", null, "Assign the operator to all elements",
						MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);

				switch (dg.open()) {
				case 0:
					// yes
					_assignAll = true;
					break;
				case 1:
					// no
					_abort = true;
					break;
				}
			}
		}

		return result;
	}

	@Override
	protected void setValue(Object element, Object value) {
	
		if ((element == null) || (value == null)) {
			return;
		}

		int oldValue = (Integer) getPos(element);
		int newValue = (Integer) value;

		if (newValue == oldValue) {
			return;
		}

		ProblemElement pe = null;
		if (element instanceof ProblemElement) {
			pe = (ProblemElement) element;
		}

		String id = (String) _aggregationOperatorsIds.toArray()[newValue];
		id = id.toLowerCase();
		AggregationOperator operator = _aggregationOperatorsManager.getAggregationOperator(id);
		
		_abort = false;
		_assignAll = false;
		for (ProblemElement son : setOperator(pe, operator)) {
			if (!_abort) {
				for (ProblemElement sonSon : setOperator(son, operator)) {
					if (!_abort) {
						setOperator(sonSon, operator);
					}
				}
			}
		}

		_aggregationProcess.completed(true);
		_aggregationProcess.notifyStepStateChange();
	}
}

