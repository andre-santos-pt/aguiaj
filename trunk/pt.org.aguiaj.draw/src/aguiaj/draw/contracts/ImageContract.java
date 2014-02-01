/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package aguiaj.draw.contracts;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;
import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.IImage;

public class ImageContract implements IImage, ContractDecorator<IImage>{

	private final IImage image;
	
	private int constWidth = -1;
	private int constHeight = -1;
	
	public ImageContract(IImage image) {
		this.image = image;
	}
	
	@Override
	public IImage getWrappedObject() {
		return image;
	}

	@Override
	public IDimension getDimension() {
		IDimension dim = image.getDimension();
		if(dim == null)
			throw new PostConditionException(image.getClass(), "getDimension", "Dimension cannot be null");
		
		int w = dim.getWidth();
		validateWidth(w);
		
		if(constWidth == -1)
			constWidth = w;
		
		int h = dim.getHeight();
		validateHeight(h);
		
		if(constHeight == -1)
			constHeight = h;
		
		return dim;
	}
	

	@Override
	public IColor getColor(int x, int y) {
		IDimension dim = getDimension();
		
		if(!DimensionUtil.isValidPoint(dim, x, y))
			throw new PreConditionException(image.getClass(), "getColor", "Invalid coordinate (" + x + ", " + y + ")");
		
		IColor color = null;
		try {
			color = image.getColor(x, y);
		}
		catch(RuntimeException e) {
			throw new RuntimeException("Problems with getColor(...)", e);
		}
		
		if(color == null)
			throw new PostConditionException(image.getClass(), "getColor", "The color of a pixel cannot be null - (" + x + ", " + y + ")"); //, image.getClass().getName(), "getColor(int, int)");
		
		return color;
	}

	@Override
	public void checkInvariant() throws InvariantException {
	
		IDimension dim = getDimension();
		
		int w = dim.getWidth();
		validateWidth(w);
		int h = dim.getHeight();
		validateHeight(h);
		
//		if(constWidth != -1 && constWidth != w)
//			throw new InvariantException("Image width must be constant");
//	
//		if(constHeight != -1 && constHeight != h)
//			throw new PostConditionException(image.getClass(), "getDimension", "Image height must be constant");
	}

	private  void validateWidth(int w) {
		if(w < 1)
			throw new PostConditionException(image.getClass(), "getDimension", "Width must be positive");
	}

	private  void validateHeight(int h) {
		if(h < 1)
			throw new PostConditionException(image.getClass(), "getDimension", "Height must be positive");
	}

	

}
