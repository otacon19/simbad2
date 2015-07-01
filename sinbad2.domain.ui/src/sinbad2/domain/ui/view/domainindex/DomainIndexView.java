package sinbad2.domain.ui.view.domainindex;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.ui.view.domainindex.provider.DomainIndexContentProvider;
import sinbad2.domain.ui.view.domainindex.provider.DomainIndexIdLabelProvider;
import sinbad2.domain.ui.view.domainindex.provider.DomainIndexIndexLabelProvider;

public class DomainIndexView extends ViewPart {
	
	public static final String ID = "flintstones.domain.ui.view.domain.domainindex"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.domain.ui.view.domainindex.domainindex_view"; //$NON-NLS-1$

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TableViewer _viewer;

	public void createPartControl(Composite parent) {
		_viewer = new TableViewer(parent, SWT.BORDER | SWT.HIDE_SELECTION);
		_viewer.getTable().setHeaderVisible(true);

		// Fix for windows
		_viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});

		DomainIndexContentProvider provider = new DomainIndexContentProvider(_viewer);
		_viewer.setContentProvider(provider);

		addColumns();
		hookContextMenu();
		hookFocusListener();

		_viewer.setInput(provider.getInput());
		getSite().setSelectionProvider(_viewer);

	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_viewer, SWT.CENTER);
		tvc.getColumn().setWidth(50);
		tvc.getColumn().setResizable(false);
		tvc.getColumn().setMoveable(false);
		tvc.getColumn().setText("Index");
		tvc.setLabelProvider(new DomainIndexIndexLabelProvider());
		tvc.getColumn().pack();

		tvc = new TableViewerColumn(_viewer, SWT.FULL_SELECTION);
		tvc.getColumn().setWidth(120);
		tvc.getColumn().setText("Id");
		tvc.setLabelProvider(new DomainIndexIdLabelProvider());
		tvc.getColumn().pack();
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_viewer.getTable());
		_viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _viewer);
	}

	private void hookFocusListener() {
		_viewer.getControl().addFocusListener(new FocusListener() {

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
		_viewer.getControl().setFocus();
	}

}
