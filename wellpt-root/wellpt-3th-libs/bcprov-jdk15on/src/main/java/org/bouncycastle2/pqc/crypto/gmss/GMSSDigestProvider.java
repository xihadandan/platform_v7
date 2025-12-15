package org.bouncycastle2.pqc.crypto.gmss;

import org.bouncycastle2.crypto.Digest;

public interface GMSSDigestProvider
{
    Digest get();
}
