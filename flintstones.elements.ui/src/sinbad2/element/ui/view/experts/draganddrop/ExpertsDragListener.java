package sinbad2.element.ui.view.experts.draganddrop;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import sinbad2.element.expert.Expert;

public class ExpertsDragListener implements DragSourceListener {

	private final TreeViewer _treeViewer;
	
	public ExpertsDragListener(TreeViewer treeViewer) {
		_treeViewer = treeViewer;
	}
	
	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !_treeViewer.getSelection().isEmpty();	
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		Object[] objects = ((IStructuredSelection) _treeViewer.getSelection()).toArray();
		
		if(TextTransfer.getInstance().isSupportedType(event.dataType)) {
			for(Object object: objects) {
				
				event.data = ((Expert) object).getFormatId();
			}
		}
		
	}

	@Override
	public void dragFinished(DragSourceEvent event) {}
}
