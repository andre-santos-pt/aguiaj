package pt.iscte.dcti.aguiaj.editor;

import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


public class Configuration extends SourceViewerConfiguration {
	private JavaCodeScanner tagScanner;
	//	private XMLScanner scanner;

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				JavaCodePartitionScanner.JAVA_COMMENT};
		//				JavaCodePartitionScanner.JAVA_CODE };
	}


	//	protected XMLScanner getXMLScanner() {
	//		if (scanner == null) {
	//			scanner = new XMLScanner(colorManager);
	//			scanner.setDefaultReturnToken(
	//				new Token(
	//					new TextAttribute(
	//						colorManager.getColor(EditorColorConstants.DEFAULT))));
	//		}
	//		return scanner;
	//	}

	protected JavaCodeScanner getTagScanner() {
		if (tagScanner == null) {
			tagScanner = new JavaCodeScanner();
			tagScanner.setDefaultReturnToken(Common.createToken(TokenColor.BLACK, false));
		}
		return tagScanner;
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {		
		return new IAnnotationHover() {

			@Override
			public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
				IDocument doc = sourceViewer.getDocument();
				String ret = "";
				for(Iterator it = sourceViewer.getAnnotationModel().getAnnotationIterator(); it.hasNext(); ) {
					Annotation ann = (Annotation) it.next();
					int offset = sourceViewer.getAnnotationModel().getPosition(ann).offset;					
					try {
						//	ret += offset + " .. " + doc.getLineOffset(lineNumber) + " .. " + doc.getLineOffset(lineNumber+1);
						if(offset >= doc.getLineOffset(lineNumber) && 
								(lineNumber == doc.getNumberOfLines()-1 || offset < doc.getLineOffset(lineNumber+1))) {
							ret += ann.getText();
							//							if(it.hasNext())
							//								ret += "\n";
							break;
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}


				}
				return ret;
			}
		};
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new JavaCommentsPartitionScanner());
		reconciler.setDamager(dr, JavaCodePartitionScanner.JAVA_COMMENT);
		reconciler.setRepairer(dr, JavaCodePartitionScanner.JAVA_COMMENT);

		DefaultDamagerRepairer dr2 = new DefaultDamagerRepairer(new JavaCodeScanner());
		reconciler.setDamager(dr2, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr2, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(new TextAttribute(TokenColor.COMMENT.color));
		reconciler.setDamager(ndr, JavaCodePartitionScanner.JAVA_COMMENT);
		reconciler.setRepairer(ndr, JavaCodePartitionScanner.JAVA_COMMENT);

		return reconciler;
	}

}