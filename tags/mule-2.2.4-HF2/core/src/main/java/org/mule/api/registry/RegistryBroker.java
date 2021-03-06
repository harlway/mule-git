/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.api.registry;

/**
 * A RegistryBroker delegates calls to a collection of Registries.
 */
public interface RegistryBroker extends Registry
{
    void addRegistry(long id, Registry registry);

    void removeRegistry(long id);
    
    void addRegistry(Registry registry);

    void removeRegistry(Registry registry);
}


