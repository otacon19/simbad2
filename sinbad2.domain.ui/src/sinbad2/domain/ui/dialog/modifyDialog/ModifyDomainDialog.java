package sinbad2.domain.ui.dialog.modifyDialog;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.DomainDialog;

public class ModifyDomainDialog extends DomainDialog {
	
	protected Domain _newDomain;
	private Domain _oldDomain;

	public ModifyDomainDialog() {
		super();
	}
	
	public void setDomain(Domain domain) {
		//TODO no le veo sentido a esta funcion (cambiada)
		//_oldDomain = domain;
		_newDomain = (Domain) domain.clone();
		_id = domain.getId();
		//_ids.remove(_id);
		_ids.remove(_oldDomain.getId());
	}
	
	public void setNewDomain(Domain newDomain) {
		_newDomain = newDomain;
	}
	
	public void setOldDomain(Domain oldDomain) {
		_oldDomain = oldDomain;
	}
	
	public Domain getNewDomain() {
		return _newDomain;
	}
	
	public Domain getOldDomain() {
		return _oldDomain;
	}

}
