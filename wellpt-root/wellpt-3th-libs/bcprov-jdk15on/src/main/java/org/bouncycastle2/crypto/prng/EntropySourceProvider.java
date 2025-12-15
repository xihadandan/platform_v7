package org.bouncycastle2.crypto.prng;

public interface EntropySourceProvider
{
    EntropySource get(final int bitsRequired);
}
