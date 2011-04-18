/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Paul Klint - Paul.Klint@cwi.nl - CWI
*******************************************************************************/
package org.rascalmpl.library.vis.containers;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IList;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.library.vis.IFigureApplet;
import org.rascalmpl.library.vis.properties.PropertyManager;

/**
 * Spacing that can act as container.
 * 
 * @author paulk
 *
 */
public class Space extends Container {

	public Space(IFigureApplet fpa, PropertyManager properties, IConstructor inside,  IList childProps, IEvaluatorContext ctx) {
		super(fpa, properties, inside, childProps, ctx);
	}

	@Override
	void drawContainer() {
		return;
	}
	
	@Override
	String containerName(){
		return "space";
	}
}
