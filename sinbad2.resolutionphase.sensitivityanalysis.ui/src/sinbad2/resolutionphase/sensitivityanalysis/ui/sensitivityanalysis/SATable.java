package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.SWTX;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.provider.SATableContentProvider;

public class SATable extends KTable implements ISelectionProvider {

	private ISelection _selection = new StructuredSelection(0d);
	private SATableContentProvider _provider;
	
	private static ListenerList listeners = new ListenerList();

	public SATable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
	}

	public void setModel(String[] alternatives, String[] criteria, Double[][][] values, double[][] dm, double[] w) {
		
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		_provider = new SATableContentProvider(this, alternatives, criteria, values);
		setModel(_provider);

		getParent().layout();

		super.addCellSelectionListener(new KTableCellSelectionListener() {

			@Override
			public void fixedCellSelected(int arg0, int arg1, int arg2) {}

			@Override
			public void cellSelected(int col, int row, int arg2) {
				if(col == 0 && row != 0) {
					String pair = (String) _provider.getContentAt(col, row);
					String a1 = pair.substring(0, pair.indexOf("-")); //$NON-NLS-1$
					String a2 = pair.substring(pair.indexOf("-") + 1, pair.length()); //$NON-NLS-1$
					
					int numAlternative1 = Integer.parseInt(a1.substring(1, a1.length()));
					int numAlternative2 = Integer.parseInt(a2.substring(1, a2.length()));
					
					Object[] pairAlternatives = new Object[2];
					pairAlternatives[0] = numAlternative1 - 1;
					pairAlternatives[1] = numAlternative2 - 1;
					
					setSelection(new StructuredSelection(pairAlternatives));
				} else if(col != 0 && row == 0) {
					String criterion = (String) _provider.doGetTooltipAt(col, row);
					setSelection(new StructuredSelection(criterion));
				}
			}
		});
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(_selection);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		_selection = selection;
		Object[] list = listeners.getListeners();
		for(int i = 0; i < list.length; i++) {
			((ISelectionChangedListener) list[i]).selectionChanged(new SelectionChangedEvent(this, selection));
		}

	}
	
	public SATableContentProvider getProvider() {
		return _provider;
	}

}
