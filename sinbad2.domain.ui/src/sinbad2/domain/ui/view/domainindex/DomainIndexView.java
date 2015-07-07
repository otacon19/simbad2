package sinbad2.domain.ui.view.domainindex;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.ui.nls.Messages;
import sinbad2.domain.ui.view.domainindex.provider.DomainIndexContentProvider;
import sinbad2.domain.ui.view.domainindex.provider.DomainIndexIdLabelProvider;
import sinbad2.domain.ui.view.domainindex.provider.DomainIndexIndexLabelProvider;

public class DomainIndexView extends ViewPart {
	
	public static final String ID = "flintstones.domain.ui.view.domain.domainindex"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.domain.ui.view.domainindex.domainindex_view"; //$NON-NLS-1$

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TableViewer _tableViewer;

	public void createPartControl(Composite parent) {
		_tableViewer = new TableViewer(parent, SWT.BORDER | SWT.HIDE_SELECTION);
		_tableViewer.getTable().setHeaderVisible(true);

		// Fix for windows
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});

		DomainIndexContentProvider provider = new DomainIndexContentProvider(_tableViewer);
		_tableViewer.setContentProvider(provider);

		addColumns();
		hookContextMenu();
		hookFocusListener();

		_tableViewer.setInput(provider.getInput());
		getSite().setSelectionProvider(_tableViewer);

	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.getColumn().setWidth(50);
		tvc.getColumn().setResizable(false);
		tvc.getColumn().setMoveable(false);
		tvc.getColumn().setText(Messages.DomainIndexView_Index);
		tvc.setLabelProvider(new DomainIndexIndexLabelProvider());
		tvc.getColumn().pack();

		tvc = new TableViewerColumn(_tableViewer, SWT.FULL_SELECTION);
		tvc.getColumn().setWidth(150);
		tvc.getColumn().setText("Id"); //$NON-NLS-1$
		tvc.setLabelProvider(new DomainIndexIdLabelProvider());
		
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

	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}

}
