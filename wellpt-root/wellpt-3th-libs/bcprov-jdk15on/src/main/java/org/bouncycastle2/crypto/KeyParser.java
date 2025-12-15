package org.bouncycastle2.crypto;

import java.io.IOException;
import java.io.InputStream;

import org.bouncycastle2.crypto.params.AsymmetricKeyParameter;

public interface KeyParser
{
    AsymmetricKeyParameter readKey(InputStream stream)
        throws IOException;
}
