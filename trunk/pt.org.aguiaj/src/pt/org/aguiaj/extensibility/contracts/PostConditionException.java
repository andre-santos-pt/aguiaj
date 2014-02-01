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
package pt.org.aguiaj.extensibility.contracts;

public class PostConditionException extends AbstractContractException implements ContractException {

	private static final long serialVersionUID = 1L;

	public PostConditionException(Class<?> clazz, String method, String message) {
		super(clazz, method, message);
	}
	
}