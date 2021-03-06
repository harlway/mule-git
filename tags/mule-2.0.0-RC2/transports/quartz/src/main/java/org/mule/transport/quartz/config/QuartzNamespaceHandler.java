/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.quartz.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.collection.ChildMapDefinitionParser;
import org.mule.config.spring.parsers.collection.ChildSingletonMapDefinitionParser;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.endpoint.URIBuilder;
import org.mule.transport.quartz.QuartzConnector;

/**
 * Registers Bean Definition Parsers for the "quartz" namespace
 */
public class QuartzNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
        registerStandardTransportEndpoints(QuartzConnector.QUARTZ, URIBuilder.PATH_ATTRIBUTES);
        registerMuleBeanDefinitionParser("connector", new MuleOrphanDefinitionParser(QuartzConnector.class, true)).addAlias("scheduler", "quartzScheduler");
        // note that we use the singular (factoryProperty) for the setter so that we auto-detect a collection
        registerBeanDefinitionParser("factory-property", new ChildSingletonMapDefinitionParser("factoryProperty"));
        registerBeanDefinitionParser("factory-properties", new ChildMapDefinitionParser("factoryProperty"));
    }

}
