package sinbad2.phasemethod.emergency.computinggainslosses.ui.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.utils.Pair;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.emergency.computinggainslosses.ComputingGainsAndLossesPhase;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.nls.Messages;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.AlternativeColumnLabelProvider;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.CriterionColumnLabelProvider;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.GLMEditableTable;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.GainsColumnLabelProvider;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.GainsLossesTableContentProvider;
import sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider.LossesColumnLabelProvider;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class ComputingGainsAndLosses extends ViewPart implements IStepStateListener {

public static final String ID = "flintstones.phasemethod.emergency.computinggainsandlosses.ui.view.computinggainsandlosses"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _matrixTablesComposite;
	private TableViewer _gainsLossesTableViewer;
	private GLMEditableTable _GLMMatrixTable;
	private GLMEditableTable _VTable;
	private GLMEditableTable _VNormalizedTable;
	
	private ComputingGainsAndLossesPhase _computingGainsAndLosses;
	
	private ProblemElementsSet _elementsSet;

	private RatingView _ratingView;
	
	private static class ElementComparator implements Comparator<String[]> {

		@Override
		 public int compare(String[] pe1, String[] pe2) {
			if(!pe1[0].equals(pe2[0])) {
				return extractInt(pe1[0]) - extractInt(pe2[0]);
			} else {
				return extractInt(pe1[1]) - extractInt(pe2[1]);
			}
	    }

	    int extractInt(String s) {
	        String num = s.replaceAll("\\D", ""); //$NON-NLS-1$
	        return num.isEmpty() ? 0 : Integer.parseInt(num);
	    }
	}
	
	public ComputingGainsAndLosses() {
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_computingGainsAndLosses = (ComputingGainsAndLossesPhase) pmm.getPhaseMethod(ComputingGainsAndLossesPhase.ID).getImplementation();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridLayout layout = new GridLayout(2, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_parent.setLayout(layout);

		_computingGainsAndLosses.computeVMatrix();
		
		createGainsAndLossesTable();
		
		layout = new GridLayout(1, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_matrixTablesComposite = new Composite(_parent, SWT.NONE);
		_matrixTablesComposite.setLayout(layout);
		_matrixTablesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createGLMMatrix();
		createVTable();
		createNormalizedVTable();
	}

	private void createGainsAndLossesTable() {
		Composite weightsTableComposite = new Composite(_parent, SWT.NONE);
		weightsTableComposite.setLayout(new GridLayout(1, true));
		weightsTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_gainsLossesTableViewer = new TableViewer(weightsTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		_gainsLossesTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_gainsLossesTableViewer.setContentProvider(new GainsLossesTableContentProvider());
		_gainsLossesTableViewer.getTable().setHeaderVisible(true);

		TableViewerColumn alternativeId = new TableViewerColumn(_gainsLossesTableViewer, SWT.NONE);
		alternativeId.getColumn().setText(Messages.ComputingGainsAndLosses_Alternative);
		alternativeId.setLabelProvider(new AlternativeColumnLabelProvider());
		alternativeId.getColumn().pack();

		TableViewerColumn criterionId = new TableViewerColumn(_gainsLossesTableViewer, SWT.NONE);
		criterionId.getColumn().setText(Messages.ComputingGainsAndLosses_Criterion);
		criterionId.setLabelProvider(new CriterionColumnLabelProvider());
		criterionId.getColumn().pack();
		
		TableViewerColumn gains = new TableViewerColumn(_gainsLossesTableViewer, SWT.NONE);
		gains.getColumn().setText(Messages.ComputingGainsAndLosses_Gains);
		gains.setLabelProvider(new GainsColumnLabelProvider());
		gains.getColumn().pack();
		
		TableViewerColumn losses = new TableViewerColumn(_gainsLossesTableViewer, SWT.NONE);
		losses.getColumn().setText(Messages.ComputingGainsAndLosses_Losses);
		losses.setLabelProvider(new LossesColumnLabelProvider());
		losses.getColumn().pack();
		
		setGainsAndLossesData();
	}

	private void setGainsAndLossesData() {
		List<String[]> result = new LinkedList<String[]>();
		for(Pair<Alternative, Criterion> pair: _computingGainsAndLosses.getGLM().keySet()) {
			String[] data = new String[4];
			data[0] = pair.getLeft().getId();
			data[1] = pair.getRight().getId();
			data[2] = Double.toString(_computingGainsAndLosses.getGLM().get(pair)[0]);
			data[3] = Double.toString(_computingGainsAndLosses.getGLM().get(pair)[1]);
			result.add(data);
		}
		
		Collections.sort(result, new ElementComparator());
		
		_gainsLossesTableViewer.setInput(result);
		_gainsLossesTableViewer.getTable().getColumn(0).pack();
		_gainsLossesTableViewer.getTable().getColumn(1).pack();
		_gainsLossesTableViewer.getTable().getColumn(2).pack();
		_gainsLossesTableViewer.getTable().getColumn(3).pack();
	}
	
	private void createGLMMatrix() {
		Composite GLMMatrixComposite = new Composite(_matrixTablesComposite, SWT.NONE);
		GLMMatrixComposite.setLayout(new GridLayout(1, true));
		GLMMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_GLMMatrixTable = new GLMEditableTable(GLMMatrixComposite);
		_GLMMatrixTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setGLMData();
	}
	
	private void setGLMData() {
		String[] alternatives = new String[_computingGainsAndLosses.getAlternativesWithoutRP().size()];
		for (int a = 0; a < _computingGainsAndLosses.getAlternativesWithoutRP().size(); ++a) {
			alternatives[a] = _computingGainsAndLosses.getAlternativesWithoutRP().get(a).getId();
		}

		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for (int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_GLMMatrixTable.setModel(alternatives, criteria, _computingGainsAndLosses.getGLMMatrix(), "GLM"); //$NON-NLS-1$
	}

	private void createVTable() {
		Composite VComposite = new Composite(_matrixTablesComposite, SWT.NONE);
		VComposite.setLayout(new GridLayout(1, true));
		VComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_VTable = new GLMEditableTable(VComposite);
		_VTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setVData();
	}
	
	private void setVData() {
		String[] alternatives = new String[_computingGainsAndLosses.getAlternativesWithoutRP().size()];
		for (int a = 0; a < _computingGainsAndLosses.getAlternativesWithoutRP().size(); ++a) {
			alternatives[a] = _computingGainsAndLosses.getAlternativesWithoutRP().get(a).getId();
		}

		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for (int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_VTable.setModel(alternatives, criteria, _computingGainsAndLosses.getVMatrix(), "V"); //$NON-NLS-1$
	}

	private void createNormalizedVTable() {
		Composite VNormalizedComposite = new Composite(_matrixTablesComposite, SWT.NONE);
		VNormalizedComposite.setLayout(new GridLayout(1, true));
		VNormalizedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_VNormalizedTable = new GLMEditableTable(VNormalizedComposite);
		_VNormalizedTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setNormalizedVData();
	}

	private void setNormalizedVData() {
		String[] alternatives = new String[_computingGainsAndLosses.getAlternativesWithoutRP().size()];
		for (int a = 0; a < _computingGainsAndLosses.getAlternativesWithoutRP().size(); ++a) {
			alternatives[a] = _computingGainsAndLosses.getAlternativesWithoutRP().get(a).getId();
		}

		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for (int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_VNormalizedTable.setModel(alternatives, criteria, _computingGainsAndLosses.normalizeVMatrix(), "VN"); //$NON-NLS-1$
	}

	@Override
	public String getPartName() {
		return Messages.ComputingGainsAndLosses_Computing_gains_and_losses;
	}

	@Override
	public void setFocus() {}

	@Override
	public void notifyStepStateChange() {
		_ratingView.loadNextStep();
	}

	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;	
		notifyStepStateChange();
	}

}

