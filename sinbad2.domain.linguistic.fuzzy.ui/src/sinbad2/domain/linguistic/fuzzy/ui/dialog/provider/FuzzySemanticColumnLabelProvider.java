package sinbad2.domain.linguistic.fuzzy.ui.dialog.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;

public class FuzzySemanticColumnLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		LabelLinguisticDomain label = (LabelLinguisticDomain) element;
		return label.getSemantic().toString();
	}

}
