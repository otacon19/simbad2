package sinbad2.core.io.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.io.IImportListener;

public class ImportHandler extends AbstractHandler {

	private static List<IImportListener> _listeners = new LinkedList<IImportListener>();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		notifyImportChange();
		return null;
	}
	
	public static void registerImportListener(IImportListener listener) {
		_listeners.add(listener);
	}

	public static void unregisterImportListener(IImportListener listener) {
		_listeners.remove(listener);
	}

	public static void notifyImportChange() {
		for (IImportListener listener : _listeners) {
			listener.notifyImportData();
		}
	}

}