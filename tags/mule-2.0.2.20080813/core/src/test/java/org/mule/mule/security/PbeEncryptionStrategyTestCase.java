/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.mule.security;

import org.mule.security.PasswordBasedEncryptionStrategy;
import org.mule.tck.AbstractMuleTestCase;

public class PbeEncryptionStrategyTestCase extends AbstractMuleTestCase
{
    public void testRoundTripEncryption() throws Exception
    {
        PasswordBasedEncryptionStrategy pbe = new PasswordBasedEncryptionStrategy();
        pbe.setPassword("test");
        pbe.initialise();

        byte[] b = pbe.encrypt("hello".getBytes(), null);

        assertNotSame(new String(b), "hello");
        String s = new String(pbe.decrypt(b, null), "UTF-8");
        assertEquals("hello", s);
    }
}
