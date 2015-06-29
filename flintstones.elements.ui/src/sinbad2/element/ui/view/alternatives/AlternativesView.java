package sinbad2.element.ui.view.alternatives;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.services.ISourceProviderService;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.handler.alternative.modify.ModifyAlternativeHandler;
import sinbad2.element.ui.sourceprovider.ElementSourceProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativeIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesContentProvider;

public class AlternativesView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.alternatives"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.alternatives.alternatives_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

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
		hookDoubleClickListener();
		
		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		
	}
	
	private void hookDoubleClickListener() {
		_tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			ISourceProviderService sourceProviderService = null;
			ElementSourceProvider elementSourceProvider = null;
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				
				if(elementSourceProvider == null) {
					if(sourceProviderService == null) {
						sourceProviderService = (ISourceProviderService) getSite()
								.getService(ISourceProviderService.class);
					}
					elementSourceProvider = (ElementSourceProvider) sourceProviderService
							.getSourceProvider(ElementSourceProvider.CAN_BE_MODIFIED_STATE);
				}
				
				if(ElementSourceProvider.ENABLED.equals(elementSourceProvider
						.getCurrentState().get(ElementSourceProvider.CAN_BE_MODIFIED_STATE))) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					Alternative alternative = (Alternative) selection.getFirstElement();
					
					ModifyAlternativeHandler modifyAlternativeHandler = new ModifyAlternativeHandler(alternative);
					try {
						modifyAlternativeHandler.execute(null);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		
	}
	
	private void hookFocusListener() {
		_tableViewer.getControl().addFocusListener(new FocusListener() {
			
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
	
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}

}
