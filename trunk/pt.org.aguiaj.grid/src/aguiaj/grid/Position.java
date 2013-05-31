/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.grid;

import aguiaj.colors.Color;
import aguiaj.images.Image;

/**
 * Represents positions of a <code>Grid</code>.
 * 
 * @author Andre L. Santos
 */
public class Position {
	private static final Color DEFAULT_BACKGROUND = Color.WHITE;
			
	private Image icon;	
	private Color background;
	
	private final int row;
	private final int column;
	

	/**
	 * Creates an empty position with the given row/column.
	 */
	protected Position(int row, int column) {
		this.row = row;
		this.column = column;
		clear();
	}

	/**
	 * Row index
	 * @return an integer greater or equal to zero
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Column index
	 * @return an integer greater or equal to zero
	 */
	public int getColumn() {
		return column;
	}
	
	
	/**
	 * Is the position empty? (i.e. without an icon)
	 */
	public final boolean isEmpty() {
		return icon == null;
	}
	
	/**
	 * The icon at the position.
	 * @return The icon object, or <code>null</code> otherwise.
	 */
	public Image getIcon() {
		return icon;
	}

	/**
	 * The background color of the position.
	 */
	public final Color getBackground() {
		return background;
	}
	
	/**
	 * Sets the icon of the position.
	 * @param image Icon image
	 */
	public void setIcon(Image image) {
		icon = image;
	}
	
	/**
	 * Sets the background of the position.
	 * @param color The background color to be set
	 */
	public void setBackground(Color color) {		
		if(color == null)
			color = DEFAULT_BACKGROUND;
		else
			background = color;
	}
	
	/**
	 * Clears the position, removing the icon (if exists) and setting the background to white.
	 */
	public void clear() {
		icon = null;
		background = DEFAULT_BACKGROUND;
	}
	
	@Override
	public String toString() {
		return "(" + row + ", " + column + ")";
	}
}
