package sinbad2.workspace.undoable.listener;

public interface IUndoableListener {
	
	public void notifyUndoHistoryChange(EUndoHistoryChange undoHistoryChange);

}
