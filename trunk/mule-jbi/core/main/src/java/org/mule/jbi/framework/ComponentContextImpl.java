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
package org.mule.jbi.framework;

import org.mule.jbi.Endpoints;
import org.mule.jbi.registry.JbiRegistryComponent;
import org.mule.jbi.servicedesc.AbstractServiceEndpoint;
import org.mule.jbi.servicedesc.DynamicEndpointImpl;
import org.mule.jbi.servicedesc.ExternalEndpointImpl;
import org.mule.jbi.servicedesc.InternalEndpointImpl;
import org.mule.management.ManagementContext;
import org.mule.registry.Registry;
import org.mule.registry.RegistryComponent;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import javax.jbi.JBIException;
import javax.jbi.component.Component;
import javax.jbi.component.ComponentContext;
import javax.jbi.management.MBeanNames;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import java.util.MissingResourceException;
import java.util.logging.Logger;

/**
 * 
 * @author <a href="mailto:gnt@codehaus.org">Guillaume Nodet</a>
 */
public class ComponentContextImpl implements ComponentContext, MBeanNames {

	private ManagementContext context;
	private JbiRegistryComponent component;
	private Registry registry;
	private Endpoints endpoints;
	
	public ComponentContextImpl(ManagementContext context, JbiRegistryComponent component, Endpoints endpoints) {
		this.context = context;
		this.component = component;
		this.registry = context.getRegistry();
		this.endpoints = endpoints;
	}
	
	public ServiceEndpoint activateEndpoint(QName serviceName, String endpointName) throws JBIException {
		InternalEndpointImpl se = new InternalEndpointImpl();
		se.setServiceName(serviceName);
		se.setEndpointName(endpointName);
		se.setComponent(this.component.getName());
		se.setActive(true);
		this.endpoints.registerInternalEndpoint(se);
		return se;
	}

	public void deactivateEndpoint(ServiceEndpoint endpoint) throws JBIException {
		this.endpoints.unregisterInternalEndpoint(endpoint);
	}

	public void registerExternalEndpoint(ServiceEndpoint externalEndpoint) throws JBIException {
		ExternalEndpointImpl se = new ExternalEndpointImpl();
		se.setEndpoint(externalEndpoint);
		se.setComponent(this.component.getName());
		se.setActive(true);
		this.endpoints.registerExternalEndpoint(se);
	}

	public void deregisterExternalEndpoint(ServiceEndpoint externalEndpoint) throws JBIException {
		this.endpoints.unregisterExternalEndpoint(externalEndpoint);
	}

	public ServiceEndpoint resolveEndpointReference(DocumentFragment epr) {
		RegistryComponent[] components = this.registry.getComponents();
		for (int i = 0; i < components.length; i++) {
			ServiceEndpoint se = ((Component)components[i].getComponent()).resolveEndpointReference(epr);
			if (se != null) {
				return new DynamicEndpointImpl(components[i].getName(), se);
			}
		}
		return null;
	}

	public String getComponentName() {
		return this.component.getName();
	}

	public DeliveryChannel getDeliveryChannel() throws MessagingException {
		return component.getChannel();
	}

	public ServiceEndpoint getEndpoint(QName service, String name) {
		return this.endpoints.getEndpoint(service, name);
	}

	public Document getEndpointDescriptor(ServiceEndpoint endpoint) throws JBIException {
		// Translate endpoint
		ServiceEndpoint se = this.endpoints.getEndpoint(endpoint.getServiceName(), endpoint.getEndpointName());
		// If endpoint is registered
		if (se instanceof AbstractServiceEndpoint) {
			// Query for the component
			String name = ((AbstractServiceEndpoint) se).getComponent();
			RegistryComponent component = this.registry.getComponent(name);
			if (component != null) {
				// Delegate to the component
				return ((Component)component.getComponent()).getServiceDescription(endpoint);
			}
		}
		return null;
	}

	public ServiceEndpoint[] getEndpoints(QName interfaceName) {
		return this.endpoints.getInternalEndpoints(interfaceName);
	}

	public ServiceEndpoint[] getEndpointsForService(QName serviceName) {
		return this.endpoints.getInternalEndpointsForService(serviceName);
	}

	public ServiceEndpoint[] getExternalEndpoints(QName interfaceName) {
		return this.endpoints.getExternalEndpoints(interfaceName);
	}

	public ServiceEndpoint[] getExternalEndpointsForService(QName serviceName) {
		return this.endpoints.getExternalEndpointsForService(serviceName);
	}

	public String getInstallRoot() {
		return this.component.getInstallRoot();
	}

	public Logger getLogger(String suffix, String resourceBundleName) throws MissingResourceException, JBIException {
		StringBuffer sb = new StringBuffer();
		sb.append("org.mule.jbi.components.");
		sb.append(this.component.getName());
		if (suffix != null && suffix.length() > 0) {
			sb.append(".");
			sb.append(suffix);
		}
		return Logger.getLogger(sb.toString(), resourceBundleName);
	}

	public MBeanNames getMBeanNames() {
		return this;
	}

	public MBeanServer getMBeanServer() {
		return this.context.getMBeanServer();
	}

	public InitialContext getNamingContext() {
		return this.context.getNamingContext();
	}

	public Object getTransactionManager() {
		return this.context.getTransactionManager();
	}

	public String getWorkspaceRoot() {
		return this.component.getWorkspaceRoot();
	}

	public ObjectName createCustomComponentMBeanName(String customName) {
		return context.createMBeanName(this.component.getName(), "custom", customName);
	}

	public String getJmxDomainName() {
		return this.context.getJmxDomainName();
	}

}
