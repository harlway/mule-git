/*
 * Copyright 2005 SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * ------------------------------------------------------------------------------------------------------
 * $Header$
 * $Revision$
 * $Date$
 */
package org.mule.jbi.servicedesc;

import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

/**
 * 
 * @author <a href="mailto:gnt@codehaus.org">Guillaume Nodet</a>
 */
public class DynamicEndpointImpl extends AbstractServiceEndpoint {

	private ServiceEndpoint ref;
	
	public DynamicEndpointImpl(String component, ServiceEndpoint ref) {
		this.component = component;
		this.ref = ref;
	}

	public String getEndpointName() {
		return ref.getEndpointName();
	}

	public QName[] getInterfaces() {
		return ref.getInterfaces();
	}

	public QName getServiceName() {
		return ref.getServiceName();
	}
	
}
