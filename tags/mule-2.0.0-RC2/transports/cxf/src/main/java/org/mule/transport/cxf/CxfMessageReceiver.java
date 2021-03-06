/*
 * $Id: XFireMessageReceiver.java 6106 2007-04-20 13:28:24Z Lajos $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf;

import org.mule.api.MuleException;
import org.mule.api.endpoint.Endpoint;
import org.mule.api.lifecycle.Callable;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.service.Service;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractMessageReceiver;
import org.mule.transport.cxf.i18n.CxfMessages;
import org.mule.transport.cxf.support.ProviderService;
import org.mule.util.ClassUtils;
import org.mule.util.StringUtils;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.BooleanUtils;
import org.apache.cxf.Bus;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.configuration.Configurer;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.factory.AbstractServiceConfiguration;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;

/**
 * Create a CXF service. All messages for the service will be sent to the Mule bus a
 * la the MuleInvoker.
 */
public class CxfMessageReceiver extends AbstractMessageReceiver
{

    protected CxfConnector connector;
    private Server server;
    private boolean bridge;

    public CxfMessageReceiver(Connector Connector, Service service, Endpoint Endpoint)
        throws CreateException
    {
        super(Connector, service, Endpoint);
        connector = (CxfConnector) Connector;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doInitialise() throws InitialisationException
    {
        try
        {
            
            Map endpointProps = getEndpoint().getProperties();
            String wsdlUrl = (String) endpointProps.get(CxfConstants.WSDL_LOCATION);
            String databinding = (String) endpointProps.get(CxfConstants.DATA_BINDING);
            String bindingId = (String) endpointProps.get(CxfConstants.BINDING_ID);
            String frontend = (String) endpointProps.get(CxfConstants.FRONTEND);
            String bridge = (String) endpointProps.get(CxfConstants.BRIDGE);
            String serviceClassName = (String) endpointProps.get(CxfConstants.SERVICE_CLASS);
            List<AbstractFeature> features = (List<AbstractFeature>) endpointProps.get(CxfConstants.FEATURES);
            
            Class<?> svcCls = null;
            if (!StringUtils.isEmpty(serviceClassName)) {
                svcCls = ClassUtils.loadClass(serviceClassName, getClass());
            } else {
                svcCls = getInterface();
            }
            
            if (BooleanUtils.toBoolean(bridge))
            {
                svcCls = ProviderService.class;
                frontend = "jaxws";
            }

            if (StringUtils.isEmpty(frontend))
            {
                frontend = connector.getDefaultFrontend();
            }

            ServerFactoryBean sfb = null;
            if (CxfConstants.SIMPLE_FRONTEND.equals(frontend))
            {
                sfb = new ServerFactoryBean();
                sfb.setDataBinding(new AegisDatabinding());
            }
            else if (CxfConstants.JAX_WS_FRONTEND.equals(frontend))
            {
                sfb = new JaxWsServerFactoryBean();
            }
            else
            {
                throw new CreateException(CxfMessages.invalidFrontend(frontend), this);
            }

            // The binding - i.e. SOAP, XML, HTTP Binding, etc
            if (bindingId != null)
            {
                sfb.setBindingId(bindingId);
            }
            
            if (features != null) {
                sfb.setFeatures(features);
            }

            // Aegis, JAXB, other?
            if (databinding != null)
            {
                Class<?> c = ClassLoaderUtils.loadClass(databinding, getClass());
                sfb.setDataBinding((DataBinding) c.newInstance());
            }

            sfb.setServiceClass(svcCls);
            sfb.setAddress(getAddressWithoutQuery());

            if (wsdlUrl != null)
            {
                sfb.setWsdlURL(wsdlUrl);
            }

            ReflectionServiceFactoryBean svcFac = sfb.getServiceFactory();

            addIgnoredMethods(svcFac, Callable.class.getName());
            addIgnoredMethods(svcFac, Initialisable.class.getName());
            addIgnoredMethods(svcFac, Disposable.class.getName());

            String name = (String) endpointProps.get(CxfConstants.NAME);
            // check if there is the namespace property on the service
            String namespace = (String) endpointProps.get(CxfConstants.NAMESPACE);

            // HACK because CXF expects a QName for the service
            initServiceName(svcCls, name, namespace, svcFac);

            boolean sync = endpoint.isSynchronous();
            // default to synchronous if using http
            if (endpoint.getEndpointURI().getScheme().startsWith("http")
                || endpoint.getEndpointURI().getScheme().startsWith("servlet"))
            {
                sync = true;
            }

            sfb.setInvoker(new MuleInvoker(this, sync));
            sfb.setStart(false);

            Bus bus = connector.getCxfBus();
            sfb.setBus(bus);

            Configurer configurer = bus.getExtension(Configurer.class);
            if (null != configurer)
            {
                configurer.configureBean(sfb.getServiceFactory().getEndpointName().toString(), sfb);
            }

            server = sfb.create();
        }
        catch (MuleException e)
        {
            throw new InitialisationException(e, this);
        }
        catch (ClassNotFoundException e)
        {
            // will be thrown in the case that the ClassUtils.loadClass() does
            // not find the class to load
            throw new InitialisationException(e, this);
        }
        catch (Exception e)
        {
            throw new InitialisationException(e, this);
        }
    }

    private String getAddressWithoutQuery()
    {
        String a = getEndpointURI().getAddress();
        int idx = a.lastIndexOf('?');
        if (idx > -1) {
            a = a.substring(0, idx);
        }
        return a;
    }

    /**
     * Gross hack to support getting the service namespace from CXF if one wasn't
     * supplied.
     */
    private void initServiceName(Class<?> exposedInterface,
                                 String name,
                                 String namespace,
                                 ReflectionServiceFactoryBean svcFac)
    {
        svcFac.setServiceClass(exposedInterface);
        for (AbstractServiceConfiguration c : svcFac.getServiceConfigurations())
        {
            c.setServiceFactory(svcFac);
        }

        if (name != null && namespace == null)
        {
            namespace = svcFac.getServiceQName().getNamespaceURI();
        }
        else if (name == null && namespace != null)
        {
            name = svcFac.getServiceQName().getLocalPart();
        }

        if (name != null)
        {
            svcFac.setServiceName(new QName(namespace, name));
        }
    }

    public void addIgnoredMethods(ReflectionServiceFactoryBean svcFac, String className)
    {
        try
        {
            Class<?> c = ClassUtils.loadClass(className, getClass());
            for (int i = 0; i < c.getMethods().length; i++)
            {
                svcFac.getIgnoredMethods().add(c.getMethods()[i]);
            }
        }
        catch (ClassNotFoundException e)
        {
            // can be ignored.
        }
    }

    private Class<?> getInterface() throws MuleException, ClassNotFoundException
    {
        try
        {
            return service.getServiceFactory().getOrCreate().getClass();
        }
        catch (Exception e)
        {
            throw new CreateException(e, this);
        }
    }

    protected void doDispose()
    {
        // template method
    }

    public void doConnect() throws Exception
    {
        // Start the CXF Server
        server.start();
        connector.registerReceiverWithMuleService(this, endpoint.getEndpointURI());
    }

    public void doDisconnect() throws Exception
    {
        server.stop();
    }

    public void doStart() throws MuleException
    {
        // nothing to do
    }

    public void doStop() throws MuleException
    {
        // nothing to do
    }

    public Server getServer()
    {
        return server;
    }

    public boolean isBridge()
    {
        return bridge;
    }

}
