package sinbad2.resolutionphase.analysis.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

public class MethodSelectorView extends ViewPart {
	public MethodSelectorView() {
	}
	
	public static final String ID = "flintstones.resolutionphase.analysis.ui.view.methodselector"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.analysis.ui.view.methodselector.methodselector_view"; //$NON-NLS-1$

	private Composite _container;
	
	@Override
	public void createPartControl(Composite parent) {
		_container = new Composite(parent, SWT.NONE);
		_container.setLayout(new GridLayout(1, false));
		
		Composite compositeCombo = new Composite(_container, SWT.FILL);
		GridData gd_compositeCombo = new GridData(GridData.FILL_HORIZONTAL);
		gd_compositeCombo.horizontalAlignment = SWT.CENTER;
		compositeCombo.setLayoutData(gd_compositeCombo);
		compositeCombo.setLayout(new GridLayout(2, false));
		
		Label method = new Label(compositeCombo, SWT.NONE);
		method.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		method.setText("Method");
		method.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.NONE));
		
		Combo selectMethodCombo = new Combo(compositeCombo, SWT.READ_ONLY);
		selectMethodCombo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		selectMethodCombo.add("Heterogeneous");
		selectMethodCombo.add("Unbalanced");
		selectMethodCombo.add("LH");
		selectMethodCombo.add("ELH");
		selectMethodCombo.add("Hesitant");
		selectMethodCombo.add("Numeric");
		
		Composite compositeInformation = new Composite(_container, SWT.NONE);
		compositeInformation.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeInformation.setLayout(new GridLayout(2, true));
		
		Composite compositeDescription= new Composite(compositeInformation, SWT.NONE);
		compositeDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeDescription.setLayout(new GridLayout(1, true));
		
		Label description = new Label(compositeDescription, SWT.NONE);
		GridData gd_description = new GridData(GridData.FILL_VERTICAL);
		gd_description.grabExcessVerticalSpace = false;
		gd_description.verticalAlignment = SWT.TOP;
		gd_description.horizontalAlignment = SWT.CENTER;
		description.setLayoutData(gd_description);
		description.setText("Method description");
		description.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		
		Text textDescription = new Text(compositeDescription, SWT.BORDER);
		textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite compositePhases = new Composite(compositeInformation, SWT.NONE);
		compositePhases.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositePhases.setLayout(new GridLayout(1, true));	
		Label phases = new Label(compositePhases, SWT.NONE);
		phases.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		phases.setText("Method phases");
		phases.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		
		Text textPhases = new Text(compositePhases, SWT.BORDER);
		textPhases.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}

	@Override
	public void setFocus() {
		_container.setFocus();
		
	}

}
