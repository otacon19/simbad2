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
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.provider.SATableContentProvider;

public class SATable extends KTable implements ISelectionProvider {
	
	private ListenerList listeners = new ListenerList();
	private ISelection _selection = new StructuredSelection(0d);
	private SATableContentProvider _provider;
	@SuppressWarnings("unused")
	private String[] _criteria;
	@SuppressWarnings("unused")
	private String[] _alternatives;
	@SuppressWarnings("unused")
	private double[] _w;
	@SuppressWarnings("unused")
	private double[][] _dm;

	public SATable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT);
	}

	public void setModel(String[] alternatives, String[] criteria, Double[][][] values, double[][] dm, double[] w) {
		
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		_alternatives = alternatives;
		_criteria = criteria;
		_dm = dm;
		_w = w;
		_provider = new SATableContentProvider(this, alternatives, criteria, values);
		setModel(_provider);

		getParent().layout();

		super.addCellSelectionListener(new KTableCellSelectionListener() {

			@Override
			public void fixedCellSelected(int arg0, int arg1, int arg2) {}

			@Override
			public void cellSelected(int arg0, int arg1, int arg2) {
				//TODO
				setSelection(new StructuredSelection(0d));
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

}
