package sinbad2.resolutionphase.rating.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.method.Method;
import sinbad2.method.MethodsManager;
import sinbad2.method.ui.MethodUI;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.ui.PhaseMethodUI;
import sinbad2.phasemethod.ui.PhaseMethodUIManager;
import sinbad2.resolutionphase.rating.ui.Images;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.dialog.AlgorithmDialog;

public class RatingView extends ViewPart {
	public RatingView() {
	}

	public static final String ID = "flintstones.resolutionphase.rating.ui.view";
	
	private Composite _ratingEditorPanel;
	private Composite _ratingEditorFooter;
	private Composite _ratingEditorContainer;
	private Composite _buttonsBar;
	private Composite _parent;
	
	private Text _descriptionText;
	private Text _stepsText;
	
	private Label _methodNameFooterText;
	private Label _stepValue;
	private CLabel _warningLabel;
	private CLabel _methodLabelSelected;
	
	private ExpandBar _methodsCategoriesBar; 

	private MethodUI _methodUISelected;

	private Button _backButton;
	private Button _nextButton;
	private Button _resetButton;
	
	private CTabFolder _tabFolder;
	
	private int _numStep;
	private int _numPhase;
	
	private String _recommendedMethod;
	
	private Map<CLabel, Method> _methods;
	
	private List<IStepStateListener> _listeners;

