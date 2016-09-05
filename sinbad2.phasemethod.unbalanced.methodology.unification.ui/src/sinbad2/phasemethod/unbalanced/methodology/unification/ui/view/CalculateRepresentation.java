package sinbad2.phasemethod.unbalanced.methodology.unification.ui.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.PlainFuzzySetChart;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.linguistic.unbalanced.ui.jfreechart.LHChart;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.unbalanced.methodology.unification.UnificationPhase;
import sinbad2.phasemethod.unbalanced.methodology.unification.ui.nls.Messages;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class CalculateRepresentation extends ViewPart implements IStepStateListener {
	
	private Composite _parent;
	private Composite _representationPanel;
	private Composite _chartPanel;
	
	private TableViewer _representationViewer;
	
	private TableViewerColumn _colorColumn;
	private TableViewerColumn _labelColumn;
	private TableViewerColumn _lhColumn;
	private TableViewerColumn _bridColumn;
	
	private LHChart _chart;
	
	private ControlAdapter _controlListener;
	
	private int _selectionPos;
	
	private boolean _completed;
	
	private Unbalanced _domain;
	
	private int[] _lh;
	
	private UnificationPhase _unification;

	private RatingView _ratingView;
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		
		GridLayout layout = new GridLayout(14, true);
		layout.marginLeft = 20;
		layout.marginRight = 15;
		layout.marginBottom = 15;
		layout.marginTop = 20;
		_parent.setLayout(layout);
		
		_controlListener = null;
		
		_completed = false;
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_unification = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();

		createRepresentationInfo();
		createChart();
		
		activate();
	}
	
	private void createRepresentationInfo() {
		_representationPanel = new Composite(_parent, SWT.NONE);
		_representationPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		_representationPanel.setLayout(layout);

		_representationViewer = new TableViewer(_representationPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.verticalIndent = 0;
		_representationViewer.getTable().setLayoutData(gridData);
		_representationViewer.getTable().setHeaderVisible(true);
		_representationViewer.setContentProvider(new IStructuredContentProvider() {

					@Override
					public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

					@Override
					public void dispose() {}

					@Override
					public Object[] getElements(Object inputElement) {
						Unbalanced domain = (Unbalanced) inputElement;

						Object[] result = new Object[domain.getLabelSet().getCardinality()];
						Object[] element;
						int r, g, b;
						for(int i = 0; i < result.length; i++) {
							element = new Object[4];
							java.awt.Color color = PlainFuzzySetChart.getColor(i);
							r = color.getRed();
							g = color.getGreen();
							b = color.getBlue();
							element[0] = new Color(Display.getCurrent(), r, g, b);
							element[1] = domain.getLabelSet().getLabel(i).getName();
							StringBuilder LHS = null;
							boolean brid = false;
							int other = -1;
							boolean find;
							int t;
							
							for(Integer pos : domain.getLabel(i).keySet()) {
								find = false;
								t = 0;
								do {
									if(pos == _lh[t]) {
										find = true;
									} else {
										t++;
									}
								} while (!find);
								t++;
		
								if(LHS == null) {
									brid = false;
									LHS = new StringBuilder("s" + domain.getLabel(i).get(pos) + " l(" + t + "," + pos + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									other = pos;
								} else {
									brid = true;
									NumericRealDomain center = _domain.getLabelSet().getLabel(i).getSemantic().getCenter();
									NumericRealDomain coverage = _domain.getLabelSet().getLabel(i).getSemantic().getCoverage();

									double left = center.getMin() - coverage.getMin();
									double right = coverage.getMax() - center.getMax();

									if(((left > right) && (other < pos)) || ((left < right) && (other > pos))) {
										LHS.append(" " + "or" + " s" + domain.getLabel(i).get(pos) + " l(" + t + "," + pos + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
									} else {
										String aux = LHS.toString();
										LHS = new StringBuilder("s" + domain.getLabel(i).get(pos) + " l(" + t + "," + pos + ") " + "or" + " " + aux); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
									}
								}
							}
							element[2] = LHS.toString();
							element[3] = Boolean.toString(brid);
							result[i] = element;
						}
						return result;
					}
				});

		_colorColumn = new TableViewerColumn(_representationViewer, SWT.NONE);
		_colorColumn.getColumn().setWidth(25);
		_colorColumn.getColumn().setResizable(false);
		_colorColumn.getColumn().setMoveable(false);
		_colorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
			}

			@Override
			public Color getBackground(Object element) {
				return (Color) ((Object[]) element)[0];
			}
		});

		_labelColumn = new TableViewerColumn(_representationViewer, SWT.NONE);
		_labelColumn.getColumn().setWidth(100);
		_labelColumn.getColumn().setText("S"); //$NON-NLS-1$
		_labelColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) ((Object[]) element)[1];
			}
		});

		_lhColumn = new TableViewerColumn(_representationViewer, SWT.NONE);
		_lhColumn.getColumn().setWidth(100);
		_lhColumn.getColumn().setText("LH_S"); //$NON-NLS-1$
		_lhColumn.getColumn().setAlignment(SWT.CENTER);
		_lhColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) ((Object[]) element)[2];
			}
		});

		_bridColumn = new TableViewerColumn(_representationViewer, SWT.NONE);
		_bridColumn.getColumn().setWidth(100);
		_bridColumn.getColumn().setText("Brid"); //$NON-NLS-1$
		_bridColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String) ((Object[]) element)[3]).toUpperCase();
			}
		});

		_representationViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = _representationViewer.getSelection();
				String selectLabel = (String) ((Object[]) ((IStructuredSelection) selection).getFirstElement())[1];
				_selectionPos = _domain.getLabelSet().getPos(selectLabel);
				_chart.select(_selectionPos);
			}
		});
	}
	
	private void createChart() {
		_chartPanel = new Composite(_parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 8, 1);
		_chartPanel.setLayoutData(gridData);
	}
	
	private void calculateRepresentation() {
		
		if(_domain == null) {
			_domain = (Unbalanced) _unification.getUnifiedDomain();
		}
		if(_lh == null) {
			generateLH();
		}

		_representationViewer.setInput(_domain);

		_labelColumn.getColumn().pack();
		_labelColumn.getColumn().setWidth(_labelColumn.getColumn().getWidth() + 10);
		_lhColumn.getColumn().pack();
		_lhColumn.getColumn().setWidth(_lhColumn.getColumn().getWidth() + 10);
		_bridColumn.getColumn().pack();
		_bridColumn.getColumn().setWidth(_bridColumn.getColumn().getWidth() + 10);

		String representation = _domain.getInfo();

		if (representation != null) {
			_completed = true;
		} else {
			_domain = null;
			_selectionPos = -1;
		}
	}

	private void generateLH() {
		_lh = _domain.getLh();
		
		if(_lh != null) {
			_chart = new LHChart(_lh, _chartPanel, _chartPanel.getSize().x, _chartPanel.getSize().y - 1, _domain);
			if(_controlListener == null) {
				_controlListener = new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						_chart.dispose();
						generateLH();
					}
				};
				_chartPanel.addControlListener(_controlListener);
			}
			if(_selectionPos != -1) {
				_chart.select(_selectionPos);
			}
		} else {
			if(_chart != null) {
				_chart.dispose();
				_chart = null;
				_selectionPos = -1;
			}
		}
	}
	
	public void activate() {
		if(_domain == null) {
			calculateRepresentation();
		}
		_representationPanel.layout();
		_chartPanel.layout();
	}
	
	@Override
	public String getPartName() {
		return Messages.CalculateRepresentation_Calculate_representation;
	}
	
	@Override
	public void setFocus() {
		_chartPanel.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		_chart = null;
		_lh = null;
		_domain = null;
	}
	
	@Override
	public void notifyStepStateChange() {
		
		if(_completed) {
			_ratingView.loadNextStep();
			_completed = false;
		}
	}

	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
	}

}
