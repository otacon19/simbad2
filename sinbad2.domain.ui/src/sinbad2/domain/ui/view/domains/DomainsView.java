package sinbad2.domain.ui.view.domains;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.handler.modify.ModifyDomainHandler;
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
		_tableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.NO_SCROLL);
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
		tvc.getColumn().setWidth(150);
		tvc.getColumn().setText("Valuation");
		tvc.setLabelProvider(new DomainValuationUsedLabelProvider());
		
		_tableViewer.getTable().addControlListener(new ControlAdapter() {
	        
			public void controlResized(ControlEvent e) {
	            packAndFillLastColumn();
	        }

			private void packAndFillLastColumn() {
				Table table = _tableViewer.getTable();
			    int columnsWidth = 0;
			    
			    for (int i = 0; i < table.getColumnCount() - 1; i++) {
			        columnsWidth += table.getColumn(i).getWidth();
			    }
			    TableColumn lastColumn = table.getColumn(table.getColumnCount() - 1);
			    lastColumn.pack();

			    Rectangle area = table.getClientArea();

			    Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			    int width = area.width - 2*table.getBorderWidth();

			    if (preferredSize.y > area.height + table.getHeaderHeight()) {
			        Point vBarSize = table.getVerticalBar().getSize();
			        width -= vBarSize.x;
			    }

			    if(lastColumn.getWidth() < width - columnsWidth) {
			        lastColumn.setWidth(width - columnsWidth);
			    }
				
			}
	    });
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
				ModifyDomainHandler modifyDomainHandler = new ModifyDomainHandler(_selectedDomain);
				try {
					modifyDomainHandler.execute(null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}		
			}
		});
	}
	

}
