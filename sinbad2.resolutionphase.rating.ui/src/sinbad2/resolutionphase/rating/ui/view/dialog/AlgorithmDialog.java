package sinbad2.resolutionphase.rating.ui.view.dialog;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.method.MethodsManager;
import sinbad2.resolutionphase.rating.ui.nls.Messages;

public class AlgorithmDialog extends Dialog {
	
	private final static String ALGORITHM = "# Require values #\nedNum = <edNum>\nedInt = <edInt>\nedLinUnb = <edLinUnb>\ntamEdLinLis = <tamEdLinLis>\nedLinList = <edLinList>\nedLin = <edLin>\n\n# Algorithm to select the suitable CWW methodology #\n 1: if (edLin[1].2T=true) and (tamEdLinLis=1) then\n 2:     return <1>\n 3: else if (edNum=true) or (edInt=true) then\n 4:     return <5>\n 5: else if (edLinUnb=true) then\n 6:     return <6>\n 7: else\n 8:     edLinListShortCard <-- short(edLinList,edLinList.card)\n 9:     i <-- 1\n10:     while i<tamEdLinLis do\n11:         if (edLinListShortCard.edLin[i].2T=false) then\n12:             return <2>\n13:         else if (edLinListShortCard[i+1].card != ((edLinListShortCard[i].card)-1)·2+1) then\n14:             return <4>\n15:         else\n16:             i <-- i+1\n17:         end if\n18:     end while\n19:     return <3>\n20: end if"; //$NON-NLS-1$

	private String _recommendedMethod;
	private Composite _container;
	private StyledText _algorithmText;
	private StyledText _algorithmInstantationText;
	
	private MethodsManager _methodsManager;

	public AlgorithmDialog(Shell parentShell) {
		super(parentShell);
		
		_methodsManager = MethodsManager.getInstance();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		_container = (Composite) super.createDialogArea(parent);
		GridLayout gl__container = new GridLayout(2, true);
		gl__container.marginRight = 10;
		gl__container.marginTop = 10;
		gl__container.marginLeft = 10;
		_container.setLayout(gl__container);

		Label algorithmLabel = new Label(_container, SWT.NONE);
		algorithmLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		algorithmLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		algorithmLabel.setText(Messages.AlgorithmDialog_Algorithm);

		Label algorithmInstantationLabel = new Label(_container, SWT.NONE);
		algorithmInstantationLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		algorithmInstantationLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		algorithmInstantationLabel.setText(Messages.AlgorithmDialog_Algorithm_instantation);

		_algorithmText = new StyledText(_container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gridData.verticalIndent = 0;
		_algorithmText.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_algorithmText.setLayoutData(gridData);

		_algorithmInstantationText = new StyledText(_container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gridData.verticalIndent = 0;
		_algorithmInstantationText.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_algorithmInstantationText.setLayoutData(gridData);

		setAlgorithm();
		setAlgorithmInstantation();
		_algorithmText.pack();
		_algorithmInstantationText.pack();
		return _container;
	}

	private void setAlgorithm() {

		String algorithm = ALGORITHM;

		algorithm = algorithm.replace("<1>", Messages.AlgorithmDialog_2_tuple_linguistic_computational_model); //$NON-NLS-1$
		algorithm = algorithm.replace("<2>", Messages.AlgorithmDialog_Fusion_approach_for_managing_multi_granular_linguistic_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<3>", Messages.AlgorithmDialog_Linguistic_hierarchies); //$NON-NLS-1$
		algorithm = algorithm.replace("<4>", Messages.AlgorithmDialog_Extended_linguistic_hierarchies); //$NON-NLS-1$
		algorithm = algorithm.replace("<5>", Messages.AlgorithmDialog_Fusion_approach_for_managing_heterogeneous_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<6>", Messages.AlgorithmDialog_Methodology_to_deal_with_unbalanced_linguistic_term_sets); //$NON-NLS-1$
		algorithm = algorithm.replace("<7>", Messages.AlgorithmDialog_Hesitant_fuzzy_2_tuple_linguistic_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<8>", Messages.AlgorithmDialog_Tecnique_for_order_of_preference_by_similarity_to_ideal_solution_TOPSIS); //$NON-NLS-1$

		Color BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

		Color[] textColors = new Color[] { BLACK, BLACK, BLACK, BLACK, BLACK,
				BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
				BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
				BLACK, BLACK, BLACK, BLACK, BLACK };

		StyleRange[] textRanges = new StyleRange[textColors.length];
		int lineStart = 0;
		int lineLength = 0;
		String lines[] = algorithm.split("\n"); //$NON-NLS-1$
		for(int i = 0; i < textColors.length; i++) {
			lineLength = lines[i].length();
			textRanges[i] = new StyleRange(lineStart, lineLength,
					textColors[i], null);
			lineStart += lineLength + 1;
		}

		_algorithmText.setText(algorithm);

		if(textRanges != null) {

			_algorithmText.setStyleRanges(textRanges);

			int auxPos = algorithm.indexOf(" Require values "); //$NON-NLS-1$
			_algorithmText.setStyleRange(new StyleRange(auxPos - 1, "# Require values #".length(), null, null, SWT.BOLD)); //$NON-NLS-1$

			auxPos = algorithm.indexOf(" Algorithm to select the suitable CWW methodology "); //$NON-NLS-1$
			_algorithmText.setStyleRange(new StyleRange(auxPos - 1, "# Algorithm to select the suitable CWW methodology #".length(), null, null, SWT.BOLD)); //$NON-NLS-1$

			// if
			int initPos = 0;
			int newPos = 0;
			while(algorithm.indexOf(" if", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" if", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 3, null, null, SWT.BOLD));
				initPos = newPos + 3;
			}

			// else
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" else", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" else", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 5, null, null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// end
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" end", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" end", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 4, null, null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// while
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" while", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" while", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 6, null, null, SWT.BOLD));
				initPos = newPos + 6;
			}

			// return
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" return", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" return", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 7, null, null, SWT.BOLD));
				initPos = newPos + 7;
			}

