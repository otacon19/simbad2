package sinbad2.domain.ui.view.domains;


import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.view.domain.DomainView;
import sinbad2.domain.ui.view.domain.DomainViewManager;
import sinbad2.domain.ui.view.domains.provider.DomainDescriptionLabelProvider;
import sinbad2.domain.ui.view.domains.provider.DomainIdLabelProvider;
import sinbad2.domain.ui.view.domains.provider.DomainTypeLabelProvider;
import sinbad2.domain.ui.view.domains.provider.DomainValuationUsedLabelProvider;
import sinbad2.domain.ui.view.domains.provider.DomainsContentProvider;

public class DomainsView extends ViewPart {
	
	public static final String ID = "flintstones.domain.ui.view.domains";
	
	private String _perspectiveID;
	
	private IWorkbenchPage _multiViews;
	private TableViewer _tableViewer;
	private DomainsContentProvider _provider;
	private Domain _selectedDomain;
	private DomainView _domainView;
	
	public DomainsView() {
		super();
		_perspectiveID = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
		_multiViews = null;
		_selectedDomain = null;
		_domainView = null;	
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_tableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		_tableViewer.getTable().setHeaderVisible(true);
		
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
				
			}
		});
		
		_provider = new DomainsContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		addColumns();
		hookContextMenu();
		//TODO hookFocusListener();
		hookSelectionChangedListener();
		hookDoubleClickListener();
		
		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		
	}

	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}
	
	private void addColumns() {
		
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.getColumn().setWidth(20);
		tvc.getColumn().setText("T");
		tvc.setLabelProvider(new DomainTypeLabelProvider());
		
		tvc = new TableViewerColumn(_tableViewer, SWT.FULL_SELECTION);
		tvc.getColumn().setWidth(120);
		tvc.getColumn().setText("Id");
		tvc.setLabelProvider(new DomainIdLabelProvider());
		
		tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.getColumn().setWidth(150);
		tvc.getColumn().setText("Description");
		tvc.setLabelProvider(new DomainDescriptionLabelProvider());
		
		tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.getColumn().setText("Valuation");
		tvc.getColumn().setWidth(150);
		tvc.setLabelProvider(new DomainValuationUsedLabelProvider());
		
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
	}
	
	private void hookSelectionChangedListener() {
		_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				if(!_tableViewer.getSelection().isEmpty()) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					_selectedDomain = (Domain) selection.getFirstElement();
				} else {
					_selectedDomain = null;
				}
				
				DomainViewManager domainViewManager = DomainViewManager.getInstance();
				
				if(_multiViews == null) {
					_multiViews = getSite().getPage();
				}
				
				if(_domainView == null) {
					for(IViewReference viewReference: _multiViews.getViewReferences()) {
						if(DomainView.ID.equals(viewReference.getId())) {
							_domainView = (DomainView) viewReference.getView(false);
						}
					}
				}
				
				String actualPerspectiveID = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
				
				if(actualPerspectiveID.equals(_perspectiveID)) {
					if(_selectedDomain == null) {
						if(_domainView != null) {
							_multiViews.hideView(_domainView);
							_domainView = null;
						}
					} else {
						if(_domainView == null) {
							try {
								_domainView = (DomainView) _multiViews.showView(DomainView.ID);
								setFocus();
							} catch (PartInitException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				domainViewManager.setContent(_selectedDomain, null);
				
			}
		});
	}
	
	private void hookDoubleClickListener() {
		_tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				//TODO
				//ModifyDomainHandler modifyDomainHandler = new ModifyDomainHandler(_selectedDomain);
				//modifyDomainHandler.execute(null);		
			}
		});
	}
	

}
