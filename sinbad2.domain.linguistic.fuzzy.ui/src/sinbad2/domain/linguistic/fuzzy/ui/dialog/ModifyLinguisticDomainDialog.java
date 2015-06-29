package sinbad2.domain.linguistic.fuzzy.ui.dialog;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.ui.dialog.provider.FuzzyNameColumnLabelProvider;
import sinbad2.domain.linguistic.fuzzy.ui.dialog.provider.FuzzySemanticColumnLabelProvider;
import sinbad2.domain.linguistic.fuzzy.ui.dialog.provider.FuzzyTableContentProvider;
import sinbad2.domain.linguistic.fuzzy.ui.dialog.subdialog.CreateManualDomainDialog;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.modifyDialog.ModifyDomainDialog;

public class ModifyLinguisticDomainDialog extends ModifyDomainDialog {
	
	private Action _add;
	private Action _remove;
	private Action _modify;
	private Composite _container;
	private FuzzySet _specificDomain;
	private Label _previewLabel;
	private TableViewer _tableViewer;
	private TableViewerColumn _tableViewerNameCol;
	private TableViewerColumn _tableViewerSemanticCol;
	private LinguisticDomainChart _chart;
	private ControlDecoration _domainNameTextControlDecoration;
	private Composite _tableViewerComposite;
	private Button _okButton;
	private ActionContributionItem _addButton;
	private ActionContributionItem _modifyButton;
	private ActionContributionItem _removeButton;
	
	
	public ModifyLinguisticDomainDialog() {
		super();
	}
	
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		_specificDomain = (FuzzySet) _newDomain;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		_container = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = new GridLayout();
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.numColumns = 2;
		
		Label idLabel = new Label(_container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText("Domain id");
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		
		Text textID = new Text(_container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		textID.setLayoutData(gridData);
		textID.setText(_id);
		textID.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				_id = ((Text) e.getSource()).getText().trim();
				_specificDomain.setId(_id);
				validate();
			}
		});
		
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
		gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gridData.verticalIndent = 15;
		_labels.setLayoutData(gridData);
		_labels.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
		_labels.setText("Labels");

		_tableViewerComposite = new Composite(_container, SWT.NONE);
		GridData gd_tableViewerComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_tableViewerComposite.heightHint = 170;
		_tableViewerComposite.setLayoutData(gd_tableViewerComposite);
		GridLayout gl_tableViewerComposite = new GridLayout(2, false);
		gl_tableViewerComposite.horizontalSpacing = 12;
		_tableViewerComposite.setLayout(gl_tableViewerComposite);

		_tableViewer = new TableViewer(_tableViewerComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		_tableViewer.setContentProvider(new FuzzyTableContentProvider());
		
		_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				_chart.setSelection(_tableViewer.getTable().getSelectionIndex());
				
			}
		});
		
		Table table = _tableViewer.getTable();
		GridData gd_table = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
		gd_table.heightHint = 140;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		_tableViewer.setInput(_specificDomain);

		_tableViewerNameCol = new TableViewerColumn(_tableViewer, SWT.NONE);
		_tableViewerNameCol.getColumn().setWidth(140);
		_tableViewerNameCol.getColumn().setText("Name");
		_tableViewerNameCol.setLabelProvider(new FuzzyNameColumnLabelProvider());

		_tableViewerSemanticCol = new TableViewerColumn(_tableViewer, SWT.NONE);
		_tableViewerSemanticCol.getColumn().setWidth(345);
		_tableViewerSemanticCol.getColumn().setText("Semantic");
		_tableViewerSemanticCol.setLabelProvider(new FuzzySemanticColumnLabelProvider());

		hookSelectAction();

		makeActions();
		
		_domainNameTextControlDecoration = createNotificationDecorator(textID);

		_addButton = new ActionContributionItem(_add);
		_addButton.fill(_tableViewerComposite);
		_addButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		_modifyButton = new ActionContributionItem(_modify);
		_modifyButton.fill(_tableViewerComposite);
		_modifyButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		_removeButton = new ActionContributionItem(_remove);
		_removeButton.fill(_tableViewerComposite);
		_removeButton.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		_previewLabel = new Label(_container, SWT.NONE);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD));
		_previewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 2, 1));
		_previewLabel.setText("Preview");

		Composite composite = new Composite(_container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		DomainUIsManager manager = DomainUIsManager.getInstance();
		_chart = (LinguisticDomainChart) manager.newDomainChart(_specificDomain);
		_chart.initialize(_specificDomain, composite, 600, 200, SWT.BORDER);
		
		refreshViewer();

		return _container;		
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(640, 600);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Manually create domain");
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
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
				CreateManualDomainDialog manualDomainDialog = new CreateManualDomainDialog(_container.getShell(), _specificDomain, null);
				if(manualDomainDialog.open() == Window.OK) {
					LabelLinguisticDomain label = manualDomainDialog.getLabel();
					_specificDomain.addLabel(label);
					refreshViewer();
				}
			}
		};
		
		_modify = new Action() {
			@Override
			public void run() {
				ISelection selection = _tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LabelLinguisticDomain currentLabel = (LabelLinguisticDomain) obj;
				
				CreateManualDomainDialog manualDomainDialog = new CreateManualDomainDialog(_container.getShell(), _specificDomain, currentLabel);
				if(manualDomainDialog.open() == Window.OK) {
					LabelLinguisticDomain label = manualDomainDialog.getLabel();
					_specificDomain.removeLabel(currentLabel);
					_specificDomain.addLabel(label);
					refreshViewer();
				}
			}
		};
		
		_remove = new Action() {
			@Override
			public void run() {
				ISelection selection = _tableViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				_specificDomain.removeLabel((LabelLinguisticDomain) obj);
				refreshViewer();
			}		
		};
		
		configureActions();
	}
	
	private void hookSelectAction() {
		_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				modifyActionsState();
				
			}
		});
	}
	
	private void modifyActionsState() {
		boolean state = !_tableViewer.getSelection().isEmpty();
		_modify.setEnabled(state);
		_remove.setEnabled(state);
	}
	
	private void refreshViewer() {
		modifyActionsState();
		
		_tableViewer.setInput(_specificDomain);
		_tableViewer.refresh();
		_chart.setDomain(_specificDomain);
	}
	
	private void validate() {		
		boolean validId;
		String msgId = "";
		
		if(!_id.isEmpty()) {
			if(_ids.contains(_id)) {
				msgId = "Duplicated id";
			}
		} else {
				msgId = "Empty value";
		}
		
		validId = validate(_domainNameTextControlDecoration, msgId);
							
		_okButton.setEnabled(validId);			
	}
	
}
