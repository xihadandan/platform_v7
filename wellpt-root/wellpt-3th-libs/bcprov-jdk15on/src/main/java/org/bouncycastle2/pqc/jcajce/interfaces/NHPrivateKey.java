package org.bouncycastle2.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface NHPrivateKey
    extends NHKey, PrivateKey
{
    short[] getSecretData();
}
