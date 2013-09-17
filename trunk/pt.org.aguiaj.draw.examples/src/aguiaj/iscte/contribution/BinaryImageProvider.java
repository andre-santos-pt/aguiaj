package aguiaj.iscte.contribution;

import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.IImage;
import aguiaj.draw.contribution.ImageImportProvider;
import aguiaj.iscte.BinaryImage;

public class BinaryImageProvider implements ImageImportProvider<BinaryImage> {

	@Override
	public Class<BinaryImage> getType() {
		return BinaryImage.class;
	}

	private static int getLuminance(IColor color) {
		return (int) Math.round(0.3*color.getR() + 0.59*color.getG() + 0.11*color.getB());
	}
	
	@Override
	public BinaryImage create(IImage image) {
		IDimension dim = image.getDimension();
		BinaryImage img = new BinaryImage(dim.getWidth(), dim.getHeight());
		for(int i = 0; i < dim.getWidth(); i++) {
			for(int j = 0; j < dim.getHeight(); j++) {
				if(getLuminance(image.getColor(i, j)) < 128)
					img.setBlack(i, j);
			}
		}
		return img;
	}

}
