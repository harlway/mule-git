/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ide.prototype.palette;

import java.util.Date;

import org.eclipse.emf.ecore.EObject;
import org.mule.ide.prototype.mulemodel.AbstractComponent;
import org.mule.ide.prototype.mulemodel.GenericComponent;
import org.mule.ide.prototype.mulemodel.BridgeComponent;
import org.mule.ide.prototype.mulemodel.MuleConfig;
import org.mule.ide.prototype.mulemodel.MuleFactory;

public class ComponentItem extends PaletteItem {

	public ComponentItem(String name) {
		super(name);
	}

	public boolean mayDropOn(EObject obj) {
		while (obj != null && !(obj instanceof MuleConfig)) obj = obj.eContainer();
		
		return obj instanceof MuleConfig;
	}
	
	public void performDropOn(EObject obj, MuleFactory factory) {
		while (obj != null && !(obj instanceof MuleConfig)) obj = obj.eContainer();
		assert obj instanceof MuleConfig;
		if (obj instanceof MuleConfig) {
			MuleConfig cfg = (MuleConfig) obj;
			AbstractComponent component = null;
			
			System.out.println("Component type: " + this.type);
			
			if (this.type.equals("bridge")) {
				component = factory.createBridgeComponent();
			} else {			
				component = factory.createGenericComponent();
			}
			
			component.setName(this.getName());
			component.setImplementation(this.getClassName()); // Not supported in XML
			component.setComment("Dropped from the Mule palette on " + new Date());
			
			cfg.getComponents().add(component);
		}
	}
}
