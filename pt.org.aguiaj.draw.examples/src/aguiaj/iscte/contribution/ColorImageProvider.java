package aguiaj.iscte.contribution;

import pt.org.aguiaj.extensibility.ImportItemProvider;
import aguiaj.iscte.ColorImage;
import aguiaj.iscte.ImageUtils;

public class ColorImageProvider implements ImportItemProvider {

	@Override
	public String getInstruction(String filePath) {
		return ImageUtils.class.getSimpleName().concat(".loadColorImage(\"").concat(filePath).concat("\")");
	}

	@Override
	public Class<ColorImage> getType() {
		return ColorImage.class;
	}
	
}
