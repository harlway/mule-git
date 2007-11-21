/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.impl.lifecycle;

import org.mule.impl.SystemInfo;
import org.mule.impl.internal.notifications.ManagerNotification;
import org.mule.impl.internal.notifications.ServerNotificationManager;
import org.mule.umo.manager.UMOServerNotification;

/**
 * TODO
 */
public class ManagerLifecycleObject extends NotificationLifecycleObject
{
    private Class notificationClass;
    private SystemInfo sysInfo;

    public ManagerLifecycleObject(Class type, Class notificationClass, SystemInfo sysInfo, ServerNotificationManager notificationManager)
    {
        super(type, notificationManager);
        this.notificationClass = notificationClass;
        this.sysInfo = sysInfo;
    }

    //@Override
    protected UMOServerNotification createNotification(String action)
    {
        //return ClassUtils.instanciateClass(notificationClass, new Object[]{context, action});

        return new ManagerNotification(sysInfo, action);
    }
}
