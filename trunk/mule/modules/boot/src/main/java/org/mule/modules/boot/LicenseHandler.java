/*
 * $Id $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.boot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.mule.util.ClassUtils;

/**
 * This class has methods for displaying the EULA and saving the
 * license acceptance acknowledgment.
 */
public class LicenseHandler 
{
    private File muleHome;
    private File muleBase;
    private static int maxRowsToDisplay = 80;

    public static String defaultLicenseType = "MuleSource Public License";
    public static String defaultLicenseVersion = "UNKNOWN";
    public static String ackJarName = "mule-local-install.jar";
    public static String ackLicenseName = "META-INF/mule/LICENSE.txt";
    public static String ackFileName = "META-INF/mule/license.props";
    public static String licenseFileName = "LICENSE.txt";

    /**
     * Constructor for when we only know MULE_HOME
     * This is the constructor that the GUI installer should use.
     *
     * @param muleHome File pointing to MULE_HOME
     */
    public LicenseHandler(File muleHome)
    {
        this.muleHome = muleHome;
        this.muleBase = muleHome;
    }

    /**
     * Constructor for when we know both MULE_HOME and MULE_BASE
     * This is the constructor used by MuleBootstrap
     *
     * @param muleHome File pointing to MULE_HOME
     * @param muleBase File pointing to MULE_BASE
     */
    public LicenseHandler(File muleHome, File muleBase)
    {
        this.muleHome = muleHome;
        this.muleBase = muleBase;
    }

    /**
     * Display the EULA and get the user's acceptance. Note that only
     * a missing license file or a non-yes answer will cause this to 
     * return false. If the user accepts, but we can't write the license
     * ack file for some reason, we'll still return true.
     *
     * @return boolean whether the license was accepted or not
     */
    public boolean getAcceptance() 
    {
        String licenseType = "";
        String licenseVersion = "";

        try {
            File licenseFile = new File(muleHome, licenseFileName);
            File muleLib = new File(muleHome, "lib/mule");

            // Make sure the license file exists
            // Also make sure that MULE_HOME/lib/mule exists - it
            // should, of course, but it doesn't hurt to check since
            // we'll need that directory later

            if (!licenseFile.exists() || !muleLib.exists())
            {
                System.out.println("\nYour Mule installation seems to be incomplete. Please try downloading it again from http://mule.mulesource.org/wiki/display/MULE/Download and start again.");
                return false;
            }

            System.out.println("\n\nPlease read over the following license agreement carefully:\n\n");

            int row = 1;
            String input = "";

    	    BufferedReader is = new BufferedReader(
		    new InputStreamReader(System.in));
            BufferedReader br = new BufferedReader(
                    new FileReader(licenseFile));

            while (br.ready())
            {
                String line = br.readLine();

                if (row == 1) licenseType = line;
                if (row == 2 && line.startsWith("Version "))
                    licenseVersion = line.substring(8);

                if ((row % maxRowsToDisplay ) == 0)
                {
                    System.out.print("\nHit return to continue ... ");
    		    input = is.readLine();
                }

                System.out.println(line);
                row++;
            }


            System.out.print("\n\nDo you accept the terms and conditions of this license agreement [y/n]?");
            input = is.readLine();

            if (!input.toLowerCase().startsWith("y"))
            {
                System.out.println("\nSorry, until you accept the terms and conditions of this EULA, you won't be able to start Mule");
                return false;
            }
        } 
        catch (Exception e) 
        {
            System.out.println("\nSorry, we encountered an error in processing your license agreement - please try again");
            return false;
        }

        if (licenseType.equals("")) licenseType = defaultLicenseType;
        if (licenseVersion.equals("")) licenseVersion = defaultLicenseVersion;

        try
        {
            saveLicenseAck(licenseType, licenseVersion);
        }
        catch (Exception e) 
        {
            System.out.println(e);
        }

        return true;
    }

