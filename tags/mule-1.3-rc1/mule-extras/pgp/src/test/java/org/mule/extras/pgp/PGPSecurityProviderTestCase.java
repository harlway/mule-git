/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.extras.pgp;

import cryptix.message.Message;
import cryptix.message.MessageFactory;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.umo.security.UMOAuthentication;

import java.io.FileInputStream;
import java.net.URL;

/**
 * @author ariva
 * 
 */
public class PGPSecurityProviderTestCase extends AbstractMuleTestCase
{
    private PGPSecurityProvider securityProvider;

    private Message message;

    protected void doSetUp() throws Exception
    {
        PGPKeyRingImpl keyM = new PGPKeyRingImpl();
        URL url;

        url = Thread.currentThread().getContextClassLoader().getResource("./serverPublic.gpg");
        keyM.setPublicKeyRingFileName(url.getFile());

        url = Thread.currentThread().getContextClassLoader().getResource("./serverPrivate.gpg");
        keyM.setSecretKeyRingFileName(url.getFile());

        keyM.setSecretAliasId("0x6168F39C");
        keyM.setSecretPassphrase("TestingPassphrase");
        keyM.initialise();

        securityProvider = new PGPSecurityProvider();
        securityProvider.setKeyManager(keyM);

        securityProvider.initialise();

        MessageFactory mf = MessageFactory.getInstance("OpenPGP");

        url = Thread.currentThread().getContextClassLoader().getResource("./signed.asc");

        FileInputStream in = new FileInputStream(url.getFile());

        message = (Message) mf.generateMessages(in).iterator().next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.tck.AbstractMuleTestCase#tearDown()
     */
    protected void doTearDown() throws Exception
    {
        securityProvider = null;
        message = null;
    }

    public void testAuthenticate()
    {
        try {
            UMOAuthentication auth = new PGPAuthentication("Mule client <mule_client@mule.com>", message);

            auth = securityProvider.authenticate(auth);

            assertTrue(auth.isAuthenticated());
            assertNotNull(auth.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