			// then
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" then", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" then", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 5, null, null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// do
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" do", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" do", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 3, null, null, SWT.BOLD));
				initPos = newPos + 3;
			}

			// and
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" and ", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" and ", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 5, null, null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// or
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf(" or ", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" or ", initPos); //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 4, null, null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// true
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf("=true", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf("=true", initPos) + 1; //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 4, null, null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// false
			initPos = 0;
			newPos = 0;
			while(algorithm.indexOf("=false", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf("=false", initPos) + 1; //$NON-NLS-1$
				_algorithmText.setStyleRange(new StyleRange(newPos, 5, null, null, SWT.BOLD));
				initPos = newPos + 4;
			}
		}
	}

	private void setAlgorithmInstantation() {

		String algorithm = ALGORITHM;

		boolean bestConditionsNumeric = _methodsManager.getBestConditionsNumeric();
		boolean edInt = _methodsManager.getBestConditionsNumeric();
		boolean bestConditionsUnbalanced = _methodsManager.getBestConditionsUnbalanced();
		int[] cardinalitiesFuzzySet = _methodsManager.getCardinalitiesFuzzySet();
		int tamEdLinList = cardinalitiesFuzzySet.length;
		Map<Integer, Boolean> edLin = _methodsManager.getBestConditionsLinguistic();

		String edLinValue = "{"; //$NON-NLS-1$

		if(cardinalitiesFuzzySet != null) {
			if(cardinalitiesFuzzySet.length > 0) {
				for(int i = 0; i < cardinalitiesFuzzySet.length; i++) {
					edLinValue += "(" + Integer.toString(cardinalitiesFuzzySet[i]) + "," + edLin.get(cardinalitiesFuzzySet[i]) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if((i + 1) < cardinalitiesFuzzySet.length) {
						edLinValue += ","; //$NON-NLS-1$
					}
				}
			}
		}
		edLinValue += "}"; //$NON-NLS-1$
		
		String edLinListValue = "["; //$NON-NLS-1$
		if(cardinalitiesFuzzySet != null) {
			if(cardinalitiesFuzzySet.length > 0) {
				for(int i = 0; i < cardinalitiesFuzzySet.length; i++) {
					edLinListValue += cardinalitiesFuzzySet[i];
					if((i + 1) < cardinalitiesFuzzySet.length) {
						edLinListValue += ","; //$NON-NLS-1$
					}

				}
			}
		}
		edLinListValue += "]"; //$NON-NLS-1$

		algorithm = algorithm.replace("<edNum>", Boolean.toString(bestConditionsNumeric)); //$NON-NLS-1$
		algorithm = algorithm.replace("<edInt>", Boolean.toString(edInt)); //$NON-NLS-1$
		algorithm = algorithm.replace("<edLinUnb>", Boolean.toString(bestConditionsUnbalanced)); //$NON-NLS-1$
		algorithm = algorithm.replace("<tamEdLinLis>", Integer.toString(tamEdLinList)); //$NON-NLS-1$
		algorithm = algorithm.replace("<edLinList>", edLinListValue); //$NON-NLS-1$
		algorithm = algorithm.replace("<edLin>", edLinValue); //$NON-NLS-1$

		algorithm = algorithm.replace("<1>", Messages.AlgorithmDialog_2_tuple_linguistic_computational_model); //$NON-NLS-1$
		algorithm = algorithm.replace("<2>", Messages.AlgorithmDialog_Fusion_approach_for_managing_multi_granular_linguistic_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<3>", Messages.AlgorithmDialog_Linguistic_hierarchies); //$NON-NLS-1$
		algorithm = algorithm.replace("<4>", Messages.AlgorithmDialog_Extended_linguistic_hierarchies); //$NON-NLS-1$
		algorithm = algorithm.replace("<5>", Messages.AlgorithmDialog_Fusion_approach_for_managing_heterogeneous_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<6>", Messages.AlgorithmDialog_Methodology_to_deal_with_unbalanced_linguistic_term_sets); //$NON-NLS-1$
		algorithm = algorithm.replace("<7>", Messages.AlgorithmDialog_Hesitant_fuzzy_2_tuple_linguistic_information); //$NON-NLS-1$
		algorithm = algorithm.replace("<8>", Messages.AlgorithmDialog_Tecnique_for_order_of_preference_by_similarity_to_ideal_solution_TOPSIS); //$NON-NLS-1$

		Color DARK_BLUE = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		Color GREEN = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
		Color MAGENTA = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
		Color RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		Color BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

		Color[] textColors = new Color[] { BLACK, MAGENTA, MAGENTA, MAGENTA, MAGENTA,
				MAGENTA, MAGENTA, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
				BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
				BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK };

		_recommendedMethod = _methodsManager.getRecommendedMethod();

		int start = 8;
		if(Messages.AlgorithmDialog_2_tuple_linguistic_computational_model.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 2] = GREEN;
		} else if(Messages.AlgorithmDialog_Fusion_approach_for_managing_multi_granular_linguistic_information.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = textColors[start + 5] = RED;
			textColors[start + 7] = textColors[start + 8] = textColors[start + 9] = textColors[start + 10] = textColors[start + 11] = textColors[start + 12] = GREEN;
		} else if(Messages.AlgorithmDialog_Linguistic_hierarchies.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = textColors[start + 5] = textColors[start + 11] = textColors[start + 13] = RED;
			textColors[start + 7] = textColors[start + 8] = textColors[start + 9] = textColors[start + 10] = textColors[start + 15] = textColors[start + 16] = textColors[start + 17] = textColors[start + 18] = textColors[start + 19] = GREEN;
		} else if(Messages.AlgorithmDialog_Extended_linguistic_hierarchies.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = textColors[start + 5] = textColors[start + 11] = RED;
			textColors[start + 7] = textColors[start + 8] = textColors[start + 9] = textColors[start + 10] = textColors[start + 13] = textColors[start + 14] = GREEN;
		} else if(Messages.AlgorithmDialog_Fusion_approach_for_managing_heterogeneous_information.equals(_recommendedMethod)) {
			textColors[start + 1] = RED;
			textColors[start + 3] = textColors[start + 4] = GREEN;
		} else if (Messages.AlgorithmDialog_Methodology_to_deal_with_unbalanced_linguistic_term_sets.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = RED;
			textColors[start + 5] = textColors[start + 6] = GREEN;
		} else if (Messages.AlgorithmDialog_Hesitant_fuzzy_2_tuple_linguistic_information.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = RED;
			textColors[start + 5] = textColors[start + 6] = GREEN;
		}  else if (Messages.AlgorithmDialog_Tecnique_for_order_of_preference_by_similarity_to_ideal_solution_TOPSIS.equals(_recommendedMethod)) {
			textColors[start + 1] = textColors[start + 3] = textColors[start + 5] = RED;
			textColors[start + 7] = textColors[start + 8] = textColors[start + 9] = textColors[start + 10] = textColors[start + 11] = textColors[start + 12] = GREEN;
		}

		StyleRange[] textRanges = new StyleRange[textColors.length];
		int lineStart = 0;
		int lineLength = 0;
		String lines[] = algorithm.split("\n"); //$NON-NLS-1$
		for(int i = 0; i < textColors.length; i++) {
			lineLength = lines[i].length();
			textRanges[i] = new StyleRange(lineStart, lineLength,
					textColors[i], null);
			lineStart += lineLength + 1;
		}

		_algorithmInstantationText.setText(algorithm);

		if(textRanges != null) {

			_algorithmInstantationText.setStyleRanges(textRanges);
			_algorithmInstantationText.setStyleRange(new StyleRange(algorithm.indexOf(_recommendedMethod), _recommendedMethod.length(), DARK_BLUE, null, SWT.BOLD));

			// Comments
			int auxPos = algorithm.indexOf(" Require values "); //$NON-NLS-1$
			_algorithmInstantationText.setStyleRange(new StyleRange(auxPos - 1, "# Require values #".length(), _algorithmInstantationText.getStyleRangeAtOffset(auxPos).foreground, null,SWT.BOLD)); //$NON-NLS-1$

			auxPos = algorithm.indexOf(" Algorithm to select the suitable CWW methodology "); //$NON-NLS-1$
			_algorithmInstantationText.setStyleRange(new StyleRange(auxPos - 1, "# Algorithm to select the suitable CWW methodology #".length(), _algorithmInstantationText.getStyleRangeAtOffset(auxPos).foreground, null,SWT.BOLD)); //$NON-NLS-1$

			// if
			int initPos = 0;
			int newPos = 0;
			while(algorithm.indexOf(" if", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" if", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos, 3, _algorithmInstantationText.getStyleRangeAtOffset(newPos).foreground, null, SWT.BOLD));
				initPos = newPos + 3;
			}

			// else
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" else", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" else", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos, 5, _algorithmInstantationText.getStyleRangeAtOffset(newPos).foreground, null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// end
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" end", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" end", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						4, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// while
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" while", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" while", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						6, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 6;
			}

			// return
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" return", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" return", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						7, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 7;
			}

			// then
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" then", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" then", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						5, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// do
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" do", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" do", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						3, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 3;
			}

			// and
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" and ", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" and ", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						5, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 5;
			}

			// or
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf(" or ", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf(" or ", initPos); //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						4, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// true
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf("=true", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf("=true", initPos) + 1; //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						4, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 4;
			}

			// false
			initPos = 0;
			newPos = 0;
			while (algorithm.indexOf("=false", initPos) != -1) { //$NON-NLS-1$
				newPos = algorithm.indexOf("=false", initPos) + 1; //$NON-NLS-1$
				_algorithmInstantationText.setStyleRange(new StyleRange(newPos,
						5, _algorithmInstantationText
								.getStyleRangeAtOffset(newPos).foreground,
						null, SWT.BOLD));
				initPos = newPos + 4;
			}
		}
	}
	
	 @Override
	   protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);

	    Button ok = getButton(IDialogConstants.OK_ID);
	    setButtonLayoutData(ok);

	    Button cancel = getButton(IDialogConstants.CANCEL_ID);
	    cancel.setVisible(false);
	    setButtonLayoutData(cancel);
	 }

}
