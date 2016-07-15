
package sinbad2.element.ui.view.elements;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ui.view.elements.provider.ElementContentProvider;
import sinbad2.element.ui.view.elements.provider.ElementIdLabelProvider;

public class ElementView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.element"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.element.element_view"; //$NON-NLS-1$

	private static final IContextService _contextService = (IContextService) PlatformUI
			.getWorkbench().getService(IContextService.class);

	private String _partName = null;
	private ElementView _instance = null;

	private TreeViewer _treeViewer;

	private ElementContentProvider _provider;

	public ElementView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		_instance = this;
		_partName = getPartName();

		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		parent.setLayout(layout);
		_treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		Tree tree = _treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// Fix for windows
		_treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});

		_provider = new ElementContentProvider(_treeViewer);
		_treeViewer.setContentProvider(_provider);
		 
		addColumns();
		addCombo(parent);
		hookContextMenu();
		hookFocusListener();
		
		_treeViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_treeViewer);
	}

	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewer, SWT.NONE);
		tvc.setLabelProvider(new ElementIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setMoveable(false);
		tc.setResizable(false);
		tc.pack();
	}

	private void addCombo(Composite parent) {
		final Combo combo = new Combo(parent, SWT.BORDER);
		combo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		combo.setItems(_provider.getTypesToString());
		combo.select(0);
		_instance.setPartName(_partName + " | " + combo.getItem(0)); //$NON-NLS-1$
		_provider.setType(_provider.getTypes()[0]);
		
		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				int index = combo.getSelectionIndex();
				_provider.setType(_provider.getTypes()[index]);
				_instance.setPartName(_partName + " | " + combo.getItem(index)); //$NON-NLS-1$
				selectFirst();
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _treeViewer);
	}

	private void hookFocusListener() {
		_treeViewer.getControl().addFocusListener(new FocusListener() {

			private IContextActivation activation = null;

			@Override
			public void focusLost(FocusEvent e) {
				_contextService.deactivateContext(activation);
			}

			@Override
			public void focusGained(FocusEvent e) {
				activation = _contextService.activateContext(CONTEXT_ID);
			}
		});
	}

	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
	}

	public boolean canSelect() {
		return _provider.canSelect();
	}

	public void selectFirst() {
		
		if (canSelect()) {
			_treeViewer.setSelection(new StructuredSelection(_provider.getElements(_treeViewer.getInput())[0]));

		}
	}
	
	public ISelection getSelection() {
		return _treeViewer.getSelection();
	}

	
}
