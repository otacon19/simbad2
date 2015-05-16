package sinbad2.core.workspace;

public interface IWorkspaceContent {
	public void clear();

	public IWorkspaceContent copyStructure();

	public IWorkspaceContent copyData();

	public void copyData(IWorkspaceContent content);

	public Object getElement(String id);

	//TODO read and save

	public void activate();

}
