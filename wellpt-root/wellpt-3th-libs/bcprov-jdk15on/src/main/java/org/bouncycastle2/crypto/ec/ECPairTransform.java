package org.bouncycastle2.crypto.ec;

import org.bouncycastle2.crypto.CipherParameters;

public interface ECPairTransform
{
    void init(CipherParameters params);

    ECPair transform(ECPair cipherText);
}
