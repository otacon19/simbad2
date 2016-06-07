package sinbad2.phasemethod.aggregation.ui.view.editingsupport;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.aggregation.ui.nls.Messages;
import sinbad2.phasemethod.aggregation.ui.view.AggregationProcess;
import sinbad2.phasemethod.aggregation.ui.view.dialog.ChoquetIntegralWeightsDialog;
import sinbad2.phasemethod.aggregation.ui.view.dialog.ParametersDialog;
import sinbad2.phasemethod.aggregation.ui.view.dialog.QuantifiersDialog;
import sinbad2.phasemethod.aggregation.ui.view.dialog.WeightsDialog;

public class AggregationOperatorEditingSupport extends EditingSupport {

	private CellEditor _cellEditor;
	
	private AggregationPhase _aggregationPhase;
	private List<String> _aggregationOperatorsIds;
	private List<String> _aggregationOperatorsNames;
	private List<Double> _weights;
	private Map<String, List<Double>> _mapWeights;
	private String _type;
	
	private boolean _abort;
	private boolean _assignAll;

	private AggregationOperatorsManager _aggregationOperatorsManager;
	
	private final TreeViewer _viewer;
	
	private AggregationProcess _aggregationProcess;
	
	private static ProblemElement[] getLeafElements(ProblemElement root, String type) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(type.equals("expert")) { //$NON-NLS-1$
			List<Expert> children = elementsSet.getAllExpertChildren((Expert) root);
			for(Expert child : children) {
				if(!child.hasChildren()) {
					result.add(child);
				} else {
					ProblemElement[] subchildren = getLeafElements(child, "expert"); //$NON-NLS-1$
					for (ProblemElement subchild : subchildren) {
						result.add(subchild);
					}
				}
			}

		} else {
			List<Criterion> subcriteria = elementsSet.getAllCriterionSubcriteria((Criterion) root);
			for(Criterion subcriterion : subcriteria) {
				if(!subcriterion.hasSubcriteria()) {
					result.add(subcriterion);
				} else {
					ProblemElement[] sub2criteria = getLeafElements(subcriterion, "criterion"); //$NON-NLS-1$
					for(ProblemElement sub2criterion : sub2criteria) {
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
		
		_abort = false;
		_assignAll = false;
		
		_cellEditor = null;

		_viewer = viewer;
	}

	protected CellEditor getCellEditor(Object element) {
		
		if(_aggregationOperatorsIds == null) {
			
			Set<String> aOperatorsIds = new TreeSet<String>();
			List<String> weightedAggregationOperatorsNames = new LinkedList<String>();
			List<String> weightedAggregationOperatorsIds = new LinkedList<String>();
			_aggregationOperatorsNames = new LinkedList<String>();
			_aggregationOperatorsIds = new LinkedList<String>();
			
			MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();	
			Set<EAggregationOperatorType> operatorsTypes = methodsUIManager.getActivateMethodUI().getMethod().getAggregationTypesSupported();
			String[] operatorsIds;
			for(EAggregationOperatorType operatorType: operatorsTypes) {
				operatorsIds = _aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
				for(String operator: operatorsIds) {
					aOperatorsIds.add(operator);
				}
			}
			
			AggregationOperator operator;
			for(int i = 0; i < aOperatorsIds.size(); i++) {
				operator = _aggregationOperatorsManager.getAggregationOperator((String) aOperatorsIds.toArray()[i]);
				if (operator instanceof WeightedAggregationOperator) {
					weightedAggregationOperatorsNames.add("(W) " + _aggregationOperatorsManager.getAggregationOperator((String) aOperatorsIds.toArray()[i]).getName()); //$NON-NLS-1$
					weightedAggregationOperatorsIds.add(operator.getId());
				} else {
					_aggregationOperatorsNames.add(_aggregationOperatorsManager.getAggregationOperator((String) aOperatorsIds.toArray()[i]).getName());
					_aggregationOperatorsIds.add(operator.getId());
				}
			}
			
			_aggregationOperatorsNames.addAll(weightedAggregationOperatorsNames);
			_aggregationOperatorsIds.addAll(weightedAggregationOperatorsIds);
		}
		
		_cellEditor = new ComboBoxCellEditor(_viewer.getTree(), _aggregationOperatorsNames.toArray(new String[0]), SWT.READ_ONLY);
		
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
				elementType = Messages.AggregationOperatorEditingSupport_Expert;
			} else {
				elementType = Messages.AggregationOperatorEditingSupport_Criterion;
			}

			if (element == null) {
				if (AggregationPhase.EXPERTS.equals(_type)) {
					elementId = Messages.AggregationOperatorEditingSupport_All_experts;
				} else {
					elementId = Messages.AggregationOperatorEditingSupport_All_criteria;
				}
			} else {
				elementId = element.getId();
			}
			
			if (aggregationOperator.getName().equals("OWA")) { //$NON-NLS-1$
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
				if(aggregationOperator.getName().equals("Choquet integral")) {
					
					ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
					ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
					
					ChoquetIntegralWeightsDialog dialog;
					if(elementType.equals("Expert")) { //$NON-NLS-1$
						dialog = new ChoquetIntegralWeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertChildren((Expert) element),
								ChoquetIntegralWeightsDialog.SIMPLE, elementType, elementId);
					} else {
						dialog = new ChoquetIntegralWeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) element),
								ChoquetIntegralWeightsDialog.SIMPLE, elementType, elementId);
					}

					int exitValue = dialog.open();
					if (exitValue == ChoquetIntegralWeightsDialog.SAVE) {
						_mapWeights = null;
						_weights = dialog.getWeights();
						operator = aggregationOperator;
					} else if (exitValue == ChoquetIntegralWeightsDialog.CANCEL_ALL) {
						_weights = null;
						_mapWeights = null;
						_abort = true;
					}
				} else {
					_weights = null;
					_mapWeights = null;
						
					ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
					ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
					
					ProblemElement nullElement = null;
					ProblemElement[] secondary;
					
					WeightsDialog dialog; 
					if(elementType.equals("Expert")) { //$NON-NLS-1$
						secondary = getLeafElements(nullElement, "criterion"); //$NON-NLS-1$
						dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertChildren((Expert) element), secondary, null, QuantifiersDialog.SIMPLE, elementType, elementId);
					} else {
						secondary = getLeafElements(nullElement, "expert"); //$NON-NLS-1$
						dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) element), secondary, null, QuantifiersDialog.SIMPLE, elementType, elementId);
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
				
				if(operator.hasParameters()) {
					ParametersDialog dialogP = new ParametersDialog(Display.getCurrent().getActiveShell());
					if(dialogP.open() == ParametersDialog.SAVE) {
						operator.setParameters(dialogP.getParameters());
					}
				}
			}
		} else {
			operator = aggregationOperator;
			
			if(operator.hasParameters()) {
				ParametersDialog dialogP = new ParametersDialog(Display.getCurrent().getActiveShell());
				if(dialogP.open() == ParametersDialog.SAVE) {
					operator.setParameters(dialogP.getParameters());
				}
			}
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
				MessageDialog dg = new MessageDialog(Display.getCurrent().getActiveShell(), Messages.AggregationOperatorEditingSupport_Assign_the_operator_to_all_elements, null, Messages.AggregationOperatorEditingSupport_Assign_the_operator_to_all_elements,
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
