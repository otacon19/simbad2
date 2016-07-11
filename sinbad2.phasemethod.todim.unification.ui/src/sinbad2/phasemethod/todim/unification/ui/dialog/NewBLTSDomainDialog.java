package sinbad2.phasemethod.todim.unification.ui.dialog;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.phasemethod.todim.unification.ui.nls.Messages;

public class NewBLTSDomainDialog extends Dialog {

	private Integer value;
	private Button _buttonSave;
	private Button _buttonCancel;
	private Spinner _spinner;
	private Label _previewLabel;
	private String[] _labels;
	private Button _setSemanticButton;
	
	private LinguisticDomainChart _fuzzySetChart;
	private FuzzySet _domainBLTS;
	
	public NewBLTSDomainDialog(Shell parent) {
		super(parent);
	}
	
	public NewBLTSDomainDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	public Integer open() {
		_domainBLTS = new FuzzySet();
		
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText(Messages.NewBLTSDomainDialog_New_BLTS_domain);
	
		GridLayout layout = new GridLayout(3, false);
		layout.marginLeft = 15;
		layout.marginRight = 15;
		layout.marginTop = 15;
		layout.marginBottom = 15;
		layout.verticalSpacing = 15;
		layout.horizontalSpacing = 15;
		shell.setLayout(layout);
	
		Label label = new Label(shell, SWT.NULL);
		label.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		label.setText(Messages.NewBLTSDomainDialog_Numer_of_labels);
	
		_spinner = new Spinner(shell, SWT.SINGLE | SWT.BORDER);
		_spinner.setSelection(3);
		_spinner.setMinimum(3);
		_spinner.setIncrement(2);
		_spinner.setMaximum(Integer.MAX_VALUE);
		_spinner.addModifyListener(new ModifyListener() {
	
			@Override
			public void modifyText(ModifyEvent e) {
				int selection = _spinner.getSelection();
				boolean valid = (selection >= 3);
				if (valid) {
					valid = ((selection % 2) == 1);
				}
				_buttonSave.setEnabled(valid);
				modifyDomain();
			}
		});
	
		_setSemanticButton = new Button(shell, SWT.NONE);
		GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		_setSemanticButton.setLayoutData(gridData);
		_setSemanticButton.setText(Messages.NewBLTSDomainDialog_Set_semantic);
	
		ControlDecoration controlDecoration = new ControlDecoration(_setSemanticButton, SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.setDescriptionText(Messages.NewBLTSDomainDialog_If_you_make_changes_to_the_data_the_semantic_will_be_lost);
	
		_setSemanticButton.addSelectionListener(new SelectionAdapter() {
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				SemanticDialog dialog = new SemanticDialog(shell, _labels);
				if (dialog.open() == SemanticDialog.SAVE) {
					List<String> auxLabels = dialog.getValues();
					for (int i = 0; i < auxLabels.size(); i++) {
						_labels[i] = auxLabels.get(i);
					}
					
					modifyDomainSemantic();
				}
	
			}
		});
	
		_previewLabel = new Label(shell, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1);
		gridData.verticalIndent = 15;
		_previewLabel.setLayoutData(gridData);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		_previewLabel.setText(Messages.NewBLTSDomainDialog_Preview);
	
		_labels = new String[] { "s0", "s1", "s2" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		_domainBLTS.createTrapezoidalFunction(_labels);
	
		Composite chartComposite = new Composite(shell, SWT.NONE);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		_fuzzySetChart = new LinguisticDomainChart();
		_fuzzySetChart.initialize(_domainBLTS, chartComposite, 550, 200, SWT.BORDER);
	
		Composite buttonsBar = new Composite(shell, SWT.NONE);
		buttonsBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 3, 1));
		buttonsBar.setLayout(new GridLayout(4, true));
		
		new Label(buttonsBar, SWT.NONE);
		new Label(buttonsBar, SWT.NONE);
		
		_buttonCancel = new Button(buttonsBar, SWT.PUSH);
		_buttonCancel.setText(Messages.NewBLTSDomainDialog_Cancel);
		GridData gd__buttonCancel = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd__buttonCancel.horizontalAlignment = SWT.FILL;
		_buttonCancel.setLayoutData(gd__buttonCancel);
	
		_buttonSave = new Button(buttonsBar, SWT.PUSH);
		_buttonSave.setText(Messages.NewBLTSDomainDialog_Save);
		GridData gd__buttonOK = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd__buttonOK.horizontalAlignment = SWT.FILL;
		_buttonSave.setLayoutData(gd__buttonOK);
	
		_buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				value = _spinner.getSelection();
				shell.dispose();
			}
		});
	
		_buttonCancel.addSelectionListener(new SelectionAdapter() {
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				value = null;
				shell.dispose();
			}
		});
	
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					event.doit = false;
			}
		});
	
		shell.pack();
		shell.open();
	
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	
		return value;
	}
	
	private void modifyDomain() {

		int cardinality = _spinner.getSelection();
		_labels = new String[cardinality];

		for (int i = 0; i < cardinality; i++) {
			_labels[i] = "s" + i; //$NON-NLS-1$
		}

		modifyDomainSemantic();
	}
	
	private void modifyDomainSemantic() {
		_domainBLTS.createTrapezoidalFunction(_labels);
		_fuzzySetChart.setDomain(_domainBLTS);
	}
	
	public FuzzySet getDomain() {
		return _domainBLTS;
	}
}
