package undoable.listener;

public interface IUndoableListener {
	
	public void notifyUndoHistoryChange(EUndoHistoryChange undoHistoryChange);

}
