package org.bouncycastle2.crypto.tls;

import java.io.ByteArrayOutputStream;

import org.bouncycastle2.crypto.Digest;

class DigestInputBuffer extends ByteArrayOutputStream
{
    void updateDigest(Digest d)
    {
        d.update(this.buf, 0, count);
    }
}
