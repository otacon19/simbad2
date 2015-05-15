package sinbad2.workspace;

public interface IWorkspaceContent {
	
	public void clear();
	
	public IWorkspaceContent copyStructure();
	
	public IWorkspaceContent copyData();
	
	public void copyData(IWorkspaceContent content);
	
	public Object getElement(String id);
	
	//TODO save and read
	
	public void activate();

}
