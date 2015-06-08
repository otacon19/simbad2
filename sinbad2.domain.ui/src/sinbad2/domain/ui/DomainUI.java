package sinbad2.domain.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DomainUI {
	
	private String _domain;
	private Image _icon;
	private DomainUIRegistryExtension _registry;
	
	public DomainUI() {
		_domain = null;
		_icon = null;
		_registry = null;
	}
	
	public void setDomain(String domain) {
		_domain = domain;
	}
	
	public String getDomain() {
		return _domain;
	}
	
	public void setIcon(String plugin, String path) {
		_icon = AbstractUIPlugin.imageDescriptorFromPlugin(plugin, path).createImage();
	}
	
	public Image getIcon() {
		return _icon;
	}
	
	public void setRegistry(DomainUIRegistryExtension registry) {
		_registry = registry;
	}
	
	public DomainUIRegistryExtension getRegistry() {
		return _registry;
	}

}
