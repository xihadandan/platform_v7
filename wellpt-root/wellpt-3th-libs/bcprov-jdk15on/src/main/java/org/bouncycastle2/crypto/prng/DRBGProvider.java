package org.bouncycastle2.crypto.prng;

import org.bouncycastle2.crypto.prng.drbg.SP80090DRBG;

interface DRBGProvider
{
    SP80090DRBG get(EntropySource entropySource);
}
