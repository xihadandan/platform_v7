package org.bouncycastle2.jcajce.util;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle2.jce.provider.BouncyCastleProvider;

/**
 * A JCA/JCE helper that refers to the BC provider for all it's needs.
 */
public class BCJcaJceHelper
    extends ProviderJcaJceHelper
{
    private static volatile Provider bcProvider;

    private static Provider getBouncyCastleProvider()
    {
        if (Security.getProvider("BC") != null)
        {
            return Security.getProvider("BC");
        }
        else if (bcProvider != null)
        {
            return bcProvider;
        }
        else
        {
            bcProvider = new BouncyCastleProvider();

            return bcProvider;
        }
    }

    public BCJcaJceHelper()
    {
        super(getBouncyCastleProvider());
    }
}
