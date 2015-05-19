package sinbad2.element.ui.view.alternatives;

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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ui.view.alternatives.provider.AlternativeIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesContentProvider;

public class AlternativesView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.alternatives";
	public static final String CONTEXT_ID = "flintstones.element.ui.view.alternatives.alternatives_view";
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	//TODO context
	
	private TableViewer _tableViewer;
	
	private AlternativesContentProvider _provider;
	
	public AlternativesView() {}
	
	@Override
	public void createPartControl(Composite parent) {
		_tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		
		_provider = new AlternativesContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		//TODO hookDoubleClickListener();
		
		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		
	}
	
	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new AlternativeIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setResizable(false);
		tc.pack();
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
	}
	
	private void hookFocusListener() {
		_tableViewer.getControl().addFocusListener(new FocusListener() {
			
			private IContextActivation activation = null;
			
			@Override
			public void focusLost(FocusEvent e) {
				//_contextService.deactivateContext(activation);
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				//activation = _contextService.activateContext(CONTEXT_ID);
				
			}
		});
	}

	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}

}
