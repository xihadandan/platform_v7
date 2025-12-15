package org.bouncycastle2.crypto.ec;

import org.bouncycastle2.crypto.CipherParameters;
import org.bouncycastle2.math.ec.ECPoint;

public interface ECDecryptor
{
    void init(CipherParameters params);

    ECPoint decrypt(ECPair cipherText);
}
