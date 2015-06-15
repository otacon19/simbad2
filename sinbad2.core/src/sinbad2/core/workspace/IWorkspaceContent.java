package sinbad2.core.workspace;

import java.io.IOException;

public interface IWorkspaceContent {
	public void clear();

	public IWorkspaceContent copyStructure();

	public IWorkspaceContent copyData();

	public void copyData(IWorkspaceContent content);

	public Object getElement(String id);

	public void save(String fileName) throws IOException, 
		WorkspaceContentPersistenceException;
	
	public IWorkspaceContent read(String fileName) throws IOException,
		WorkspaceContentPersistenceException;

	public void activate();

}
