package sinbad2.phasemethod.todim.resolution.ui.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriteriaTableContentProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionIdColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionWeightColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.ReferenceCriterionEditingSupport;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.ReferenceCriterionWeightColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.RelativeWeightCriterionColumnLabelProvider;

public class CriteriaWeightsView extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution.ui.view.criteriaweights";

	private Composite _parent;
	
	private DecisionMatrixTable _dmTable;
	private TableViewer _criteriaTableViewer;
	
	private ResolutionPhase _resolutionPhase;
	
	private ProblemElementsSet _elementsSet;
	
	public CriteriaWeightsView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_resolutionPhase = (ResolutionPhase) pmm.getPhaseMethod(ResolutionPhase.ID).getImplementation();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout(1, true));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite decisionMatrixComposite = new Composite(_parent, SWT.NONE);
		decisionMatrixComposite.setLayout(new GridLayout(1, true));
		decisionMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		_dmTable = new DecisionMatrixTable(decisionMatrixComposite);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));	
		
		Composite criteriaComposite = new Composite(_parent, SWT.NONE);
		criteriaComposite.setLayout(new GridLayout(1, true));
		criteriaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		_criteriaTableViewer = new TableViewer(_parent);
		_criteriaTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_criteriaTableViewer.setContentProvider(new CriteriaTableContentProvider());
		_criteriaTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn criterionId = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionId.getColumn().setText("Id");
		criterionId.setLabelProvider(new CriterionIdColumnLabelProvider());
		criterionId.getColumn().pack();
		
		TableViewerColumn criterionWeight = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionWeight.getColumn().setText("Weight");
		criterionWeight.setLabelProvider(new CriterionWeightColumnLabelProvider());
		criterionWeight.getColumn().pack();
		
		TableViewerColumn criterionReference = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionReference.getColumn().setText("Reference criterion");
		criterionReference.setLabelProvider(new ReferenceCriterionWeightColumnLabelProvider());
		criterionReference.setEditingSupport(new ReferenceCriterionEditingSupport(_criteriaTableViewer));
		criterionReference.getColumn().pack();
		
		TableViewerColumn relativeWeight = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		relativeWeight.getColumn().setText("Relative weight");
		relativeWeight.setLabelProvider(new RelativeWeightCriterionColumnLabelProvider());
		relativeWeight.getColumn().pack();
	
		refreshTable();
		
		setInputCriteriaTable();
	}

	private void refreshTable() {		
		String[] alternatives = new String[_elementsSet.getAlternatives().size()];
		for(int a = 0; a < alternatives.length; ++a) {
			alternatives[a] = _elementsSet.getAlternatives().get(a).getId();
		}
		
		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for(int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_dmTable.setModel(alternatives, criteria, _resolutionPhase.getConsensusMatrix());
	}
	
	private void setInputCriteriaTable() {
		List<String[]> result = new LinkedList<String[]>();
		
		List<Double> globalWeights = _resolutionPhase.getGlobalWeights();
		
		int indexCriterion = 0;
		for(Criterion c: _elementsSet.getAllCriteria()) {
			String[] row = new String[4];
			row[0] = c.getCanonicalId();
			row[1] = Double.toString(globalWeights.get(indexCriterion));
			row[2] = "false";
			row[3] = "0";
			
			indexCriterion++;
			result.add(row);
		}
		_criteriaTableViewer.setInput(result);
	}
	
	@Override
	public void setFocus() {
		_criteriaTableViewer.getTable().setFocus();
	}
	
	@Override
	public String getPartName() {
		return "Criteria weights";
	}

}
