/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.impl.lifecycle.phases;

import org.mule.impl.internal.notifications.ManagerNotification;
import org.mule.impl.lifecycle.LifecyclePhase;
import org.mule.impl.lifecycle.NotificationLifecycleObject;
import org.mule.registry.Registry;
import org.mule.umo.UMOManagementContext;
import org.mule.umo.lifecycle.Disposable;
import org.mule.umo.lifecycle.UMOLifecyclePhase;
import org.mule.umo.manager.UMOAgent;
import org.mule.umo.model.UMOModel;
import org.mule.umo.provider.UMOConnector;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * TODO
 */
public class TransientRegistryDisposePhase extends LifecyclePhase
{
    public TransientRegistryDisposePhase()
    {
        this(new Class[]{Registry.class});
    }

    public TransientRegistryDisposePhase(Class[] ignorredObjects)
    {
        super(Disposable.PHASE_NAME, Disposable.class);

        Set disposeOrderedObjects = new LinkedHashSet();
//        disposeOrderedObjects.add(new NotificationLifecycleObject(UMOManagementContext.class, ManagerNotification.class,
//                ManagerNotification.getActionName(ManagerNotification.MANAGER_DISPOSING),
//                ManagerNotification.getActionName(ManagerNotification.MANAGER_DISPOSED)));
        disposeOrderedObjects.add(new NotificationLifecycleObject(UMOManagementContext.class));
        disposeOrderedObjects.add(new NotificationLifecycleObject(UMOConnector.class, ManagerNotification.class,
                ManagerNotification.getActionName(ManagerNotification.MANAGER_DISPOSING_CONNECTORS),
                ManagerNotification.getActionName(ManagerNotification.MANAGER_DISPOSED_CONNECTORS)));
        disposeOrderedObjects.add(new NotificationLifecycleObject(UMOAgent.class));
        disposeOrderedObjects.add(new NotificationLifecycleObject(UMOModel.class));
        disposeOrderedObjects.add(new NotificationLifecycleObject(Disposable.class));

        setIgnorredObjectTypes(ignorredObjects);
        setOrderedLifecycleObjects(disposeOrderedObjects);
        registerSupportedPhase(UMOLifecyclePhase.ALL_PHASES);
    }
}
