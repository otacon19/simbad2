package sinbad2.core.ui.undoable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import sinbad2.core.undoable.HistoryState;
import sinbad2.core.undoable.listener.EUndoHistoryChange;
import sinbad2.core.undoable.listener.IUndoableListener;

public class UndoableSourceProvider extends AbstractSourceProvider implements
		IUndoableListener {

	public final static String UNDO_STATE = "flintstones.ui.undo.state"; //$NON-NLS-1$
	public final static String REDO_STATE = "flintstones.ui.redo.state"; //$NON-NLS-1$
	public final static String ENABLED = "ENABLED"; //$NON-NLS-1$
	public final static String DISENABLED = "DISENABLED"; //$NON-NLS-1$

	private boolean undo = true;
	private boolean redo = true;

	public UndoableSourceProvider() {
		HistoryState.getInstance().registerUndoableListener(this);
	}

	@Override
	public void dispose() {
		HistoryState.getInstance().unregisterUndoableListener(this);
	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> map = new HashMap<String, String>();

		String value = undo ? ENABLED : DISENABLED;
		map.put(UNDO_STATE, value);

		value = redo ? ENABLED : DISENABLED;
		map.put(REDO_STATE, value);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { UNDO_STATE, REDO_STATE };
	}

	@Override
	public void notifyUndoHistoryChange(EUndoHistoryChange undoHistoryChange) {
		boolean oldUndo = undo;
		boolean oldRedo = redo;

		switch (undoHistoryChange) {
		case UNDO_ENABLED:
			undo = true;
			break;
		case UNDO_DISENABLED:
			undo = false;
			break;
		case REDO_ENABLED:
			redo = true;
			break;
		case REDO_DISENABLED:
			redo = false;
			break;
		}

		if (undo != oldUndo) {
			if (undo) {
				fireSourceChanged(ISources.WORKBENCH, UNDO_STATE, ENABLED);
			} else {
				fireSourceChanged(ISources.WORKBENCH, UNDO_STATE, DISENABLED);
			}
		}

		if (redo != oldRedo) {
			if (redo) {
				fireSourceChanged(ISources.WORKBENCH, REDO_STATE, ENABLED);
			} else {
				fireSourceChanged(ISources.WORKBENCH, REDO_STATE, DISENABLED);
			}
		}
	}
}
