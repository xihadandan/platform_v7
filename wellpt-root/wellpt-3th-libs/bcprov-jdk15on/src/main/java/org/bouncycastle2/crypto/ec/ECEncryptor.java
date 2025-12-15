package org.bouncycastle2.crypto.ec;

import org.bouncycastle2.crypto.CipherParameters;
import org.bouncycastle2.math.ec.ECPoint;

public interface ECEncryptor
{
    void init(CipherParameters params);

    ECPair encrypt(ECPoint point);
}