	@Override
	public void createPartControl(Composite parent) {	
		_numPhase = 0;
		_numStep = 0;
		
		_methods = new HashMap<CLabel, Method>();
		
		_methodUISelected = null;
		
		_listeners = new LinkedList<IStepStateListener>();
		
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
		_backButton.setEnabled(false);
		_backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPreviousStep();
			}
		});
		
		_nextButton = new Button(_buttonsBar, SWT.PUSH);
		_nextButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		_nextButton.setText("Next >");
		_nextButton.setEnabled(false);
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
		
		_methodNameFooterText = new Label(_ratingEditorFooter, SWT.NONE);
		_methodNameFooterText.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1));
		_methodNameFooterText.setText("Unselected");
		
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
			activateStep(_numStep);
			_nextButton.setEnabled(true);
		}
		
		if(_numStep == 0) {
			_backButton.setEnabled(false);
		}
		modifyStep(0);
	}
	
	private void modifyStep(int state) {
		String currentStep = _stepValue.getText().substring(_stepValue.getText().indexOf("(") + 1, _stepValue.getText().indexOf("/"));
		String totalSteps = _stepValue.getText().substring(_stepValue.getText().indexOf("/") + 1, _stepValue.getText().indexOf(")"));
		int currentStepNum = Integer.parseInt(currentStep);
		if(state == 0) {
			_stepValue.setText("(" + Integer.toString(currentStepNum - 1) + "/" + totalSteps +")");
		} else {
			_stepValue.setText("(" + Integer.toString(currentStepNum + 1) + "/" + totalSteps +")");
		}
	}
	
	private void getNextStep() {
		if((_numStep + 1) < _tabFolder.getItemCount()) {
			_numStep++;
			activateStep(_numStep);
			_backButton.setEnabled(true);
		}
		
		if(_numStep + 1 == _tabFolder.getItemCount()) {
			_nextButton.setEnabled(false);
		}
		modifyStep(1);
	}

	public void resetRating(boolean confirm) {
		boolean reset = true;
		if(confirm) {
			reset = MessageDialog.openConfirm(this.getSite().getShell(), "Cancel confirm", "All information will be lost");
		}
		if(reset) {
			clearMethodSteps();
			_tabFolder.setSelection(0);
			checkRecommendedMethod();
		}
	}

	private void createMethodSelectionStep() {
		if(_tabFolder == null) {
			_tabFolder = new CTabFolder(_ratingEditorContainer, SWT.BORDER | SWT.VERTICAL);
			_tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	
			Display display = Display.getCurrent();
			_tabFolder.setSelectionBackground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			_tabFolder.layout();
			  
		    _tabFolder.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(SelectionEvent e) {
		    		if(_tabFolder.getSelectionIndex() < _numStep) {
		    			getPreviousStep();
		    		} else if(_tabFolder.getSelectionIndex() > _numStep) {
		    			getNextStep();
		    		}
		    	}
			});
		}
		
		CTabItem item = new CTabItem(_tabFolder, SWT.CLOSE, 0);
	    item.setText("Method selection");
	    item.setShowClose(false);
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
		
		createInfoPanels(composite);
		createWarningLabel(composite);
		
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();
		String[] ids = methodsUIManager.getIdsRegisters();
		
		Map<String, List<Method>> categoriesMethods = new HashMap<String, List<Method>>();
		List<Method> methods;
		for(String id: ids) {
			methods = new LinkedList<Method>();
			MethodUI methodUI = methodsUIManager.getUI(id);
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
		
		checkRecommendedMethod();
		
		Button showAlgorithmButton = new Button(compositeLeft, SWT.NONE);
		showAlgorithmButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		showAlgorithmButton.setText("Show algorithm");
		showAlgorithmButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AlgorithmDialog(Display.getCurrent().getActiveShell()).open();
			}
		});
		
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

		MethodsManager methodsManager = MethodsManager.getInstance();
		_recommendedMethod = methodsManager.getRecommendedMethod();
		for (int i = 0; i < methods.size(); i++) {
			createMethod(composite, methods.get(i));
		}

		ExpandItem categoryItem = new ExpandItem(_methodsCategoriesBar, SWT.NONE, pos);
		categoryItem.setText(categoryName);
		categoryItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		categoryItem.setControl(composite);
		categoryItem.setImage(Images.category);
	}
	
	private void createMethod(Composite parent, final Method currentMethod) {
		final CLabel label = new CLabel(parent, SWT.LEFT);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.horizontalIndent = 15;
		label.setLayoutData(gridData);

		label.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL)); //$NON-NLS-1$
		label.setText(currentMethod.getName());
		if(currentMethod.getName().equals(_recommendedMethod)) {
			label.setText(label.getText() + " (SUITABLE)");
		}
		
		final String test = currentMethod.getImplementation().isAvailable();
		if(test.length() == 0 || test.equals("Not set all assignments")) {
			label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
			label.setImage(Images.signed_yes);
			_nextButton.setEnabled(false);
			_resetButton.setEnabled(false);
		} else {
			label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			label.setImage(Images.signed_no);
			_resetButton.setEnabled(true);
		}
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {			
				CLabel labelSelected = (CLabel) e.getSource();
				if(_methodLabelSelected != null && _methodLabelSelected != labelSelected) {
					_methodLabelSelected.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
				}
				_methodLabelSelected = labelSelected;
				_methodLabelSelected.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
				_methodNameFooterText.setText(_methodLabelSelected.getText());
				_methodNameFooterText.getParent().layout();
				
				selectMethod(currentMethod, _methodLabelSelected);
			}
		});
		
		label.pack();
		
		_methods.put(label, currentMethod);
	}
	
	private void calculateNumSteps() {
		List<PhaseMethodUI> phasesMethodUI = _methodUISelected.getPhasesUI();
		PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
		
		int numSteps = 0;
		for(PhaseMethodUI phase: phasesMethodUI) {
			numSteps += phasesMethodUIManager.getSteps(phase.getId()).size();
		}
		_stepValue.setText("(0/" + numSteps + ")");
	}
	
	public void loadNextStep() {
		PhaseMethodUIManager phasesMethodUIManager = PhaseMethodUIManager.getInstance();
		PhaseMethodUI currentPhaseMethod = phasesMethodUIManager.getActiveResolutionPhasesUI();
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();
		List<PhaseMethodUI> phasesMethodUI = methodsUIManager.getActivateMethodUI().getPhasesUI();
		
		if(_listeners.size() == 0) {
			registerStepChangeListeners(phasesMethodUIManager);
		}
		
		ViewPart step;
		if(currentPhaseMethod == null) {
			currentPhaseMethod = phasesMethodUI.get(_numPhase);
			phasesMethodUIManager.activate(currentPhaseMethod.getId());
			step = phasesMethodUIManager.getStep(currentPhaseMethod.getId(), 0);
		} else if(phasesMethodUIManager.getNextStep() == null) {
			_numPhase++;
			currentPhaseMethod = phasesMethodUI.get(_numPhase);
			phasesMethodUIManager.activate(currentPhaseMethod.getId());	
			step = phasesMethodUIManager.getStep(currentPhaseMethod.getId(), 0);
		} else {
			step = phasesMethodUIManager.getNextStep();	
		}
		
		phasesMethodUIManager.activateStep(step);
		boolean loaded = false;
		for(CTabItem tabItem: _tabFolder.getItems()) {
			if(tabItem.getText().equals(step.getPartName())) {
				loaded = true;
				break;
			}
		}

		if(!loaded) {
			CTabItem item = new CTabItem(_tabFolder, SWT.CLOSE, _tabFolder.getItemCount());
			item.setText(step.getPartName());
			item.setShowClose(false);

			Composite parent = new Composite(_tabFolder, SWT.NONE);
			step.createPartControl(parent);
			if(step instanceof IStepStateListener) {
				notifyRatingView((IStepStateListener) step);
			}
			item.setControl(parent);
			item.setData(step);
			
			_nextButton.setEnabled(true);
		}
	}
	
	private void clearMethodSteps() {
		while(_tabFolder.getItemCount() > 1) {
			_tabFolder.getItem(1).dispose();
		}
		_listeners.clear();

		_numPhase = 0;
		_numStep = 0;
		
		_methodNameFooterText.setText("Unselected");
		_stepValue.setText("(0/0)");
		
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();
		methodsUIManager.deactiveCurrentActive();
		
		_methodUISelected = null;
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
	
	private void createWarningLabel(Composite composite) {
		Composite compositeWarning = new Composite(composite, SWT.NONE);
		compositeWarning.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		compositeWarning.setLayoutData(gridData);
		
		_warningLabel = new CLabel(compositeWarning, SWT.NONE);
		_warningLabel.setFont(SWTResourceManager.getFont("Occidental", 10, SWT.BOLD)); //$NON-NLS-1$
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, true, 1, 1);
		_warningLabel.setLayoutData(gridData);
	}

	public void checkRecommendedMethod() {
		String methodName;
		CLabel suitableLabel = null;
		for(ExpandItem item: _methodsCategoriesBar.getItems()) {
			Composite control = (Composite) item.getControl();
			Control[] childrens = control.getChildren();
			for(Control methodLabel : childrens) {
				methodName = ((CLabel) methodLabel).getText();
				if(methodName.contains(" (SUITABLE)")) {
					suitableLabel = (CLabel) methodLabel;
					if(!item.getExpanded()) {
						item.setExpanded(true);
						((CLabel) methodLabel).setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
						((CLabel) methodLabel).pack();
					}
				}
			}
		}

		Method methodToSelect = _methods.get(suitableLabel);
		if(methodToSelect != null) {
			selectMethod(methodToSelect, suitableLabel);
		}
	}
	
	private void selectMethod(Method methodToSelect, CLabel suitableLabel) {
		_descriptionText.setText(methodToSelect.getDescription());
		
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();
		if(methodsUIManager.getActivateMethodUI() != null) {
			clearMethodSteps();
		}

		_methodNameFooterText.setText(suitableLabel.getText().replace(" (SUITABLE)", ""));
		_methodLabelSelected = suitableLabel;
		
		methodsUIManager.activate(methodToSelect.getId() + ".ui");
		_methodUISelected = methodsUIManager.getActivateMethodUI();
		_stepsText.setText(_methodUISelected.getPhasesFormat());
		
		String test = methodToSelect.getImplementation().isAvailable();
		if(suitableLabel != null) {
			if(!suitableLabel.getForeground().equals(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED))) {
				calculateNumSteps();
				loadNextStep();
				_warningLabel.setImage(null);
				_warningLabel.setText("");
			} else {
				_warningLabel.setImage(Images.error);
				_warningLabel.setText(test);
				_warningLabel.setForeground(new Color(_warningLabel.getParent().getDisplay(), new RGB(205, 65, 65)));
			}
			
			
			if(test.equals("Not set all assignments")) {
				_warningLabel.setImage(Images.warning);
				_warningLabel.setText(test);
				_warningLabel.setForeground(new Color(_warningLabel.getParent().getDisplay(), new RGB(255, 212, 0)));
			}
		}
		
		_warningLabel.pack();
		_warningLabel.getParent().layout();;
	}
		
	private void activateStep(int numStep) {
		_tabFolder.setSelection(numStep);
		notifyNewStep();
	}
	
	public void disabledNextStep() {
		_nextButton.setEnabled(false);
	}
	
	@Override
	public void setFocus() {
		_tabFolder.setSelection(0);
	}
	
	public void registerStepChangeListeners(PhaseMethodUIManager phasesMethodUIManager) {
		Map<String, List<ViewPart>> phasesSteps = phasesMethodUIManager.getPhasesSteps(); 
		for(String id: phasesSteps.keySet()) {
			List<ViewPart> views = phasesSteps.get(id);
			for(ViewPart view: views) {
				if(view instanceof IStepStateListener) {
					_listeners.add((IStepStateListener) view);
				}
			}
		}
	}
	
	public void unregisterStepChangeListener(IStepStateListener listener) {
		_listeners.remove(listener);
	}

	private void notifyNewStep() {
		CTabItem item = _tabFolder.getSelection();
		for(IStepStateListener listener: _listeners) {
			if(listener.equals(item.getData())) {
				listener.notifyStepStateChange();
			}
		}
	}
	
	private void notifyRatingView(IStepStateListener listener) {
		listener.notifyRatingView(this);
	}
	
}
