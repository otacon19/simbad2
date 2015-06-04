package sinbad2.domain.ui.view.domain;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class DomainView extends ViewPart implements IDisplayDomainChangeListener {
	
	public static final String ID = "flintstones.domain.ui.view.domain";
	
	private Composite _container;
	private DomainChart _chart = null;
	private Domain _domain = null;
	//TODO ranking?
	
	@Override
	public void createPartControl(Composite parent) {
		_container = parent;
		_container.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_container.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				refreshDomainChart();
			}
		});
		
		DomainViewManager.getInstance().registerDisplayDomainChangeListener(this);
		
	}
	
	public void refreshDomainChart() {
		
		if(_chart != null) {
			for(Control control: _container.getChildren()) {
				if(!control.isDisposed()) {
					control.dispose();
				}
			}
		}
		
		if(_domain != null) {
			Point size = _container.getSize();
			DomainUIsManager manager = DomainUIsManager.getInstance();
			_chart = manager.newDomainChart(_domain);
			_chart.initialize(_domain, _container, size.x, size.y, SWT.NONE);
			//TODO preguntar sobre el displayRanking
			_container.layout();
		}
		
	}
	
	@Override
	public void dispose() {
		DomainViewManager.getInstance().unregisterDisplayDomainChangeListener(this);
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		_container.setFocus();
		
	}
	
	@Override
	public void displayDomainChangeListener(Domain domain, Object ranking) {
		_domain = domain;
		
		refreshDomainChart();
		
	}

}
