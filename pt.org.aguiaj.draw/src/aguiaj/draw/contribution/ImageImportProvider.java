package aguiaj.draw.contribution;

import aguiaj.draw.IImage;

public interface ImageImportProvider<I extends IImage> {

	Class<I> getType();
	
	I create(IImage image); 
	
	// String getInstruction(String filePath)
}
