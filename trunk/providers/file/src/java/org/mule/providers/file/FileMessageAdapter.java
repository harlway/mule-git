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
 *
 */

package org.mule.providers.file;

import org.apache.commons.lang.ObjectUtils;
import org.mule.MuleRuntimeException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;
import org.mule.providers.AbstractMessageAdapter;
import org.mule.providers.file.transformers.FileToByteArray;
import org.mule.umo.MessagingException;
import org.mule.umo.provider.MessageTypeNotSupportedException;
import org.mule.umo.provider.UniqueIdNotSupportedException;

import java.io.File;

/**
 * <code>FileMessageAdapter</code> provides a wrapper for a fiel reference.
 * Users can obtain the contents of the message through the payload property and
 * can get the filename and directory in the properties using PROPERTY_FILENAME
 * and PROPERTY_DIRECTORY.
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class FileMessageAdapter extends AbstractMessageAdapter
{
    private FileToByteArray transformer = new FileToByteArray();

    private File file = null;
    private byte[] contents = null;

    public FileMessageAdapter(Object message) throws MessagingException
    {
        super();

        if (message instanceof File) {
            this.setMessage((File)message);
        }
        else {
            throw new MessageTypeNotSupportedException(message, this.getClass());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.UMOMessageAdapter#getPayload()
     */
    public Object getPayload()
    {
        return file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.UMOMessageAdapter#getPayloadAsBytes()
     */
    public byte[] getPayloadAsBytes() throws Exception
    {
        synchronized (this) {
            if (contents == null) {
                try {
                    // TODO to cache or not to cache, that is the question?
                    this.contents = (byte[])transformer.transform(file);
                }
                catch (Exception noPayloadException) {
                    throw new MuleRuntimeException(new Message(Messages.FAILED_TO_READ_PAYLOAD),
                            noPayloadException);
                }
            }
            return contents;
        }
    }

    /**
     * Converts the message implementation into a String representation
     * 
     * @param encoding
     *            The encoding to use when transforming the message (if
     *            necessary). The parameter is used when converting from a byte
     *            array
     * @return String representation of the message payload
     * @throws Exception
     *             Implementation may throw an endpoint specific exception
     */
    public String getPayloadAsString(String encoding) throws Exception
    {
        synchronized (this) {
            return new String(this.getPayloadAsBytes(), encoding);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.providers.UMOMessageAdapter#setMessage(java.lang.Object)
     */
    protected void setMessage(File message) throws MessagingException
    {
        boolean fileIsValid;
        Exception fileInvalidException;

        try {
            fileIsValid = (message != null && message.isFile());
            fileInvalidException = null;
        }
        catch (Exception ex) {
            // save any file access exceptions
            fileInvalidException = ex;
            fileIsValid = false;
        }

        if (!fileIsValid) {
            Object exceptionArg;

            if (fileInvalidException != null) {
                exceptionArg = fileInvalidException;
            }
            else {
                exceptionArg = ObjectUtils.toString(message, "null");
            }

            Message msg = new Message(Messages.FILE_X_DOES_NOT_EXIST, ObjectUtils.toString(message,
                    "null"));

            throw new MessagingException(msg, exceptionArg);
        }

        this.file = message;
        properties.put(FileConnector.PROPERTY_ORIGINAL_FILENAME, this.file.getName());
        properties.put(FileConnector.PROPERTY_DIRECTORY, this.file.getParent());
    }

    public String getUniqueId() throws UniqueIdNotSupportedException
    {
        return file.getAbsolutePath();
    }

}
