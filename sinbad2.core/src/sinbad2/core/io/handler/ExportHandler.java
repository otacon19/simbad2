package sinbad2.core.io.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.io.IExportListener;

public class ExportHandler extends AbstractHandler {

	private static List<IExportListener> _listeners = new LinkedList<IExportListener>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		notifyExportChange();
		return null;
	}
	
	public static void registerExportListener(IExportListener listener) {
		_listeners.add(listener);
	}

	public static void unregisterExportListener(IExportListener listener) {
		_listeners.remove(listener);
	}

	public static void notifyExportChange() {
		for (IExportListener listener : _listeners) {
			listener.notifyExportData();
		}
	}

}
