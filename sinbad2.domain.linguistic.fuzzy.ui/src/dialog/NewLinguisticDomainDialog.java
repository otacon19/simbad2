package dialog;

import jfreechart.LinguisticDomainChart;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

import dialog.label.LabelDialog;
import dialog.provider.FuzzyNameColumnLabelProvider;
import dialog.provider.FuzzySemanticColumnLabelProvider;
import dialog.provider.FuzzyTableContentProvider;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class NewLinguisticDomainDialog extends NewDomainDialog {
	
	private Action _add;
	private Action _remove;
	private Action _modify;
	private Composite _container;
	private FuzzySet _specificDomain;
	private Label _previewLabel;
	private TableViewer _labelsTable;
	private TableViewerColumn _labelsTableNameCol;
	private TableViewerColumn _labelsTableSemanticCol;
	private LinguisticDomainChart _chart;
	private Composite _tableViewerComposite;
	private ActionContributionItem _addButton;
	private ActionContributionItem _modifyButton;
	private ActionContributionItem _removeButton;
	
	public NewLinguisticDomainDialog() {
		super();
	}
	
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		_specificDomain = (FuzzySet) _domain;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		_container = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout();
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.numColumns = 2;
		_container.setLayout(layout);
		_container.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				((Button) _addButton.getWidget()).setSize(83, 25);
				((Button) _modifyButton.getWidget()).setSize(83, 25);
				((Button) _removeButton.getWidget()).setSize(83, 25);
			}
		});

		Label _labels = new Label(_container, SWT.NULL);
		_labels.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		_labels.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		_labels.setText("Labels");

		_tableViewerComposite = new Composite(_container, SWT.NONE);
		GridData gd_tableViewerComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_tableViewerComposite.heightHint = 170;
		_tableViewerComposite.setLayoutData(gd_tableViewerComposite);
		GridLayout gl_tableViewerComposite = new GridLayout(2, false);
		gl_tableViewerComposite.horizontalSpacing = 12;
		_tableViewerComposite.setLayout(gl_tableViewerComposite);

		_labelsTable = new TableViewer(_tableViewerComposite, SWT.BORDER | SWT.MULTI);
		_labelsTable.setContentProvider(new FuzzyTableContentProvider());

		Table table = _labelsTable.getTable();
		GridData gd_table = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 5);
		gd_table.heightHint = 170;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		_labelsTable.setInput(_specificDomain);

		_labelsTableNameCol = new TableViewerColumn(_labelsTable, SWT.NONE);
		_labelsTableNameCol.getColumn().setWidth(140);
		_labelsTableNameCol.getColumn().setText("Name");
		_labelsTableNameCol.setLabelProvider(new FuzzyNameColumnLabelProvider());

		_labelsTableSemanticCol = new TableViewerColumn(_labelsTable, SWT.NONE);
		_labelsTableSemanticCol.getColumn().setWidth(345);
		_labelsTableSemanticCol.getColumn().setText("Semantic");
		_labelsTableSemanticCol.setLabelProvider(new FuzzySemanticColumnLabelProvider());

		hookSelectAction();

		makeActions();
		
		new Label(_tableViewerComposite, SWT.NONE);
		_addButton = new ActionContributionItem(_add);
		_addButton.fill(_tableViewerComposite);
		_addButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		_modifyButton = new ActionContributionItem(_modify);
		_modifyButton.fill(_tableViewerComposite);
		_modifyButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		_removeButton = new ActionContributionItem(_remove);
		_removeButton.fill(_tableViewerComposite);
		new Label(_tableViewerComposite, SWT.NONE);
		new Label(_tableViewerComposite, SWT.NONE);
		new Label(_tableViewerComposite, SWT.NONE);
		new Label(_tableViewerComposite, SWT.NONE);
		_removeButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		new Label(_container, SWT.NONE);
		new Label(_container, SWT.NONE);

		_previewLabel = new Label(_container, SWT.NONE);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		_previewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 2, 1));
		_previewLabel.setText("Preview");

		Composite composite = new Composite(_container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		DomainUIsManager manager = DomainUIsManager.getInstance();
		_chart = (LinguisticDomainChart) manager.newDomainChart(_specificDomain);
		_chart.initialize(_specificDomain, composite, 600, 250, SWT.BORDER);

		refreshViewer();

		return _container;		
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(640, 570);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	private void configureAction(Action action, String text, String toolTipText, ImageDescriptor imageDescriptor) {
		action.setText(text);
		action.setToolTipText(toolTipText);
		action.setImageDescriptor(imageDescriptor);
	}
	
	private void configureActions() {
		
		configureAction(_add, "Add", "Add label", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		configureAction(_modify, "Modify", "Modify label", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
		configureAction(_remove, "Remove", "Remove label", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}
	
	private void makeActions() {
		_add = new Action() {
			@Override
			public void run() {
				LabelDialog labelDialog = new LabelDialog(_container.getShell(), _specificDomain, null);
				if(labelDialog.open() == Window.OK) {
					LabelLinguisticDomain label = labelDialog.getLabel();
					_specificDomain.addLabel(label);
					refreshViewer();
				}
			}
		};
		
		_modify = new Action() {
			@Override
			public void run() {
				ISelection selection = _labelsTable.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LabelLinguisticDomain currentLabel = (LabelLinguisticDomain) obj;
				
				LabelDialog labelDialog = new LabelDialog(_container.getShell(), _specificDomain, currentLabel);
				if(labelDialog.open() == Window.OK) {
					LabelLinguisticDomain label = labelDialog.getLabel();
					_specificDomain.removeLabel(currentLabel);
					_specificDomain.addLabel(label);
					refreshViewer();
				}
			}
		};
		
		_remove = new Action() {
			@Override
			public void run() {
				ISelection selection = _labelsTable.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				_specificDomain.removeLabel((LabelLinguisticDomain) obj);
				refreshViewer();
			}		
		};
		
		configureActions();
	}
	
	private void hookSelectAction() {
		_labelsTable.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				modifyActionsState();
				
			}
		});
	}
	
	private void modifyActionsState() {
		boolean state = !_labelsTable.getSelection().isEmpty();
		_modify.setEnabled(state);
		_remove.setEnabled(state);
	}
	
	private void refreshViewer() {
		modifyActionsState();
		
		_labelsTable.setInput(_specificDomain);
		_labelsTable.refresh();
		_chart.setDomain(_specificDomain);
	}
	
}
