package sinbad2.valuation.valuationset.ui.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import sinbad2.element.ProblemElement;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ui.view.listener.IRemoveValuationListener;
import sinbad2.valuation.valuationset.ui.view.provider.ElementValuationsTableContentProvider;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellSelectionListener;

public class ElementValuationsTable extends KTable implements ISelectionProvider {
	
	private ElementValuationsTableContentProvider _model;
	private List<ISelectionChangedListener> _listeners;
	private List<IRemoveValuationListener> _removeValuationListeners;
	private StructuredSelection _selection = new StructuredSelection();

	public ElementValuationsTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWT.V_SCROLL);
		setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_model = null;
		_listeners = new LinkedList<ISelectionChangedListener>();
		_removeValuationListeners = new LinkedList<IRemoveValuationListener>();
	}

	public void setModel(ProblemElement element) {
		_model = new ElementValuationsTableContentProvider(this, element);
		setModel(_model);
		getParent().getParent().layout();

		super.addCellSelectionListener(new KTableCellSelectionListener() {

			@Override
			public void fixedCellSelected(int arg0, int arg1, int arg2) {}

			@Override
			public void cellSelected(int arg0, int arg1, int arg2) {
				setSelection(new StructuredSelection(_model.getValuationKey(arg0, arg1)));
			}
		});

		super.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (SWT.DEL == e.character) {
					if (_selection != null) {
						if (!_selection.isEmpty()) {
							notifyRemove((ValuationKey) _selection.getFirstElement());
						}
					}
				}

			}
		});
	}

	@Override
	public void dispose() {
		_selection = new StructuredSelection();
		_model.dispose();
		super.dispose();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return _selection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		_selection = (StructuredSelection) selection;
		
		for (ISelectionChangedListener listener : _listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

	public void registerRemoveValuationListener(IRemoveValuationListener listener) {
		_removeValuationListeners.add(listener);
	}

	public void unregisterRemoveValuationListener(IRemoveValuationListener listener) {
		_removeValuationListeners.remove(listener);
	}

	public void notifyRemove(ValuationKey key) {
		
		for (IRemoveValuationListener listener : _removeValuationListeners) {
			listener.notifyRemoveValuation(key);
		}
	}

}
