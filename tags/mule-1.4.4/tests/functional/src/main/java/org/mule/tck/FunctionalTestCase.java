/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck;

import org.mule.MuleManager;
import org.mule.config.ConfigurationBuilder;
import org.mule.util.ClassUtils;

/**
 * Is a base tast case for tests that initialise Mule using a configuration file. The
 * default configuration builder used is the MuleXmlConfigurationBuilder. This you
 * need to have the mule-modules-builders module/jar on your classpath. If you want
 * to use a different builder, just overload the <code>getBuilder()</code> method
 * of this class to return the type of builder you want to use with your test. Note
 * you can overload the <code>getBuilder()</code> to return an initialised instance
 * of the QuickConfiguratonBuilder, this allows the developer to programmatically
 * build a Mule instance and roves the need for additional config files for the test.
 */
public abstract class FunctionalTestCase extends AbstractMuleTestCase
{

    public static final String DEFAULT_BUILDER_CLASS = "org.mule.config.builders.MuleXmlConfigurationBuilder";

    protected final void doSetUp() throws Exception
    {
        doPreFunctionalSetUp();
        // Should we set up te manager for every method?
        if (!getTestInfo().isDisposeManagerPerSuite())
        {
            setupManager();
        }
        doPostFunctionalSetUp();
    }

    protected void suitePreSetUp() throws Exception
    {
        if (getTestInfo().isDisposeManagerPerSuite())
        {
            setupManager();
        }
    }

    protected void setupManager() throws Exception
    {
        MuleManager.getConfiguration().setWorkListener(new TestingWorkListener());
        ConfigurationBuilder builder = getBuilder();
        builder.configure(getConfigResources(), null);
    }

    protected final void doTearDown() throws Exception
    {
        doFunctionalTearDown();
    }

    protected ConfigurationBuilder getBuilder() throws Exception
    {

        try
        {
            Class builderClass = ClassUtils.loadClass(DEFAULT_BUILDER_CLASS, getClass());
            return (ConfigurationBuilder)builderClass.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new ClassNotFoundException(
                "The builder "
                                + DEFAULT_BUILDER_CLASS
                                + " is not on your classpath and "
                                + "the getBuilder() method of this class has not been overloaded to return a different builder. Please "
                                + "check your functional test.", e);
        }

    }

    protected void doPreFunctionalSetUp() throws Exception
    {
        // template method
    }

    protected void doPostFunctionalSetUp() throws Exception
    {
        // template method
    }

    protected void doFunctionalTearDown() throws Exception
    {
        // template method
    }

    protected abstract String getConfigResources();
}
