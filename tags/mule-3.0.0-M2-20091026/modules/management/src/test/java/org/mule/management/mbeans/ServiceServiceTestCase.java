/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.management.mbeans;

import org.mule.api.config.ThreadingProfile;
import org.mule.component.DefaultJavaComponent;
import org.mule.management.AbstractMuleJmxTestCase;
import org.mule.model.seda.SedaModel;
import org.mule.model.seda.SedaService;
import org.mule.module.management.mbean.ServiceService;
import org.mule.object.SingletonObjectFactory;

import java.util.Set;

import javax.management.ObjectName;

public class ServiceServiceTestCase extends AbstractMuleJmxTestCase
{
    public void testUndeploy() throws Exception
    {
        final String domainOriginal = "TEST_DOMAIN_1";

        final SedaService service = new SedaService();
        service.setName("TEST_SERVICE");
        final DefaultJavaComponent component = new DefaultJavaComponent(new SingletonObjectFactory(Object.class));
        component.setMuleContext(muleContext);
        service.setComponent(component);
        service.setMuleContext(muleContext);

        service.setThreadingProfile(ThreadingProfile.DEFAULT_THREADING_PROFILE);
        SedaModel model = new SedaModel();
        model.setMuleContext(muleContext);
        service.setModel(model);
        muleContext.getRegistry().registerModel(model);
        muleContext.getRegistry().registerService(service);
        muleContext.start();

        final ServiceService jmxService = new ServiceService("TEST_SERVICE", muleContext);
        final ObjectName name = ObjectName.getInstance(domainOriginal + ":type=TEST_SERVICE");
        mBeanServer.registerMBean(jmxService, name);
        Set mbeans = mBeanServer.queryMBeans(ObjectName.getInstance(domainOriginal + ":*"), null);

        // Expecting following mbeans to be registered:
        // 1) org.mule.management.mbeans.ServiceService@TEST_DOMAIN_1:type=TEST_SERVICE
        // 2) org.mule.management.mbeans.ServiceStats@TEST_DOMAIN_1:type=org.mule.Statistics,service=TEST_SERVICE
        // 3) org.mule.management.mbeans.RouterStats@TEST_DOMAIN_1:type=org.mule.Statistics,service=TEST_SERVICE,router=inbound
        // 4) org.mule.management.mbeans.RouterStats@TEST_DOMAIN_1:type=org.mule.Statistics,service=TEST_SERVICE,router=outbound
        assertEquals("Unexpected number of components registered in the domain.", 4, mbeans.size());

        mBeanServer.unregisterMBean(name);

        mbeans = mBeanServer.queryMBeans(ObjectName.getInstance(domainOriginal + ":*"), null);

        assertEquals("There should be no MBeans left in the domain", 0, mbeans.size());
    }
}
