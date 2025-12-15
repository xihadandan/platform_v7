package org.bouncycastle2.jcajce.provider.symmetric.util;

import org.bouncycastle2.crypto.BlockCipher;

public interface BlockCipherProvider
{
    BlockCipher get();
}
