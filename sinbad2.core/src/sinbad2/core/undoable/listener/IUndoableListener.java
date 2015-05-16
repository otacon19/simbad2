package sinbad2.core.undoable.listener;

public interface IUndoableListener {

	public void notifyUndoHistoryChange(EUndoHistoryChange undoHistoryChange);
}
