package aguiaj.iscte.contribution;

import pt.org.aguiaj.extensibility.ImportItemProvider;
import aguiaj.iscte.GrayscaleImage;
import aguiaj.iscte.ImageUtilsIscte;

public class GrayscaleImageProvider implements ImportItemProvider {

	

	
	@Override
	public String getInstruction(String filePath) {
		
		return ImageUtilsIscte.class.getSimpleName().concat(".loadGrayscaleImage(\"").concat(filePath).concat("\")");
	}

	
	

	@Override
	public Class<GrayscaleImage> getType() {
		return GrayscaleImage.class;
	}

	
}