    /**
     * Saves the license acceptance acknowledgement file. This method
     * should be used by the GUI installer.
     *
     * For now, the acknowlegment file is license.props and contains:
     *    LicenseVersion:
     *    LicenseAcceptanceDate:
     *    LicenseType:
     *
     * We are also making a copy of the license file into the jar as well
     *
     * The logic here is:
     *
     * 1. Normally, save the license ack in a jar
     * in MULE_HOME/lib/mule called "mule-local-install.jar"
     *
     * 2. If MULE_HOME/lib/mule is not writable AND 
     * MULE_BASE != MULE_HOME, try to save in MULE_BASE/lib/user
     *
     * 3. If MULE_BASE/lib/user is not writable, something is
     * probably strange and we need a third option ... which is ...
     * well, I'm sure there is one ...
     *
     * @param licenseType type of license - for now, should be just MuleSource Public License
     * @param licenseVersion version of license - for now, should be 1.1.3
     *
     * @throws Exception if there is nowhere to write the file or somehow the jar creation fails (disk full, etc.)
     */
    public void saveLicenseAck(String licenseType, String licenseVersion) throws Exception
    {
        if (ClassUtils.getResource("META-INF/mule/license.props", MuleBootstrap.class) != null) {
            // We already have the license.props file from somewhere, so
            // forget about saving the jar file
            // 
            // This case should ONLY be for when the GUI installer saved the
            // license.props already, and forgot to check for its existence
            // prior to calling this method.
            return;
        }

        File muleLib = new File(muleHome, "lib/mule");
        File tempJar = createAckJarFile(licenseType, licenseVersion);

        if (!muleLib.canWrite()) {
            // If we can't write to MULE_HOME/lib/mule, try MULE_BASE/lib/user
            if (!muleHome.getCanonicalFile().equals(muleBase.getCanonicalFile())) {
                muleLib = new File(muleBase, "lib/user");

                if (!muleLib.canWrite())
                    throw new Exception("No write permissions for " + ackJarName + " in either MULE_HOME or MULE_BASE");

            } else {
                throw new Exception("No write permission for " + ackJarName);
            }
        }

        // Now we have a directory to create the jar to, so let's rename 
        // the temporary one
        File newJarFile = new File(muleLib, ackJarName);
        if (newJarFile.exists())
            throw new Exception("Unable to rename temporary jar to " + newJarFile.getAbsolutePath() + " a file with this name already exists!");

        if (!tempJar.renameTo(newJarFile))
            throw new Exception("Unable to rename temporary jar to " + newJarFile.getAbsolutePath());
    }

    /**
     * This method will create a temporary jar file with the
     * license ack file. It will either return the File or throw
     * an Exception
     *
     * @throws Exception
     */
    private File createAckJarFile(String licenseType, String licenseVersion) throws Exception
    {
        File tempJar = File.createTempFile(ackJarName, null);
        File licenseFile = new File(muleHome, licenseFileName);
        JarOutputStream newJar = null;

        String ackData = "LicenseType=" + licenseType + "\n";
        ackData += "LicenseVersion=" + licenseVersion + "\n";
        ackData += "LicenseDate=" + (new java.util.Date()).toString() + "\n";

        try {
            newJar = new JarOutputStream(new FileOutputStream(tempJar));
        } catch (IOException ioe) {
            throw new Exception("Unable to create temporary jar file");
        }

        byte buffer[] = new byte[1024];
        int bytesRead;
        FileInputStream fis = null;

        try 
        {
            fis = new FileInputStream(licenseFile);

            JarEntry entry = new JarEntry(ackFileName);
            newJar.putNextEntry(entry);
            newJar.write(ackData.getBytes(), 0, ackData.getBytes().length);

            entry = new JarEntry(ackLicenseName);
            newJar.putNextEntry(entry);

            while ((bytesRead = fis.read(buffer)) != -1)
            {
                newJar.write(buffer, 0, bytesRead);
            }

        } catch (IOException ioe) {
            throw new Exception("Unable to write " + ackFileName + 
                   " to temporary jar file");
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (Exception e) { }
            }
        }
        
        try {
            newJar.close();
        } catch (IOException ioe) {
            throw new Exception("Unable to close temporary jar file");
        }

        return tempJar;
    }

}

