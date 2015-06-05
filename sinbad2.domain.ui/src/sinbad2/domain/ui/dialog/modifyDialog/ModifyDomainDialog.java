package sinbad2.domain.ui.dialog.modifyDialog;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.dialog.DomainDialog;

public class ModifyDomainDialog extends DomainDialog {
	
	protected Domain _newDomain;
	protected Domain _oldDomain;

	public ModifyDomainDialog() {
		super();
	}
	
	public void setDomain(Domain domain) {
		_oldDomain = domain;
		_newDomain = (Domain) domain.clone();
		_id = _newDomain.getId();
		_ids.remove(_id);
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
