package org.bouncycastle2.math.ec;

public interface ECLookupTable
{
    int getSize();
    ECPoint lookup(int index);
}
