package sinbad2.phasemethod.multigranular.unification.ui.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;

public class SelectBLTS extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.multigranular.unification.ui.view.selectblts";
	
	private Composite _parent;
	private Composite _validDomainsPanel;
	private Composite _selectedDomainPanel;
	private Button _createNewButton;
	
	private TableViewer _validDomainsViewer;
	
	private LinguisticDomainChart _chart;
	
	private ControlAdapter _controlListener;
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(14, true);
		layout.horizontalSpacing = 15;
		parent.setLayout(layout);
		
		createValidDomainsPanel();
		createSelectedDomainPanel();
	}

	private void createValidDomainsPanel() {
		_validDomainsPanel = new Composite(_parent, SWT.NONE);
		_validDomainsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 20;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		_validDomainsPanel.setLayout(layout);

		_validDomainsViewer = new TableViewer(_validDomainsPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_validDomainsViewer.getTable().setLayoutData(gridData);
		_validDomainsViewer.getTable().setHeaderVisible(true);
		_validDomainsViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[][]) inputElement;
			}
		});

		TableViewerColumn col = new TableViewerColumn(_validDomainsViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) ((Object[]) element)[0];
			}
		});

		TableViewerColumn descriptionColumn = new TableViewerColumn(_validDomainsViewer, SWT.NONE);
		descriptionColumn.getColumn().setWidth(100);
		descriptionColumn.getColumn().setText("Description");
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((FuzzySet) ((Object[]) element)[1]).formatDescriptionDomain();
			}
		});

		_createNewButton = new Button(_validDomainsPanel, SWT.PUSH);
		_createNewButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		_createNewButton.setText("Create new");	
	}

	private void createSelectedDomainPanel() {
		_selectedDomainPanel = new Composite(_parent, SWT.NONE);
		_selectedDomainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
		
		refreshChart();	
	}
	
	
	private void refreshChart() {
		removeChart();
		_chart = new LinguisticDomainChart();
		Point size = _selectedDomainPanel.getSize();
		_chart.initialize(null, _selectedDomainPanel, size.x, size.y, SWT.BORDER);
		
		if (_controlListener == null) {
			_controlListener = new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					refreshChart();
				}
			};
			_selectedDomainPanel.addControlListener(_controlListener);
		}
	}
	
	private void removeChart() {
		if (_chart != null) {
			_chart.getChartComposite().dispose();
		}
	}

	@Override
	public String getPartName() {
		return "Select BLTS";
	}
	
	@Override
	public void setFocus() {
		_validDomainsViewer.getControl().setFocus();
	}
	
}
