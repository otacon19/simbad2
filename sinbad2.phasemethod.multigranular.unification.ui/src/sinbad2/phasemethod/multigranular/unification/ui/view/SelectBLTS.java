package sinbad2.phasemethod.multigranular.unification.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class SelectBLTS extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.multigranular.unification.ui.view.selectblts";

	private Composite _parent;
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public Composite getParent() {
		return _parent;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		setPartName("Select BLTS");
	}
	
}
