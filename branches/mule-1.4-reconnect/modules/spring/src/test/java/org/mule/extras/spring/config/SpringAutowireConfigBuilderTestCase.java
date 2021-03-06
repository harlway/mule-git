/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extras.spring.config;

import org.mule.MuleManager;
import org.mule.config.ConfigurationBuilder;
import org.mule.impl.container.ContainerKeyPair;
import org.mule.tck.AbstractConfigBuilderTestCase;
import org.mule.tck.testmodels.fruit.FruitBowl;
import org.mule.umo.manager.UMOManager;

/**
 * Tests the Mule-to-Spring XSLT transformation with Autowiring for Mule.
 */
public class SpringAutowireConfigBuilderTestCase extends AbstractConfigBuilderTestCase
{

    public String getConfigResources()
    {
        return "test-mule-autowire-app-context.xml,test-application-context.xml";
    }

    // @Override
    public ConfigurationBuilder getBuilder()
    {
        return new SpringConfigurationBuilder();
    }

    public void testComponentResolverConfig() throws Exception
    {
        // test container init
        UMOManager manager = MuleManager.getInstance();
        assertNotNull(manager.getContainerContext());

        Object object = manager.getContainerContext().getComponent(
            new ContainerKeyPair("spring", "fruitBowl"));
        assertNotNull(object);
        assertTrue(object instanceof FruitBowl);
        FruitBowl bowl = (FruitBowl)object;
        assertTrue(bowl.hasBanana());
        assertTrue(bowl.hasApple());
    }

}
