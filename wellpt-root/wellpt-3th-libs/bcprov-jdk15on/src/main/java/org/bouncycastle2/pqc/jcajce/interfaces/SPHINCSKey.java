package org.bouncycastle2.pqc.jcajce.interfaces;

import java.security.Key;

public interface SPHINCSKey
    extends Key
{
    byte[] getKeyData();
}
