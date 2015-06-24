package sinbad2.domain.linguistic.unbalanced.ui.dialog;

import java.util.List;

import jfreechart.LinguisticDomainChart;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.linguistic.unbalanced.ui.dialog.subdialog.SemanticDialog;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class LHDomainDialog extends NewDomainDialog {
	
	private Composite _container;
	private FuzzySet _specificDomain;
	private Label _previewLabel;
	private LinguisticDomainChart _chart;
	private Combo _lhInitialDomainCombo;
	private Spinner _numberOfLabels;
	private Spinner _slSpinner;
	private Spinner _srSpinner;
	private int _cardinality;
	private int _sl;
	private int _sr;
	private int _minimumLabels;
	private int _maximumLabels;
	private ControlDecoration _semanticButtonControlDecoration;
	private ControlDecoration _domainNameTextControlDecoration;
	private ModifyListener _numberOfLabelsModifyListener;
	private ModifyListener _slModifyListener;
	private ModifyListener _srModifyListener;
	private ModifyListener _slDensityComboModifyListener;
	private ModifyListener _srDensityComboModifyListener;
	private ModifyListener _lhInitialDomainModifyListener;
	private Label _domainLabelsLabel;
	private Label _lhFeaturesLabel;
	private Combo _srDensityCombo;
	private Combo _slDensityCombo;
	private String[] _labels;
	private Button _setSemanticButton;
	private Button _okButton;
	
	public LHDomainDialog() {
		super();
	}
	
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		_specificDomain = (Unbalanced) _domain;
	}

	@Override
	public Control createDialogArea(Composite parent) {

		_container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(4, false);
		_container.setLayout(layout);
		
		Label idLabel = new Label(_container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText("Domain id");
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		
		Text textID = new Text(_container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gridData.horizontalIndent = 5;
		textID.setLayoutData(gridData);
		textID.setText(_id);
		textID.setFocus();
		textID.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				_id = ((Text) e.getSource()).getText().trim();
				//TODO comprobar el id del specificDomain
				_domain.setId(_id);
				validate();
			}
		});

		_lhFeaturesLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1);
		gridData.verticalIndent = 10;
		_lhFeaturesLabel.setLayoutData(gridData);
		_lhFeaturesLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		_lhFeaturesLabel.setText("LH Features");

		Composite genericDomainComposite = new Composite(_container, SWT.BORDER);
		genericDomainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,true, false, 4, 1));
		genericDomainComposite.setLayout(new GridLayout(2, false));

		Label lhInitialGranularityLabel = new Label(genericDomainComposite, SWT.NULL);
		lhInitialGranularityLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.NONE));
		lhInitialGranularityLabel.setText("Initial granularity of LH");
		lhInitialGranularityLabel.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 1, 1));

		_lhInitialDomainCombo = new Combo(genericDomainComposite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gridData.widthHint = 60;
		_lhInitialDomainCombo.setLayoutData(gridData);
		_lhInitialDomainCombo.setItems(new String[] { "3", "7" }); //$NON-NLS-1$ //$NON-NLS-2$
		_lhInitialDomainCombo.select(0);
		_minimumLabels = 3;
		_maximumLabels = 17;

		_domainLabelsLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1);
		gridData.verticalIndent = 15;
		_domainLabelsLabel.setLayoutData(gridData);
		_domainLabelsLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, //$NON-NLS-1$
				SWT.BOLD));
		_domainLabelsLabel.setText("Labels of unbalanced scale");

		Composite labelsComposite = new Composite(_container, SWT.BORDER);
		labelsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		labelsComposite.setLayout(new GridLayout(4, false));

		Label numberOfLabelsLabel = new Label(labelsComposite, SWT.NULL);
		numberOfLabelsLabel.setFont(SWTResourceManager.getFont("Cantarell", 8,SWT.NONE));
		numberOfLabelsLabel.setText("Number of labels of unbalancedScale");
		numberOfLabelsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,false, false, 3, 1));

		_cardinality = 3;
		_numberOfLabels = new Spinner(labelsComposite, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 110;
		_numberOfLabels.setLayoutData(gridData);
		_numberOfLabels.setMinimum(_minimumLabels);
		_numberOfLabels.setMaximum(_maximumLabels);
		_numberOfLabels.setIncrement(2);
		_numberOfLabels.setSelection(_cardinality);

		Label slLabel = new Label(labelsComposite, SWT.NULL);
		slLabel.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NONE));
		slLabel.setText("Left side SL");
		slLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		_sl = 1;
		_slSpinner = new Spinner(labelsComposite, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 110;
		_slSpinner.setLayoutData(gridData);
		_slSpinner.setMinimum(1);
		_slSpinner.setMaximum(1);
		_slSpinner.setSelection(_sl);

		Label slDensity = new Label(labelsComposite, SWT.NULL);
		slDensity.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NONE));
		slDensity.setText("Density SL");
		slDensity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		_slDensityCombo = new Combo(labelsComposite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 110;
		_slDensityCombo.setLayoutData(gridData);
		_slDensityCombo.setItems(new String[] { "Extreme", "Center" }); //$NON-NLS-1$ //$NON-NLS-2$
		_slDensityCombo.select(0);
		_slDensityCombo.setEnabled(false);

		Label srLabel = new Label(labelsComposite, SWT.NULL);
		srLabel.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NONE));
		srLabel.setText("Right side SR");
		srLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		_sr = 1;
		_srSpinner = new Spinner(labelsComposite, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 110;
		_srSpinner.setLayoutData(gridData);
		_srSpinner.setMinimum(1);
		_srSpinner.setMaximum(1);
		_srSpinner.setSelection(_sr);

		Label srDensity = new Label(labelsComposite, SWT.NULL);
		srDensity.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NONE));
		srDensity.setText("Density SR");
		srDensity.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		_srDensityCombo = new Combo(labelsComposite, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 110;
		_srDensityCombo.setLayoutData(gridData);
		_srDensityCombo.setItems(new String[] { "Extreme", "Center" }); //$NON-NLS-1$ //$NON-NLS-2$
		_srDensityCombo.select(0);
		_srDensityCombo.setEnabled(false);

		_setSemanticButton = new Button(labelsComposite, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1);
		gridData.verticalIndent = 5;
		_setSemanticButton.setLayoutData(gridData);
		_setSemanticButton.setText("Set semantic");

		_semanticButtonControlDecoration = createNotificationDecorator(_setSemanticButton);
		_domainNameTextControlDecoration = createNotificationDecorator(textID);

		_previewLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 4, 1);
		gridData.verticalIndent = 15;
		_previewLabel.setLayoutData(gridData);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		_previewLabel.setText("Preview");

		_labels = new String[] { "s0", "s1", "s2" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_specificDomain = ((FuzzySet) _domain).createTrapezoidalFunction(_labels);

		Composite composite = new Composite(_container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		DomainUIsManager manager = DomainUIsManager.getInstance();
		_chart = (LinguisticDomainChart) manager.newDomainChart(_specificDomain);
		_chart.initialize(_specificDomain, composite, 530, 145, SWT.BORDER);

		modifyDomain();

		hookModifyListeners();

		hookSemanticButtonListener();
		
		return _container;

	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create unbalanced domain");
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_domainNameTextControlDecoration.show();
		_semanticButtonControlDecoration.show();
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	private void hookSemanticButtonListener() {
		_setSemanticButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SemanticDialog dialog = new SemanticDialog(_labels);
				if (dialog.open() == SemanticDialog.OK) {
					List<String> auxLabels = dialog.getValues();
					for (int i = 0; i < auxLabels.size(); i++) {
						_labels[i] = auxLabels.get(i);
					}
					
					modifyDomainSemantic();
				}

			}
		});
	}
	
	private void hookModifyListeners() {

		_lhInitialDomainModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				boolean modify = false;
				if (_lhInitialDomainCombo.getSelectionIndex() == 1) {
					if (_minimumLabels == 3) {
						modify = true;
						_sl = 3;
						_sr = 3;
						_minimumLabels = 7;
						_maximumLabels = 25;
						_cardinality = _minimumLabels;
					}
				} else {
					if (_minimumLabels == 7) {
						modify = true;
						_sl = 1;
						_sr = 1;
						_minimumLabels = 3;
						_maximumLabels = 17;
						_cardinality = _minimumLabels;
					}

				}

				if (modify) {
					_numberOfLabels
							.removeModifyListener(_numberOfLabelsModifyListener);
					_slSpinner.removeModifyListener(_slModifyListener);
					_srSpinner.removeModifyListener(_srModifyListener);
					_slDensityCombo
							.removeModifyListener(_slDensityComboModifyListener);
					_srDensityCombo
							.removeModifyListener(_srDensityComboModifyListener);

					_numberOfLabels.setValues(_cardinality, _minimumLabels,
							_maximumLabels, 0, 2, 2);
					_slSpinner.setValues(_sl, _sl, _sl, 0, 1, 1);
					_srSpinner.setValues(_sr, _sr, _sr, 0, 1, 1);
					_slDensityCombo.setEnabled(false);
					_slDensityCombo.select(0);
					_srDensityCombo.setEnabled(false);
					_srDensityCombo.select(0);

					_numberOfLabels
							.addModifyListener(_numberOfLabelsModifyListener);
					_slSpinner.addModifyListener(_slModifyListener);
					_srSpinner.addModifyListener(_srModifyListener);
					_slDensityCombo
							.addModifyListener(_slDensityComboModifyListener);
					_srDensityCombo
							.addModifyListener(_srDensityComboModifyListener);

					modifyDomain();
				}

			}

		};

		_numberOfLabelsModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				int cardinality = _numberOfLabels.getSelection();
				if (cardinality < _minimumLabels) {
					_numberOfLabels.setSelection(_minimumLabels);
				}
				if (cardinality > _maximumLabels) {
					_numberOfLabels.setSelection(_maximumLabels);
				}
				if ((cardinality % 2) == 0) {
					if (cardinality > _cardinality) {
						cardinality++;
					} else {
						cardinality--;
					}
					_numberOfLabels.setSelection(cardinality);
				} else {
					int difference;
					_srSpinner.removeModifyListener(_srModifyListener);
					_slSpinner.removeModifyListener(_slModifyListener);
					int maximum = cardinality - ((_minimumLabels - 1) / 2) - 1;
					if (maximum > ((_maximumLabels - 1) / 2)) {
						maximum = ((_maximumLabels - 1) / 2);
					}
					int minimum = cardinality - ((_maximumLabels - 1) / 2) - 1;
					if (minimum < ((_minimumLabels - 1) / 2)) {
						minimum = ((_minimumLabels - 1) / 2);
					}
					_slSpinner.setMinimum(minimum);
					_srSpinner.setMinimum(minimum);
					_slSpinner.setMaximum(maximum);
					_srSpinner.setMaximum(maximum);
					if (cardinality > _cardinality) {
						difference = cardinality - _cardinality;
						if (_sl == ((_maximumLabels - 1) / 2)) {
							_sr += difference;
							_srSpinner.setSelection(_sr);
						} else if (_sr == ((_maximumLabels - 1) / 2)) {
							_sl += difference;
							_slSpinner.setSelection(_sl);
						} else {
							difference /= 2;
							_sl += difference;
							_sr += difference;
							_slSpinner.setSelection(_sl);
							_srSpinner.setSelection(_sr);
						}

					} else if (cardinality < _cardinality) {
						difference = _cardinality - cardinality;
						if (_sl == ((_minimumLabels - 1) / 2)) {
							_sr -= difference;
							_srSpinner.setSelection(_sr);
						} else if (_sr == ((_minimumLabels - 1) / 2)) {
							_sl -= difference;
							_slSpinner.setSelection(_sl);
						} else {
							difference /= 2;
							_sl -= difference;
							_sr -= difference;
							_slSpinner.setSelection(_sl);
							_srSpinner.setSelection(_sr);
						}
					}

					_cardinality = cardinality;
					
					modifyDensityCombos();
					_srSpinner.addModifyListener(_srModifyListener);
					_slSpinner.addModifyListener(_slModifyListener);

					modifyDomain();
				}
			}
		};

		_slModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				int sl = _slSpinner.getSelection();

				_srSpinner.removeModifyListener(_srModifyListener);
				if (sl > _sl) {
					_sr--;
					_srSpinner.setSelection(_sr);
				} else {
					_sr++;
					_srSpinner.setSelection(_sr);
				}
				_sl = sl;

				modifyDensityCombos();
				_srSpinner.addModifyListener(_srModifyListener);

				modifyDomain();
			}
		};

		_srModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				int sr = _srSpinner.getSelection();

				_slSpinner.removeModifyListener(_slModifyListener);
				if (sr > _sr) {
					_sl--;
					_slSpinner.setSelection(_sl);
				} else {
					_sl++;
					_slSpinner.setSelection(_sl);
				}
				_sr = sr;

				modifyDensityCombos();
				_slSpinner.addModifyListener(_slModifyListener);

				modifyDomain();
			}
		};

		_slDensityComboModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				modifyDomain();
			}
		};

		_srDensityComboModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				modifyDomain();
			}
		};

		_lhInitialDomainCombo.addModifyListener(_lhInitialDomainModifyListener);
		_numberOfLabels.addModifyListener(_numberOfLabelsModifyListener);
		_slSpinner.addModifyListener(_slModifyListener);
		_srSpinner.addModifyListener(_srModifyListener);
		_slDensityCombo.addModifyListener(_slDensityComboModifyListener);
		_srDensityCombo.addModifyListener(_srDensityComboModifyListener);
	}

	private void modifyDensityCombos() {

		_slDensityCombo.removeModifyListener(_slDensityComboModifyListener);
		_srDensityCombo.removeModifyListener(_srDensityComboModifyListener);

		if (!direct(_sl)) {
			if (!_slDensityCombo.isEnabled()) {
				_slDensityCombo.setEnabled(true);
			}
		} else {
			if (_slDensityCombo.isEnabled()) {
				_slDensityCombo.setEnabled(false);
			}
		}

		if (!direct(_sr)) {
			if (!_srDensityCombo.isEnabled()) {
				_srDensityCombo.setEnabled(true);
			}
		} else {
			if (_srDensityCombo.isEnabled()) {
				_srDensityCombo.setEnabled(false);
			}
		}

		_slDensityCombo.addModifyListener(_slDensityComboModifyListener);
		_srDensityCombo.addModifyListener(_srDensityComboModifyListener);
	}

	private void modifyDomain() {

		_labels = new String[_cardinality];

		for (int i = 0; i < _cardinality; i++) {
			_labels[i] = "s" + i; //$NON-NLS-1$
		}

		modifyDomainSemantic();
	}

	private void modifyDomainSemantic() {

		int initialDomain = 0;
		if (_lhInitialDomainCombo.getSelectionIndex() == 0) {
			initialDomain = 3;
		} else {
			initialDomain = 7;
		}
		
		_specificDomain = ((Unbalanced)_domain).createUnbalancedDomain(_labels, _sr, _sl,_slDensityCombo.getSelectionIndex(),_srDensityCombo.getSelectionIndex(), initialDomain);
		_chart.setDomain(_specificDomain);
	}

	private boolean direct(int value) {

		int minimum = (_minimumLabels - 1) / 2;

		while (value > minimum) {
			minimum *= 2;
		}

		return (value == minimum);

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
