/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.registry;

import java.util.Properties;


/**
 * <code>ServiceFinder</code> can be used as a hook into the service lookup process to 
 * return the correct Service Descriptor for a given service name. By default a service 
 * is looked up directly, however a generic service name might be used where the real 
 * service implementation will depend on other modules/resources being available.
 * For example, in the case of a SOAP connector the finder could check the classpath for 
 * Axis or XFire and return the correct descriptor.
 */
public interface ServiceFinder
{
    String findService(String service, ServiceDescriptor descriptor, Properties props) throws ServiceException;
}


