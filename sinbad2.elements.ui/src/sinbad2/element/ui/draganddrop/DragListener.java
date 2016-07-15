package sinbad2.element.ui.draganddrop;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import sinbad2.element.ProblemElement;

public class DragListener implements DragSourceListener {

	private final Viewer _viewer;
	
	public DragListener(Viewer viewer) {
		_viewer = viewer;
	}
	
	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !_viewer.getSelection().isEmpty();	
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
		ProblemElement firstElement = (ProblemElement) selection.getFirstElement();
		
		if(TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = firstElement.getCanonicalId();
		}
		
	}

	@Override
	public void dragFinished(DragSourceEvent event) {}
}