package sinbad2.resolutionphase.rating.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.method.Method;
import sinbad2.method.ui.MethodUI;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.ui.PhaseMethodUI;
import sinbad2.phasemethod.ui.PhaseMethodUIManager;
import sinbad2.resolutionphase.rating.ui.Images;

public class RatingView extends ViewPart {
	
	public static final String ID = "flintstones.resolutionphase.rating.ui.view";
	
	private int _numStep;
	
	private Composite _ratingEditorPanel;
	private Composite _ratingEditorFooter;
	private Composite _ratingEditorContainer;
	private Composite _buttonsBar;
	private Composite _parent;
	private Text _descriptionText;
	private Text _stepsText;
	private Button _backButton;
	private Button _nextButton;
	private Button _resetButton;
	private Label _methodName;
	private Label _stepValue;
	private CLabel _methodSelected;
	
	private CTabFolder _tabFolder;
	private ExpandBar _methodsCategoriesBar; 

	private MethodsUIManager _methodsUIManager;

	public RatingView() {}

	@Override
	public void createPartControl(Composite parent) {	
		_numStep = 0;
		_methodsUIManager = MethodsUIManager.getInstance();
		
		_parent = parent;
		createRatingEditorPanel();
		createMethodSelectionStep();
		createContent();
	}

	private void createRatingEditorPanel() {
		_ratingEditorPanel = new Composite(_parent, SWT.BORDER);
		
		GridLayout ratingEditorPanelLayout = new GridLayout(1, false);
		ratingEditorPanelLayout.marginRight = 0;
		ratingEditorPanelLayout.verticalSpacing = 0;
		ratingEditorPanelLayout.marginWidth = 0;
		ratingEditorPanelLayout.marginHeight = 0;
		_ratingEditorPanel.setLayout(ratingEditorPanelLayout);
		
		_ratingEditorContainer = new Composite(_ratingEditorPanel, SWT.NONE);
		_ratingEditorContainer.setLayout(new GridLayout(1, false));
		_ratingEditorContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		_ratingEditorFooter = new Composite(_ratingEditorPanel, SWT.NONE);
		_ratingEditorFooter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		GridLayout ratingEditorFooterLayout = new GridLayout(4, false);
		ratingEditorFooterLayout.marginRight = 0;
		ratingEditorFooterLayout.verticalSpacing = 0;
		ratingEditorFooterLayout.marginWidth = 0;
		ratingEditorFooterLayout.marginHeight = 0;
		_ratingEditorFooter.setLayout(ratingEditorFooterLayout);
		
		_buttonsBar = new Composite(_ratingEditorFooter, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		_buttonsBar.setLayoutData(gridData);
		_buttonsBar.setLayout(new GridLayout(3, true));
		
		_backButton = new Button(_buttonsBar, SWT.PUSH);
		GridData gd_backbutton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		_backButton.setLayoutData(gd_backbutton);
		_backButton.setText("< Back");
		_backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPreviousStep();
			}
		});
		
		_nextButton = new Button(_buttonsBar, SWT.PUSH);
		_nextButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		_nextButton.setText("Next >");
		_nextButton.addSelectionListener(new SelectionAdapter() { 
			@Override
			public void widgetSelected(SelectionEvent e) {
				getNextStep();
			}	
		});
		
		_resetButton = new Button(_buttonsBar, SWT.PUSH);
		_resetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		_resetButton.setText("Reset");
		_resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetRating(true);
			}
		});
		
		Label separator = new Label(_ratingEditorFooter, SWT.SEPARATOR | SWT.HORIZONTAL);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gridData.verticalIndent = 5;
		separator.setLayoutData(gridData);
		
		Label method = new Label(_ratingEditorFooter, SWT.NONE);
		method.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
		method.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		method.setText("Method: ");
		
		_methodName = new Label(_ratingEditorFooter, SWT.NONE);
		_methodName.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
		_methodName.setText("Unselected");
		
		Label step = new Label(_ratingEditorFooter, SWT.NONE);
		step.setLayoutData(new GridData(SWT.RIGHT, SWT.LEFT, true, false, 1, 1));
		step.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		step.setText("Step: ");
		
		_stepValue = new Label(_ratingEditorFooter, SWT.NONE);
		_stepValue.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
		_stepValue.setText("(0/0)");
	}
	
	private void getPreviousStep() {
		if(_numStep != 0) {
			_numStep--;
			_tabFolder.setSelection(_numStep);
		}
	}
	
	private void getNextStep() {
		_numStep++;
		_tabFolder.setSelection(_numStep);
		
	}
	
	private void resetRating(boolean reset) {
		// TODO Auto-generated method stub
	}

	private void createMethodSelectionStep() {
		_tabFolder = new CTabFolder(_ratingEditorContainer, SWT.BORDER | SWT.VERTICAL);
		_tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		Display display = Display.getCurrent();
		_tabFolder.setSelectionBackground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		_tabFolder.layout();
		
		CTabItem item = new CTabItem(_tabFolder, SWT.CLOSE, 0);
	    item.setText("Method selection");
	}

	private void createContent() {
		Composite composite = new Composite(_tabFolder, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		composite.setLayoutData(gridData);

		GridLayout layout = new GridLayout(2, true);
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		
		Composite compositeLeft = new Composite(composite, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		compositeLeft.setLayoutData(gridData);
		
		layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		compositeLeft.setLayout(layout);
		
		Label methodsLabel = new Label(compositeLeft, SWT.NONE);
		methodsLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		methodsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		methodsLabel.setText("Method selection");
		
		_methodsCategoriesBar = new ExpandBar(compositeLeft, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_methodsCategoriesBar.setLayoutData(gridData);
		
		String[] ids = _methodsUIManager.getIdsRegisters();
		
		Map<String, List<Method>> categoriesMethods = new HashMap<String, List<Method>>();
		List<Method> methods = new LinkedList<Method>();
		
		for(String id: ids) {
			methods.clear();
			MethodUI methodUI = _methodsUIManager.getUI(id);
			Method method = methodUI.getMethod();
			String category = method.getCategory();
			
			if(categoriesMethods.get(category) != null) {
				methods = categoriesMethods.get(category);
			}
			methods.add(method);
			categoriesMethods.put(category, methods);
		}

		int cont = 0;
		for(String key: categoriesMethods.keySet()) {
			createCategoryBar(key, cont,  categoriesMethods.get(key));
			cont++;
		}
		
		Button showAlgorithmButton = new Button(compositeLeft, SWT.NONE);
		showAlgorithmButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		showAlgorithmButton.setText("Show algorithm");
		
		createInfoPanels(composite);
		
		_tabFolder.getItem(0).setControl(composite);
	}
	
	private void createCategoryBar(String categoryName, int pos, List<Method> methods) {
		Composite composite = new Composite(_methodsCategoriesBar, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		composite.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		for (int i = 0; i < methods.size(); i++) {
			createMethod(composite, methods.get(i));
		}

		ExpandItem categoryItem = new ExpandItem(_methodsCategoriesBar, SWT.NONE, pos);
		categoryItem.setText(categoryName);
		categoryItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		categoryItem.setControl(composite);
		categoryItem.setImage(Images.category);
	}
	
	private void createMethod(Composite parent, final Method method) {
		final CLabel label = new CLabel(parent, SWT.LEFT);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.horizontalIndent = 15;
		label.setLayoutData(gridData);

		label.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL)); //$NON-NLS-1$
		label.setText(method.getName());

		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		label.setImage(Images.signed_yes);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				CLabel labelSelected = (CLabel) e.getSource();
				if(_methodSelected != null && _methodSelected != labelSelected) {
					_methodSelected.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
				}
				_methodSelected = labelSelected;
				_methodSelected.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
				_methodName.setText(_methodSelected.getText());
				_methodName.getParent().layout();
				
				_descriptionText.setText(method.getDescription());
				
				_methodsUIManager.activate(method.getId() + ".ui");
				
				MethodUI methodUI = _methodsUIManager.getActivateMethodUI();
				calculateNumSteps(methodUI);
				loadFirstStep(methodUI);
				
				_stepsText.setText(methodUI.getPhasesFormat());
			}
		});
	
		label.pack();
	}
	
	private void calculateNumSteps(MethodUI methodUI) {
		List<PhaseMethodUI> phasesMethodUI = methodUI.getPhasesUI();
		PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
		
		int numSteps = 0;
		for(PhaseMethodUI phase: phasesMethodUI) {
			numSteps += phasesMethodUIManager.getSteps(phase.getId()).size();
		}
		_stepValue.setText("0/" + numSteps);
	}
	
	private void loadFirstStep(MethodUI methodUI) {
		PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
		List<PhaseMethodUI> phasesMethodUI = methodUI.getPhasesUI();
		
		ViewPart step = phasesMethodUIManager.getStep(phasesMethodUI.get(0).getId(), 0);
		CTabItem item = new CTabItem(_tabFolder, SWT.CLOSE, 1);
		item.setText(step.getPartName());
		
		Composite parent = new Composite(_tabFolder, SWT.NONE);
		
		step.createPartControl(parent);
		item.setControl(parent);
	}
	
	private void createInfoPanels(Composite composite) {
		Composite compositePanels = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.verticalIndent = 2;
		compositePanels.setLayoutData(gridData);

		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 15;
		compositePanels.setLayout(layout);

		Label descriptionLabel = new Label(compositePanels, SWT.NONE);
		descriptionLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		descriptionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		descriptionLabel.setText("Method description");

		_descriptionText = new Text(compositePanels, SWT.BORDER | SWT.READ_ONLY| SWT.MULTI | SWT.WRAP);
		gridData  = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.verticalIndent = 0;
		gridData.heightHint = 120;
		_descriptionText.setLayoutData(gridData);

		Label stepsLabel = new Label(compositePanels, SWT.NONE);
		stepsLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		stepsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		stepsLabel.setText("Method phases");

		_stepsText = new Text(compositePanels, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_stepsText.setLayoutData(gridData);
	}
	
	@Override
	public void setFocus() {
		_tabFolder.setSelection(0);
	}
	
}
