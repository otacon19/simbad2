package sinbad2.core.io.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.io.IExportAFRYCAListener;

public class ExportAFRYCAHandler extends AbstractHandler {

	private static List<IExportAFRYCAListener> _listeners = new LinkedList<IExportAFRYCAListener>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		notifyExportChange();
		return null;
	}
	
	public static void registerExportListener(IExportAFRYCAListener listener) {
		_listeners.add(listener);
	}

	public static void unregisterExportListener(IExportAFRYCAListener listener) {
		_listeners.remove(listener);
	}

	public static void notifyExportChange() {
		for (IExportAFRYCAListener listener : _listeners) {
			listener.notifyExportData();
		}
	}
}
